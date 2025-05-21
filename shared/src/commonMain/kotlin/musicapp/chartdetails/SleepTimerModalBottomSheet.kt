package musicapp.chartdetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import musicapp.playerview.CountdownViewModel
import musicapp_kmp.shared.generated.resources.Res
import musicapp_kmp.shared.generated.resources._15_M
import musicapp_kmp.shared.generated.resources._1_H
import musicapp_kmp.shared.generated.resources._2_H
import musicapp_kmp.shared.generated.resources._30_M
import musicapp_kmp.shared.generated.resources._3_H
import musicapp_kmp.shared.generated.resources._45_M
import musicapp_kmp.shared.generated.resources.sleep_timer
import org.jetbrains.compose.resources.stringResource

const val SLEEP_TIMER_ITEM_COUNT = 6
const val FIFTEEN_MIN = 15 * 60 * 1000L
const val THIRTY_MIN = 30 * 60 * 1000L
const val FORTY_FIVE_MIN = 45 * 60 * 1000L
const val ONE_HOUR = 60 * 60 * 1000L
const val TWO_HOUR = 2 * 60 * 60 * 1000L
const val THREE_HOUR = 3 * 60 * 60 * 1000L
const val INTERVAL = 1000L

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SleepTimerModalBottomSheet(
    countdownViewModel: CountdownViewModel,
    onSleepTimerExpired:()-> Unit,
    onDismiss: () -> Unit,
    isAnyTimeIntervalSelected: (Boolean) -> Unit
) {
    val modalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Expanded)

    val listOfSleepTimerTitles = listOf(
        stringResource(Res.string._15_M),
        stringResource(Res.string._30_M),
        stringResource(Res.string._45_M),
        stringResource(Res.string._1_H),
        stringResource(Res.string._2_H),
        stringResource(Res.string._3_H)
    )

    val listOfTimeIntervalsToStopAudio =
        listOf(FIFTEEN_MIN, THIRTY_MIN, FORTY_FIVE_MIN, ONE_HOUR, TWO_HOUR, THREE_HOUR)

    LaunchedEffect(true) {
        modalBottomSheetState.show()
    }
    LaunchedEffect(key1 = modalBottomSheetState) {
        snapshotFlow { modalBottomSheetState.currentValue }
            .collect { currentValue ->
                if (currentValue == ModalBottomSheetValue.Hidden) {
                    onDismiss()
                }
            }
    }
    ModalBottomSheetLayout(sheetContent = {
        Column(modifier = Modifier.padding(bottom = 70.dp)) {
            Text(
                text = stringResource(Res.string.sleep_timer),
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                textAlign = TextAlign.Center
            )
            LazyColumn(modifier = Modifier.padding(8.dp)) {
                items(SLEEP_TIMER_ITEM_COUNT) { index ->
                    SleepTimeItem(listOfSleepTimerTitles[index], onItemClick = {
                        countdownViewModel.startCountdown(
                            initialMillis = listOfTimeIntervalsToStopAudio[index],
                            intervalMillis = INTERVAL,
                            onCountDownFinish = {
                                onSleepTimerExpired
                                isAnyTimeIntervalSelected(false)
                            })
                        onDismiss()
                        isAnyTimeIntervalSelected(true)
                    })
                }
            }
        }
    }, sheetState = modalBottomSheetState, content = {

    })
}


@Composable
fun SleepTimeItem(timeTitle: String, onItemClick: () -> Unit) {
    Text(
        text = timeTitle,
        modifier = Modifier.fillMaxWidth().padding(16.dp).clickable(onClick = onItemClick)
    )
}