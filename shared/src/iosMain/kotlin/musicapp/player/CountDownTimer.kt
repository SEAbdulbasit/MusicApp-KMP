package musicapp.player

import kotlinx.cinterop.ObjCAction
import platform.Foundation.NSDate
import platform.Foundation.NSTimer
import platform.Foundation.timeIntervalSince1970


actual class CountDownTimer actual constructor(
    private val initialMillis: Long,
    private val intervalMillis: Long,
    private val onCountDownFinish: () -> Unit
) {
    private var iosTimer: NSTimer? = null
    private var targetTimeMillis: Long = 0L
    @ObjCAction
    fun onTimerTick(timer: NSTimer) {
        // Get the current time in milliseconds
        val currentTimeMillis = (NSDate().timeIntervalSince1970 * 1000).toLong()

        // Calculate the remaining time
        val millisUntilFinished = targetTimeMillis - currentTimeMillis

        if (millisUntilFinished <= 0) {
            // If the countdown is complete, call the finish method
            onTimerFinish(timer)
            return
        }
    }

    @ObjCAction
    fun onTimerFinish(timer: NSTimer) {
        onCountDownFinish()
        cancel() // Stop the timer
    }

    actual fun start() {
        // Calculate the target time in milliseconds (future time)
        val timeInFuture = initialMillis
        targetTimeMillis = (NSDate().timeIntervalSince1970 * 1000).toLong() + timeInFuture

        // Schedule the timer with the given interval
        iosTimer = NSTimer.scheduledTimerWithTimeInterval(
            interval = intervalMillis / 1000.0,
            repeats = true
        ) { _ ->
            onTimerTick(iosTimer!!)
        }
    }

    actual fun cancel() {
        iosTimer?.invalidate()
    }

}
