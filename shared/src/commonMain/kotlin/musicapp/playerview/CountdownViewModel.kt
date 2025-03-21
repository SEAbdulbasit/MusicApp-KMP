package musicapp.playerview

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.milliseconds

class CountdownViewModel : InstanceKeeper.Instance {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
    }

    private var job  = SupervisorJob()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + coroutineExceptionHandler + job)

    fun startCountdown(initialMillis: Long, intervalMillis: Long, onCountDownFinish: () -> Unit) {
        val targetTime = Clock.System.now() + initialMillis.milliseconds

        viewModelScope.launch {
            while (true) {
                val now = Clock.System.now()
                val millisUntilFinished = (targetTime - now).inWholeMilliseconds

                if (millisUntilFinished <= 0) {
                    onCountDownFinish()
                    break
                }

                delay(intervalMillis)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModelScope.cancel()
    }
}