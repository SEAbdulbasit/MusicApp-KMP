package musicapp.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import musicapp.network.models.topfiftycharts.Item
import musicapp.player.MediaPlayerController
import musicapp.playerview.PlayerViewModel
import kotlinx.coroutines.flow.SharedFlow

class PlayerComponentImpl(
    componentContext: ComponentContext,
    private val mediaPlayerController: MediaPlayerController,
    private val trackList: List<Item>,
    private val playerInputs: SharedFlow<PlayerComponent.Input>,
    val output: (PlayerComponent.Output) -> Unit
) : PlayerComponent, ComponentContext by componentContext {

    override val viewModel: PlayerViewModel
        get() = instanceKeeper.getOrCreate {
            PlayerViewModel(
                mediaPlayerController = mediaPlayerController,
                trackList = trackList,
                playerInputs = playerInputs
            )
        }

    override fun onOutPut(output: PlayerComponent.Output) {
        output(output)
    }
}

