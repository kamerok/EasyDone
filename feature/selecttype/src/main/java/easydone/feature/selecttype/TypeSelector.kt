package easydone.feature.selecttype

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import easydone.core.domain.model.Task
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


@Composable
fun TypeSelector(
    type: Task.Type,
    onTypeSelected: (Task.Type) -> Unit,
    modifier: Modifier = Modifier
) {
    //TODO: extract resources
    Column(modifier = modifier) {
        TypeSelectorItem(
            isSelected = type == Task.Type.Inbox,
            typeText = "Inbox",
            onClick = { onTypeSelected(Task.Type.Inbox) }
        )
        HorizontalDivider()
        TypeSelectorItem(
            isSelected = type == Task.Type.ToDo,
            typeText = "ToDo",
            onClick = { onTypeSelected(Task.Type.ToDo) }
        )
        val context = LocalContext.current
        val initialDay = if (type is Task.Type.Waiting) type.date else LocalDate.now().plusDays(1)
        HorizontalDivider()
        TypeSelectorItem(
            isSelected = type is Task.Type.Waiting,
            typeText = "Waiting",
            date = if (type is Task.Type.Waiting) type.date else null,
            onClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val newDate = LocalDate.of(year, month + 1, dayOfMonth)
                        onTypeSelected(Task.Type.Waiting(newDate))
                    },
                    initialDay.year,
                    initialDay.monthValue - 1,
                    initialDay.dayOfMonth
                )
                    .apply {
                        datePicker.minDate =
                            LocalDate.now().plusDays(1)
                                .atStartOfDay(ZoneOffset.UTC).toInstant()
                                .toEpochMilli()
                    }
                    .show()
            }
        )
        HorizontalDivider()
        TypeSelectorItem(
            isSelected = type == Task.Type.Project,
            typeText = "Project",
            onClick = { onTypeSelected(Task.Type.Project) }
        )
        HorizontalDivider()
        TypeSelectorItem(
            isSelected = type == Task.Type.Maybe,
            typeText = "Maybe",
            onClick = { onTypeSelected(Task.Type.Maybe) }
        )
    }
}

@Composable
private fun TypeSelectorItem(
    isSelected: Boolean,
    typeText: String,
    date: LocalDate? = null,
    onClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        if (isSelected) {
            Icon(Icons.Default.Check, "")
        }
        Text(
            text = typeText,
            style = MaterialTheme.typography.titleMedium
        )
        if (date != null) {
            val formatter = remember { DateTimeFormatter.ofPattern("d MMM y") }
            Text(
                text = "(${formatter.format(date)})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TypeSelectorPreview() {
    TypeSelector(
        type = Task.Type.Waiting(LocalDate.of(2020, 1, 10)),
        onTypeSelected = { }
    )
}
