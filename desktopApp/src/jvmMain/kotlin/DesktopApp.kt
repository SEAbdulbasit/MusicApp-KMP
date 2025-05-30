import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.statekeeper.SerializableContainer
import com.arkivanov.essenty.statekeeper.StateKeeperDispatcher
import musicapp.CommonMainDesktop
import musicapp.decompose.MusicRootImpl
import musicapp.network.SpotifyApiImpl
import musicapp.player.MediaPlayerController
import musicapp.utils.PlatformContext
import java.awt.Dimension
import java.awt.Toolkit
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

fun main() {
    val lifecycle = LifecycleRegistry()
    val stateKeeper = StateKeeperDispatcher(tryRestoreStateFromFile())

    val rootComponent = runOnUiThread {
        MusicRootImpl(
            componentContext = DefaultComponentContext(
                lifecycle = lifecycle,
                stateKeeper = stateKeeper,
            ),
            api = SpotifyApiImpl(),
            mediaPlayerController = MediaPlayerController(PlatformContext())
        )
    }

    application {
        val windowState = rememberWindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = getPreferredWindowSize(800, 800)
        )

        LifecycleController(lifecycle, windowState)

        var isCloseRequested by remember { mutableStateOf(false) }

        Window(
            onCloseRequest = { isCloseRequested = true },
            title = "MusicApp-KMP",
            state = windowState,
        ) {
            CommonMainDesktop(rootComponent)
            if (isCloseRequested) {
                SaveStateDialog(
                    onSaveState = { saveStateToFile(stateKeeper.save()) },
                    onExitApplication = ::exitApplication,
                    onDismiss = { isCloseRequested = false },
                )
            }
        }
    }
}

fun getPreferredWindowSize(desiredWidth: Int, desiredHeight: Int): DpSize {
    val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
    val preferredWidth: Int = (screenSize.width * 0.8f).toInt()
    val preferredHeight: Int = (screenSize.height * 0.8f).toInt()
    val width: Int = if (desiredWidth < preferredWidth) desiredWidth else preferredWidth
    val height: Int = if (desiredHeight < preferredHeight) desiredHeight else preferredHeight
    return DpSize(width.dp, height.dp)
}

@Composable
private fun SaveStateDialog(
    onSaveState: () -> Unit,
    onExitApplication: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        buttons = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onDismiss) {
                    Text(text = "Cancel")
                }

                TextButton(onClick = onExitApplication) {
                    Text(text = "No")
                }

                TextButton(onClick = {
//                    onSaveState()
                    onExitApplication()
                }) {
                    Text(text = "Yes")
                }
            }
        },
        title = { Text(text = "MusicApp-KMP") },
        text = { Text(text = "Do you want to save the application's state?") },
        modifier = Modifier.width(400.dp),
    )
}

private const val SAVED_STATE_FILE_NAME = "saved_state.dat"

private fun saveStateToFile(state: SerializableContainer) {
    ObjectOutputStream(File(SAVED_STATE_FILE_NAME).outputStream()).use { output ->
        output.writeObject(state)
    }
}

private fun tryRestoreStateFromFile(): SerializableContainer? =
    File(SAVED_STATE_FILE_NAME).takeIf(File::exists)?.let { file ->
        try {
            ObjectInputStream(file.inputStream()).use(ObjectInputStream::readObject) as SerializableContainer
        } catch (e: Exception) {
            null
        } finally {
            file.delete()
        }
    }



