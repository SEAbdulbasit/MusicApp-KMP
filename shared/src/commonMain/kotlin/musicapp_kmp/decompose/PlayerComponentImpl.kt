package musicapp_kmp.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.coroutines.flow.SharedFlow
import musicapp_kmp.network.models.topfiftycharts.Item
import musicapp_kmp.player.MediaPlayerController
import musicapp_kmp.playerview.PlayerViewModel

class PlayerComponentImpl(
    componentContext: ComponentContext,
    private val mediaPlayerController: MediaPlayerController,
    private val trackList: List<Item>,
    private val playerInputs: SharedFlow<PlayerComponent.Input>,
    val output: (PlayerComponent.Output) -> Unit
) : PlayerComponent, ComponentContext by componentContext {

    override val viewModel: PlayerViewModel
        get() = throw Exception("Abc")/*instanceKeeper.getOrCreate {
            PlayerViewModel(
                mediaPlayerController = mediaPlayerController,
                trackList = trackList,
                playerInputs = playerInputs
            )*/

    override fun onOutPut(output: PlayerComponent.Output) {
        TODO("Not yet implemented")
    }
}
//
//    override fun onOutPut(output: PlayerComponent.Output) {
//        output(output)
//    }


