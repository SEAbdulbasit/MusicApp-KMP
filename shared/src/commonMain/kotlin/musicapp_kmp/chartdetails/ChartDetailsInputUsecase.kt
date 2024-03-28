package musicapp_kmp.chartdetails

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import musicapp_kmp.decompose.ChartDetailsComponent


/**
 * Created by abdulbasit on 28/03/2024.
 */
class ChartDetailsInputUsecase {
    private val _chatDetailsInput = MutableSharedFlow<ChartDetailsComponent.Input>()
    val chatDetailsInput: SharedFlow<ChartDetailsComponent.Input> = _chatDetailsInput

    suspend fun sendEvents(input: ChartDetailsComponent.Input) {
        _chatDetailsInput.emit(input)
    }
}