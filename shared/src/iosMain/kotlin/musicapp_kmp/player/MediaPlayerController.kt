package musicapp_kmp.player

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerTimeControlStatusPlaying
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.seekToTime
import platform.AVFoundation.timeControlStatus
import platform.AVKit.AVPlayerViewController
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.darwin.NSObjectProtocol
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.WeakReference

actual class MediaPlayerController actual constructor(val platformContext: PlatformContext) {

    private var player: AVPlayer? = null
        set(value) {
            field = value
            checkReady()
        }
    @OptIn(ExperimentalNativeApi::class)
    private var listener: WeakReference<MediaPlayerListener>? = null
    private var observer: NSObjectProtocol? = null

    private var playerController: AVPlayerViewController? = null
        set(value) {
            field = value
            checkReady()
        }

    private fun checkReady() {
        if (player != null && playerController != null) onReady()
    }

    @OptIn(ExperimentalNativeApi::class)
    actual fun prepare(pathSource: String, listener: MediaPlayerListener) {
        this.listener = WeakReference(listener)

        val url = NSURL(string = pathSource)
        this.player = AVPlayer(uRL = url)

        observer = NSNotificationCenter.defaultCenter.addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
            usingBlock = {
                listener.onVideoCompleted()
            }
        )
    }

    @OptIn(ExperimentalNativeApi::class)
    private fun onReady() {
        val playerController = playerController ?: return
        val player = player ?: return

        playerController.player = player

        listener?.get()?.onReady()
    }

    actual fun start() {
        player?.play()
    }

    actual fun pause() {
        player?.pause()
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun stop() {
        player?.run {
            pause()
            seekToTime(time = cValue {
                value = 0
            })
        }
    }

    actual fun isPlaying(): Boolean {
        return player?.timeControlStatus == AVPlayerTimeControlStatusPlaying
    }

    actual fun release() {
        observer?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
    }
}