package musicapp_kmp.chartdetails

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import musicapp_kmp.decompose.PlayerComponent


/**
 * Created by abdulbasit on 28/03/2024.
 */
class PlayerInputUsecase {
    private val _chatDetailsInput = MutableSharedFlow<PlayerComponent.Input>()
    val chatDetailsInput: SharedFlow<PlayerComponent.Input> = _chatDetailsInput

    suspend fun sendEvents(input: PlayerComponent.Input) {
        _chatDetailsInput.emit(input)
    }
}