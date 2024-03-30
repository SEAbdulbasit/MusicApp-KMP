package musicapp.player

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerTimeControlStatusPlaying
import platform.AVFoundation.addPeriodicTimeObserverForInterval
import platform.AVFoundation.currentItem
import platform.AVFoundation.isPlaybackLikelyToKeepUp
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.removeTimeObserver
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.seekToTime
import platform.AVFoundation.timeControlStatus
import platform.CoreMedia.CMTime
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.darwin.NSEC_PER_SEC
import kotlin.experimental.ExperimentalNativeApi


@OptIn(ExperimentalForeignApi::class)
actual class MediaPlayerController actual constructor(val platformContext: PlatformContext) {

    private lateinit var timeObserver: Any

    private val player: AVPlayer = AVPlayer()

    private var listener: MediaPlayerListener? = null

    init {
        setUpAudioSession()
    }

    actual fun prepare(pathSource: String, listener: MediaPlayerListener) {
        println("Prepare")
        this.listener = listener
        val url = NSURL(string = pathSource)
        stop1()
        startTimeObserver()
        player.replaceCurrentItemWithPlayerItem(AVPlayerItem(url))
        player.play()
    }

    private fun setUpAudioSession() {
        try {
            val audioSession = AVAudioSession.sharedInstance()
            audioSession.setCategory(AVAudioSessionCategoryPlayback, null)
            audioSession.setActive(true, null)
        } catch (e: Exception) {
            println("Error setting up audio session: ${e.message}")
        }
    }

    private val observer: (CValue<CMTime>) -> Unit = { time: CValue<CMTime> ->
        if (player.currentItem?.isPlaybackLikelyToKeepUp() == true) {
            listener?.onReady()
        }
    }

    @OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
    private fun startTimeObserver() {
        val interval = CMTimeMakeWithSeconds(1.0, NSEC_PER_SEC.toInt())
        timeObserver = player.addPeriodicTimeObserverForInterval(interval, null, observer)
        NSNotificationCenter.defaultCenter.addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = player.currentItem,
            queue = NSOperationQueue.mainQueue,
            usingBlock = {
                println("I am called")
                listener?.onAudioCompleted()
            }
        )
    }

    actual fun start() {
        println("On Play")
        player.play()
    }

    actual fun pause() {
        println("On pause")
        player.pause()
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun stop() {
        player.run {
            pause()
            seekToTime(time = cValue {
                value = 0
            })
        }
    }

    private fun stop1() {
        if (::timeObserver.isInitialized) player.removeTimeObserver(timeObserver)
        player.pause()
        player.currentItem?.seekToTime(CMTimeMakeWithSeconds(0.0, NSEC_PER_SEC.toInt()))
    }


    actual fun isPlaying(): Boolean {
        return this.player.timeControlStatus == AVPlayerTimeControlStatusPlaying
    }

    actual fun release() {
        observer.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
    }
}