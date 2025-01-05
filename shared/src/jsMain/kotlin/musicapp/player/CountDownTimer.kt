package musicapp.player

import kotlinx.browser.window

actual class CountDownTimer actual constructor(
    private val initialMillis: Long,
    private val intervalMillis: Long,
    private val onCountDownFinish: () -> Unit
) {
    private var timerId: Int? = null
    private var targetTimeMillis: Long = 0L

    actual fun start() {
        // Set the target time in the future
        val currentTimeMillis = window.performance.now().toLong()
        targetTimeMillis = currentTimeMillis + initialMillis

        timerId = window.setInterval({
            val millisUntilFinished = targetTimeMillis - window.performance.now().toLong()

            if (millisUntilFinished <= 0) {
                cancel() // Stop the timer
                onCountDownFinish()
            }
        }, intervalMillis.toInt())
    }

    actual fun cancel() {
        timerId?.let { window.clearInterval(it) }
        timerId = null
    }
}