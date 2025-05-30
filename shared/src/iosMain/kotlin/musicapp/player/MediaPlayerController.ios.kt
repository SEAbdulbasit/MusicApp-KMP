package musicapp.player

import kotlinx.cinterop.*
import musicapp.utils.PlatformContext
import platform.AVFAudio.*
import platform.AVFoundation.*
import platform.CoreMedia.CMTime
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMake
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.*
import platform.MediaPlayer.*
import platform.UIKit.UIImage
import platform.darwin.NSEC_PER_SEC
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalForeignApi::class)
actual class MediaPlayerController actual constructor(val platformContext: PlatformContext) {

    private var timeObserver: Any? = null
    private var notificationObserver: Any? = null
    private val player: AVPlayer = AVPlayer()
    private var listener: MediaPlayerListener? = null
    private var currentTrack: TrackItem? = null

    private var trackList: List<TrackItem> = emptyList()
    private var currentTrackIndex: Int = -1

    actual fun setTrackList(trackList: List<TrackItem>, currentTrackId: String) {
        this.trackList = trackList
        this.currentTrackIndex = trackList.indexOfFirst { it.id == currentTrackId }.takeIf { it >= 0 } ?: 0
    }

    actual fun playNextTrack(): Boolean {
        if (trackList.isEmpty() || currentTrackIndex < 0) {
            return false
        }

        val nextIndex = currentTrackIndex + 1
        if (nextIndex >= trackList.size) {
            return false
        }

        currentTrackIndex = nextIndex
        val nextTrack = trackList[nextIndex]

        listener?.onTrackChanged(nextTrack.id)

        prepare(nextTrack, listener ?: return false)
        return true
    }

    actual fun playPreviousTrack(): Boolean {
        if (trackList.isEmpty() || currentTrackIndex <= 0) {
            return false
        }

        val previousIndex = currentTrackIndex - 1
        currentTrackIndex = previousIndex
        val previousTrack = trackList[previousIndex]

        listener?.onTrackChanged(previousTrack.id)

        prepare(previousTrack, listener ?: return false)
        return true
    }

    actual fun getCurrentTrack(): TrackItem? {
        currentTrack?.let { return it }

        if (trackList.isEmpty() || currentTrackIndex < 0 || currentTrackIndex >= trackList.size) {
            return null
        }
        return trackList[currentTrackIndex]
    }

    private val nowPlayingInfoCenter = MPNowPlayingInfoCenter.defaultCenter()
    private val remoteCommandCenter = MPRemoteCommandCenter.sharedCommandCenter()
    val nowPlayingInfo = mutableMapOf<String, Any>()

    init {
        setUpAudioSession()
        setupRemoteCommands()
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    private fun setUpAudioSession() {
        memScoped {
            val audioSession = AVAudioSession.sharedInstance()
            val errorPtr = alloc<ObjCObjectVar<NSError?>>()

            val options = AVAudioSessionCategoryOptionAllowBluetooth or
                    AVAudioSessionCategoryOptionAllowAirPlay or
                    AVAudioSessionCategoryOptionAllowBluetoothA2DP or
                    AVAudioSessionCategoryOptionDefaultToSpeaker

            if (!audioSession.setCategory(
                    category = AVAudioSessionCategoryPlayback,
                    mode = AVAudioSessionModeDefault,
                    options = options,
                    error = errorPtr.ptr
                )
            ) {
                errorPtr.value?.let { error ->
                    println("Error setting audio session category: ${error.localizedDescription}")
                }
                return@memScoped
            }

            if (!audioSession.setActive(active = true, withOptions = 0u, error = errorPtr.ptr)) {
                errorPtr.value?.let { error ->
                    println("Error activating audio session: ${error.localizedDescription}")
                }
            }
            NSNotificationCenter.defaultCenter().addObserverForName(
                name = "AVAudioSessionInterruptionNotification",
                `object` = audioSession,
                queue = NSOperationQueue.mainQueue(),
                usingBlock = { notification: NSNotification? ->
                    notification?.userInfo?.let { userInfo ->
                        val interruptionType = userInfo[AVAudioSessionInterruptionTypeKey] as? NSNumber
                        val typeValue = interruptionType?.unsignedLongValue
                        when (typeValue) {
                            AVAudioSessionInterruptionTypeBegan.toULong() -> pause()
                            AVAudioSessionInterruptionTypeEnded.toULong() -> {
                                val options = userInfo[AVAudioSessionInterruptionOptionKey] as? NSNumber
                                if (options?.unsignedLongValue == AVAudioSessionInterruptionOptionShouldResume.toULong()) {
                                    start()
                                }
                            }
                        }
                    }
                }
            )
        }
    }

    private fun setupRemoteCommands() {
        remoteCommandCenter.playCommand.setEnabled(true)
        remoteCommandCenter.playCommand.addTargetWithHandler { event: MPRemoteCommandEvent? ->
            if (!isPlaying()) {
                start()
                MPRemoteCommandHandlerStatusSuccess
            } else {
                MPRemoteCommandHandlerStatusCommandFailed
            }
        }

        remoteCommandCenter.pauseCommand.setEnabled(true)
        remoteCommandCenter.pauseCommand.addTargetWithHandler { event: MPRemoteCommandEvent? ->
            if (isPlaying()) {
                pause()
                MPRemoteCommandHandlerStatusSuccess
            } else {
                MPRemoteCommandHandlerStatusCommandFailed
            }
        }

        remoteCommandCenter.togglePlayPauseCommand.setEnabled(true)
        remoteCommandCenter.togglePlayPauseCommand.addTargetWithHandler { event: MPRemoteCommandEvent? ->
            if (isPlaying()) pause() else start()
            MPRemoteCommandHandlerStatusSuccess
        }

        remoteCommandCenter.skipForwardCommand.setEnabled(true)
        remoteCommandCenter.skipForwardCommand.preferredIntervals = NSArray.arrayWithObject(NSNumber(double = 15.0))
        remoteCommandCenter.skipForwardCommand.addTargetWithHandler { event: MPRemoteCommandEvent? ->
            val seconds = (event as? MPSkipIntervalCommandEvent)?.interval ?: 15.0
            val current = getCurrentPosition()?.toDouble()?.div(1000) ?: 0.0
            seekTo(((current + seconds) * 1000).toLong())
            MPRemoteCommandHandlerStatusSuccess
        }

        remoteCommandCenter.skipBackwardCommand.setEnabled(true)
        remoteCommandCenter.skipBackwardCommand.preferredIntervals = NSArray.arrayWithObject(NSNumber(double = 15.0))
        remoteCommandCenter.skipBackwardCommand.addTargetWithHandler { event: MPRemoteCommandEvent? ->
            val seconds = (event as? MPSkipIntervalCommandEvent)?.interval ?: 15.0
            val current = getCurrentPosition()?.toDouble()?.div(1000) ?: 0.0
            seekTo(((current - seconds).coerceAtLeast(0.0) * 1000).toLong())
            MPRemoteCommandHandlerStatusSuccess
        }

        remoteCommandCenter.nextTrackCommand.setEnabled(true)
        remoteCommandCenter.nextTrackCommand.addTargetWithHandler { event: MPRemoteCommandEvent? ->
            if (playNextTrack()) {
                MPRemoteCommandHandlerStatusSuccess
            } else {
                MPRemoteCommandHandlerStatusCommandFailed
            }
        }

        remoteCommandCenter.previousTrackCommand.setEnabled(true)
        remoteCommandCenter.previousTrackCommand.addTargetWithHandler { event: MPRemoteCommandEvent? ->
            if (playPreviousTrack()) {
                MPRemoteCommandHandlerStatusSuccess
            } else {
                MPRemoteCommandHandlerStatusCommandFailed
            }
        }
    }

    private fun updateNowPlayingInfo(track: TrackItem) {
        nowPlayingInfo.apply {
            this[MPMediaItemPropertyTitle] = track.title
            this[MPMediaItemPropertyArtist] = track.artist
            this[MPMediaItemPropertyAlbumTitle] = track.artist
            this[MPMediaItemPropertyPlaybackDuration] = getDuration()?.toDouble()?.div(1000) ?: 0.0
            this[MPNowPlayingInfoPropertyElapsedPlaybackTime] = getCurrentPosition()?.toDouble()?.div(1000) ?: 0.0
            this[MPNowPlayingInfoPropertyPlaybackRate] = if (isPlaying()) 1.0 else 0.0
        }

        UIImage.imageNamed("AppIcon")?.let { placeholderImage ->
            nowPlayingInfo[MPMediaItemPropertyArtwork] =
                MPMediaItemArtwork(boundsSize = placeholderImage.size) { _ -> placeholderImage }
        }

        nowPlayingInfoCenter.nowPlayingInfo = nowPlayingInfo.toMap()

        loadAlbumArtwork(track.albumImageUrl)
    }

    private fun loadAlbumArtwork(urlString: String) {
        NSURL.URLWithString(urlString)?.let { url ->
            NSURLSession.sharedSession().dataTaskWithURL(url) { data, _, error ->
                if (error == null && data != null) {
                    UIImage.imageWithData(data)?.let { image ->
                        nowPlayingInfo[MPMediaItemPropertyArtwork] =
                            MPMediaItemArtwork(boundsSize = image.size) { _ -> image }

                        NSOperationQueue.mainQueue().addOperationWithBlock {
                            nowPlayingInfoCenter.nowPlayingInfo = nowPlayingInfo.toMap()
                        }
                    }
                }
            }.resume()
        }
    }

    actual fun prepare(mediaItem: TrackItem, listener: MediaPlayerListener) {
        this.listener = listener
        this.currentTrack = mediaItem

        if (trackList.isNotEmpty()) {
            val index = trackList.indexOfFirst { it.id == mediaItem.id }
            if (index >= 0) {
                currentTrackIndex = index
            }
        }

        // Use pathSource for the audio URL instead of albumImageUrl
        val url = NSURL.URLWithString(mediaItem.pathSource)
        if (url == null) {
            listener.onError()
            return
        }

        ensureAudioSessionActive()

        player.pause()

        if (timeObserver != null) {
            player.removeTimeObserver(timeObserver!!)
            timeObserver = null
        }

        player.replaceCurrentItemWithPlayerItem(AVPlayerItem(uRL = url))

        startTimeObserver()

        player.play()

        listener.onBufferingStateChanged(true)
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    private fun ensureAudioSessionActive() {
        memScoped {
            val audioSession = AVAudioSession.sharedInstance()
            val errorPtr = alloc<ObjCObjectVar<NSError?>>()

            audioSession.setActive(active = true, withOptions = 0u, error = errorPtr.ptr)
        }
    }

    private val observer: (CValue<CMTime>) -> Unit = { _ ->
        if (player.currentItem?.isPlaybackLikelyToKeepUp() == true) {
            listener?.onBufferingStateChanged(false)
            listener?.onPlaybackStateChanged(isPlaying())
            setupRemoteCommands()
            currentTrack?.let { updateNowPlayingInfo(it) }
        } else {
            listener?.onBufferingStateChanged(true)
        }
    }

    private val endTimeObserver: (NSNotification?) -> Unit = { _ ->
        ensureAudioSessionActive()
        NSOperationQueue.mainQueue().addOperationWithBlock {
            val nextTrackPlayed = playNextTrack()

            if (!nextTrackPlayed) {
                listener?.onAudioCompleted()

                player.pause()
                player.seekToTime(CMTimeMake(value = 0, timescale = 1000))

                currentTrack?.let { updateNowPlayingInfo(it) }
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
    private fun startTimeObserver() {
        val interval = CMTimeMakeWithSeconds(1.0, NSEC_PER_SEC.toInt())
        timeObserver = player.addPeriodicTimeObserverForInterval(interval, queue = null, usingBlock = observer)

        if (notificationObserver != null) {
            NSNotificationCenter.defaultCenter().removeObserver(notificationObserver!!)
            notificationObserver = null
        }

        player.currentItem?.let { currentItem ->
            notificationObserver = NSNotificationCenter.defaultCenter().addObserverForName(
                name = AVPlayerItemDidPlayToEndTimeNotification,
                `object` = currentItem,
                queue = NSOperationQueue.mainQueue(),
                usingBlock = endTimeObserver
            )
        } ?: run {
            listener?.onError()
        }
    }

    actual fun start() {
        ensureAudioSessionActive()
        player.play()
        listener?.onPlaybackStateChanged(true)
        currentTrack?.let { updateNowPlayingInfo(it) }
    }

    actual fun pause() {
        player.pause()

        listener?.onPlaybackStateChanged(false)
        currentTrack?.let { updateNowPlayingInfo(it) }
    }

    actual fun seekTo(seconds: Long) {
        val time = CMTimeMake(value = seconds, timescale = 1000)
        player.seekToTime(time)
        currentTrack?.let { updateNowPlayingInfo(it) }
    }

    actual fun getCurrentPosition(): Long? {
        val currentTime = player.currentTime()
        return CMTimeGetSeconds(currentTime).toLong() * 1000
    }

    actual fun getDuration(): Long? {
        val currentItem = player.currentItem
        currentItem?.let {
            val duration = it.duration
            return CMTimeGetSeconds(duration).toLong() * 1000
        }
        return null
    }

    actual fun isPlaying(): Boolean {
        return player.timeControlStatus == AVPlayerTimeControlStatusPlaying
    }
}
