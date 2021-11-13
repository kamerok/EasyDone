package easydone.feature.waiting

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.systemBarsPadding
import easydone.coreui.design.AppTheme
import easydone.coreui.design.EasyDoneAppBar
import easydone.coreui.design.TaskCard
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Period
import java.time.Year
import java.time.YearMonth
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
                        item {
                            val significantDays by derivedStateOf { state.tasks.keys }
                            val months = remember {
                                val currentMonth = YearMonth.now()
                                (0..(12 * 10)).map { currentMonth.plusMonths(it.toLong()) }
                            }
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(months) { month ->
                                    CalendarMonth(month, significantDays)
                                }
                            }
                        }
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

@OptIn(ExperimentalStdlibApi::class)
@Composable
private fun CalendarMonth(
    month: YearMonth,
    significantDays: Set<LocalDate> = emptySet()
) {
    val formatter = remember {
        DateTimeFormatter.ofPattern(
            if (Year.now().value == month.year) "MMM" else "MMM y"
        )
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = month.format(formatter),
            style = MaterialTheme.typography.body2
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(4.dp)
        ) {
            val days: List<LocalDate> = remember(month) {
                buildList {
                    val firstDay = month.atDay(1)
                    val firstMonday = if (firstDay.dayOfWeek == DayOfWeek.MONDAY) {
                        firstDay
                    } else {
                        firstDay.minusDays(firstDay.dayOfWeek.value - 1L)
                    }
                    (0..5).forEach { weekIndex ->
                        (0..6).forEach { dayIndex ->
                            add(firstMonday.plusDays((7 * weekIndex + dayIndex).toLong()))
                        }
                    }
                }
            }
            days.chunked(7).forEach { weekDays ->
                CalendarWeek(weekDays.map { localDate ->
                    CalendarDay(
                        number = localDate.dayOfMonth,
                        isEnabled = YearMonth.from(localDate) == month,
                        isToday = localDate == LocalDate.now(),
                        isAction = significantDays.contains(localDate)
                    )
                })
            }
        }
    }
}

private data class CalendarDay(
    val number: Int,
    val isEnabled: Boolean,
    val isToday: Boolean,
    val isAction: Boolean
)

@Composable
private fun CalendarWeek(
    days: List<CalendarDay>
) {
    Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
        days.forEach { CalendarDay(it) }
    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
private fun CalendarDay(day: CalendarDay) {
    Surface(
        color = if (day.isAction) MaterialTheme.colors.primary else Color.Transparent,
        shape = CircleShape,
        border = if (day.isToday) {
            BorderStroke(1.dp, MaterialTheme.colors.onSurface)
        } else {
            null
        },
        modifier = Modifier
            .size(16.dp)
            .alpha(if (day.isEnabled) 1f else ContentAlpha.disabled)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "${day.number}",
                fontSize = TextUnit(10f, TextUnitType.Sp),
                fontWeight = FontWeight.Medium,
                letterSpacing = TextUnit(0f, TextUnitType.Unspecified)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MonthPreview() {
    CalendarMonth(
        month = YearMonth.now(),
        significantDays = setOf(
            LocalDate.now().plusDays(5),
            LocalDate.now().minusDays(2),
            LocalDate.now()
        )
    )
}

@Preview
@Composable
private fun DayPreview() {
    Row {
        CalendarDay(CalendarDay(number = 10, isEnabled = true, isToday = false, isAction = false))
        CalendarDay(CalendarDay(number = 10, isEnabled = true, isToday = true, isAction = false))
        CalendarDay(CalendarDay(number = 10, isEnabled = true, isToday = false, isAction = true))
        CalendarDay(CalendarDay(number = 10, isEnabled = false, isToday = false, isAction = false))
        CalendarDay(CalendarDay(number = 10, isEnabled = false, isToday = true, isAction = false))
        CalendarDay(CalendarDay(number = 10, isEnabled = false, isToday = false, isAction = true))
    }
}
