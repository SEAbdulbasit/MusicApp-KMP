package musicapp.player

import kotlinx.cinterop.*
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
    private val player: AVPlayer = AVPlayer()
    private var listener: MediaPlayerListener? = null
    private var currentTrack: MediaItem? = null

    // Media Player notification components
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

            // Set the audio session category to Playback for background audio
            // Use options to allow Bluetooth and AirPlay to ensure network connectivity
            val categorySet = audioSession.setCategory(
                category = AVAudioSessionCategoryPlayback,
                mode = AVAudioSessionModeDefault,
                options = 0u,
                error = errorPtr.ptr
            )
            if (!categorySet) {
                errorPtr.value?.let { error ->
                    println("Error setting audio session category: ${error.localizedDescription}")
                }
                return@memScoped
            }

            // Activate the audio session
            val activeSet = audioSession.setActive(active = true, withOptions = 0u, error = errorPtr.ptr)
            if (!activeSet) {
                errorPtr.value?.let { error ->
                    println("Error activating audio session: ${error.localizedDescription}")
                }
            }

            // Add interruption observer
            NSNotificationCenter.defaultCenter().addObserverForName(
                name = "AVAudioSessionInterruptionNotification",
                `object` = audioSession,
                queue = NSOperationQueue.mainQueue(),
                usingBlock = { notification: NSNotification? ->
                    notification?.userInfo?.let { userInfo ->
                        val interruptionType = userInfo[AVAudioSessionInterruptionTypeKey] as? NSNumber
                        val typeValue = interruptionType?.unsignedLongValue
                        when (typeValue) {
                            AVAudioSessionInterruptionTypeBegan.toULong() -> {
                                // Pause playback when the interruption begins
                                pause()
                            }

                            AVAudioSessionInterruptionTypeEnded.toULong() -> {
                                // Resume playback when the interruption ends, if desired
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
        // Play command
        remoteCommandCenter.playCommand.setEnabled(true)
        remoteCommandCenter.playCommand.addTargetWithHandler { event: MPRemoteCommandEvent? ->
            if (!isPlaying()) {
                start()
                MPRemoteCommandHandlerStatusSuccess
            } else {
                MPRemoteCommandHandlerStatusCommandFailed
            }
        }

        // Pause command
        remoteCommandCenter.pauseCommand.setEnabled(true)
        remoteCommandCenter.pauseCommand.addTargetWithHandler { event: MPRemoteCommandEvent? ->
            if (isPlaying()) {
                pause()
                MPRemoteCommandHandlerStatusSuccess
            } else {
                MPRemoteCommandHandlerStatusCommandFailed
            }
        }

        // Toggle play/pause command
        remoteCommandCenter.togglePlayPauseCommand.setEnabled(true)
        remoteCommandCenter.togglePlayPauseCommand.addTargetWithHandler { event: MPRemoteCommandEvent? ->
            if (isPlaying()) pause() else start()
            MPRemoteCommandHandlerStatusSuccess
        }

        // Seek forward (e.g., 15 seconds)
        remoteCommandCenter.skipForwardCommand.setEnabled(true)
        remoteCommandCenter.skipForwardCommand.preferredIntervals = NSArray.arrayWithObject(NSNumber(double = 15.0))
        remoteCommandCenter.skipForwardCommand.addTargetWithHandler { event: MPRemoteCommandEvent? ->
            val seconds = (event as? MPSkipIntervalCommandEvent)?.interval ?: 15.0
            val current = getCurrentPosition()?.toDouble()?.div(1000) ?: 0.0
            seekTo(((current + seconds) * 1000).toLong())
            MPRemoteCommandHandlerStatusSuccess
        }

        // Seek backward (e.g., 15 seconds)
        remoteCommandCenter.skipBackwardCommand.setEnabled(true)
        remoteCommandCenter.skipBackwardCommand.preferredIntervals = NSArray.arrayWithObject(NSNumber(double = 15.0))
        remoteCommandCenter.skipBackwardCommand.addTargetWithHandler { event: MPRemoteCommandEvent? ->
            val seconds = (event as? MPSkipIntervalCommandEvent)?.interval ?: 15.0
            val current = getCurrentPosition()?.toDouble()?.div(1000) ?: 0.0
            seekTo(((current - seconds).coerceAtLeast(0.0) * 1000).toLong())
            MPRemoteCommandHandlerStatusSuccess
        }
    }

    private fun updateNowPlayingInfo(track: MediaItem) {
        nowPlayingInfo[MPMediaItemPropertyTitle] = track.title
        nowPlayingInfo[MPMediaItemPropertyArtist] = track.artist
        nowPlayingInfo[MPMediaItemPropertyAlbumTitle] = track.title
        val duration = getDuration()?.toDouble()?.div(1000) ?: 0.0
        nowPlayingInfo[MPMediaItemPropertyPlaybackDuration] = duration
        val currentPosition = getCurrentPosition()?.toDouble()?.div(1000) ?: 0.0
        nowPlayingInfo[MPNowPlayingInfoPropertyElapsedPlaybackTime] = currentPosition
        nowPlayingInfo[MPNowPlayingInfoPropertyPlaybackRate] = if (isPlaying()) 1.0 else 0.0

        val placeholderImage = UIImage.imageNamed("AppIcon")
        if (placeholderImage != null) {
            nowPlayingInfo[MPMediaItemPropertyArtwork] =
                MPMediaItemArtwork(boundsSize = placeholderImage.size) { _ -> placeholderImage }
        }

        // Async image loading for album artwork
        track.artworkUrl?.let { urlString ->
            val url = NSURL.URLWithString(urlString)
            if (url != null) {
                val session = NSURLSession.sharedSession()
                val task = session.dataTaskWithURL(url) { data, response, error ->
                    if (error == null && data != null) {
                        val image = UIImage.imageWithData(data)
                        if (image != null) {
                            // Update nowPlayingInfo with the loaded image
                            nowPlayingInfo[MPMediaItemPropertyArtwork] =
                                MPMediaItemArtwork(boundsSize = image.size) { _ -> image }
                            // Refresh nowPlayingInfo on the main queue
                            NSOperationQueue.mainQueue().addOperationWithBlock {
                                nowPlayingInfoCenter.nowPlayingInfo = nowPlayingInfo.toMap()
                            }
                        }
                    }
                }
                task.resume()
            }
        }


        nowPlayingInfoCenter.nowPlayingInfo = nowPlayingInfo.toMap()
    }

    actual fun prepare(mediaItem: MediaItem, listener: MediaPlayerListener) {
        this.listener = listener
        this.currentTrack = mediaItem
        val url = NSURL.URLWithString(mediaItem.pathSource) ?: return
        stop()
        startTimeObserver()
        player.replaceCurrentItemWithPlayerItem(AVPlayerItem(uRL = url))
        player.play()
        // Notification will be updated in the observer callback when media is ready
    }

    private val observer: (CValue<CMTime>) -> Unit = { time: CValue<CMTime> ->
        if (player.currentItem?.isPlaybackLikelyToKeepUp() == true) {
            listener?.onReady()
            setupRemoteCommands()
            // Update notification info when media is ready
            currentTrack?.let { updateNowPlayingInfo(it) }
        }
    }

    @OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
    private fun startTimeObserver() {
        val interval = CMTimeMakeWithSeconds(1.0, NSEC_PER_SEC.toInt())
        timeObserver = player.addPeriodicTimeObserverForInterval(interval, queue = null, usingBlock = observer)
        NSNotificationCenter.defaultCenter().addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = player.currentItem,
            queue = NSOperationQueue.mainQueue(),
            usingBlock = { _: NSNotification? -> listener?.onAudioCompleted() }
        )
    }

    actual fun start() {
        player.play()
        currentTrack?.let { updateNowPlayingInfo(it) }
    }

    actual fun pause() {
        player.pause()
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

    @OptIn(ExperimentalForeignApi::class)
    actual fun stop() {
        player.pause()
        player.seekToTime(CMTimeMake(value = 0, timescale = 1000))
        if (timeObserver != null) {
            player.removeTimeObserver(timeObserver!!)
            timeObserver = null
        }
        currentTrack?.let { updateNowPlayingInfo(it) }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun release() {
        if (timeObserver != null) {
            player.removeTimeObserver(timeObserver!!)
            timeObserver = null
        }
        NSNotificationCenter.defaultCenter().removeObserver(observer = player)
        nowPlayingInfoCenter.nowPlayingInfo = null
        remoteCommandCenter.playCommand.setEnabled(false)
        remoteCommandCenter.pauseCommand.setEnabled(false)
        remoteCommandCenter.togglePlayPauseCommand.setEnabled(false)
        remoteCommandCenter.skipForwardCommand.setEnabled(false)
        remoteCommandCenter.skipBackwardCommand.setEnabled(false)
        player.replaceCurrentItemWithPlayerItem(null)
    }

    actual fun isPlaying(): Boolean {
        return player.timeControlStatus == AVPlayerTimeControlStatusPlaying
    }
}
