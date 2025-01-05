package musicapp.player

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

actual class CountDownTimer actual constructor(
    private val initialMillis: Long,
    private val intervalMillis: Long,
    private val onCountDownFinish: () -> Unit
) {
    private var job: Job? = null
    private var targetTimeMillis: Long = 0L

    actual fun start() {
        // Set the target time in the future
        val currentTimeMillis = System.currentTimeMillis()
        targetTimeMillis = currentTimeMillis + initialMillis

        job = CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                val millisUntilFinished = targetTimeMillis - System.currentTimeMillis()

                // If the countdown is complete, call the finish method
                if (millisUntilFinished <= 0) {
                    onCountDownFinish()
                    break
                }
                delay(intervalMillis)
            }
        }
    }

    actual fun cancel() {
        job?.cancel()
    }
}