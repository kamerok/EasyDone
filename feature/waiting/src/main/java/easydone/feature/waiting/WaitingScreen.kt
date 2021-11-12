package easydone.feature.waiting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.systemBarsPadding
import easydone.coreui.design.AppTheme
import easydone.coreui.design.EasyDoneAppBar
import easydone.coreui.design.TaskCard
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter


@Composable
internal fun WaitingScreen(viewModel: WaitingViewModel) {
    AppTheme {
        ProvideWindowInsets {
            FullscreenContent {
                Column {
                    EasyDoneAppBar { Text("Waiting") }
                    val state = viewModel.state.collectAsState().value
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        //TODO: reuse format logic
                        val formatter = DateTimeFormatter.ofPattern("d MMM y")
                        state.tasks.entries
                            .sortedBy { it.key }
                            .forEach { (date, tasks) ->
                                item {
                                    val period = Period.between(LocalDate.now(), date)
                                    val dateText = buildString {
                                        append(formatter.format(date))
                                        append(" (")
                                        if (period.years > 0) {
                                            append("${period.years}y ")
                                        }
                                        if (period.months > 0) {
                                            append("${period.months}m ")
                                        }
                                        append("${period.days}d")
                                        append(")")
                                    }
                                    Text(
                                        text = dateText,
                                        style = MaterialTheme.typography.h5
                                    )
                                }
                                items(tasks) { task ->
                                    TaskCard(
                                        task = task,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.onTaskClick(task) }
                                    )
                                }
                            }
                    }
                }
            }
        }
    }
}

@Composable
private fun FullscreenContent(
    content: @Composable () -> Unit
) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier
            .fillMaxSize()
            //to draw under paddings
            .background(MaterialTheme.colors.background)
            .systemBarsPadding()
    ) {
        content()
    }
}
