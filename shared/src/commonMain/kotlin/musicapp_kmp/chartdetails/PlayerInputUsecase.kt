package musicapp_kmp.chartdetails

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import musicapp_kmp.decompose.PlayerComponent


/**
 * Created by abdulbasit on 28/03/2024.
 */
class PlayerInputUsecase {
    val coroutineScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())

    private val _chatDetailsInput = MutableSharedFlow<PlayerComponent.Input>()
    val chatDetailsInput: SharedFlow<PlayerComponent.Input> = _chatDetailsInput

    fun sendEvents(input: PlayerComponent.Input) {
        coroutineScope.launch {
            _chatDetailsInput.emit(input)
        }
    }
}