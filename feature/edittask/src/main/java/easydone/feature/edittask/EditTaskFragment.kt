package easydone.feature.edittask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.darkColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.statusBarsPadding
import easydone.core.domain.DomainRepository


class EditTaskFragment(
    private val repository: DomainRepository
) : Fragment() {

    private val id: String by lazy { arguments?.getString(TASK_ID) ?: error("ID must be provided") }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            EditTaskScreen()
        }
    }

    companion object {
        private const val TASK_ID = "task_id"

        fun createArgs(taskId: String): Bundle = bundleOf(TASK_ID to taskId)
    }

}

@Composable
private fun EditTaskScreen() {
    val primary = Color(0xFF5073F0)
    MaterialTheme(
        colors = if (isSystemInDarkTheme()) {
            darkColors(
                background = Color(0xFF2D3134),
                primary = primary,
                onPrimary = Color.White
            )
        } else {
            lightColors(
                primary = primary
            )
        }
    ) {
        ProvideWindowInsets {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsWithImePadding()
            ) {
                ScreenContent()
            }
        }
    }
}

@Composable
private fun ScreenContent() {
    Column {
        AppBar()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Fields()
            Spacer(modifier = Modifier.height(16.dp))
            SaveButton()
        }
    }
}

@Composable
private fun AppBar() {
    val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    TopAppBar(
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.background,
        title = { Text(text = "Edit task") },
        navigationIcon = {
            IconButton(onClick = { dispatcher?.onBackPressed() }) {
                Icon(Icons.Default.ArrowBack, "")
            }
        },
    )
}

@Composable
private fun Fields() {
    Column {
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(Modifier.clickable { /*TODO*/ }) {
                Text(text = "INBOX")
                Icon(Icons.Default.ArrowDropDown, "")
            }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = title,
                onValueChange = { title = it },
                label = { Text(text = "Title") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(text = "Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                var isUrgent by remember { mutableStateOf(false) }
                var isImportant by remember { mutableStateOf(false) }
                Chip(
                    isSelected = isUrgent,
                    icon = {
                        Icon(Icons.Default.Bolt, "", tint = Color(0xFFF3BE00) /*TODO: extract*/)
                    },
                    label = { Text("Urgent") },
                    onClick = { isUrgent = !isUrgent }
                )
                Chip(
                    isSelected = isImportant,
                    icon = {
                        Icon(Icons.Default.PriorityHigh, "", tint = Color(0xFFE14B4B)/*TODO*/)
                    },
                    label = { Text("Important") },
                    onClick = { isImportant = !isImportant }
                )
            }
        }
    }
}

@Composable
private fun Chip(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit,
    onClick: () -> Unit
) {
    //todo: extract
    val borderColor =
        if (MaterialTheme.colors.isLight) Color(0f, 0f, 0f, 0.12f) else Color(1f, 1f, 1f, 0.12f)
    val shape = RoundedCornerShape(CornerSize(100))
    Surface(
        color = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.background,
        shape = shape,
        border = if (isSelected) null else BorderStroke(1.dp, borderColor),
        modifier = modifier
    ) {
        Row(
            Modifier
                .clickable(onClick = onClick)
                .padding(top = 4.dp, bottom = 4.dp, start = 8.dp, end = 16.dp)
        ) {
            icon()
            label()
        }
    }
}

@Composable
private fun SaveButton() {
    Button(
        onClick = { /*TODO*/ },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Save")
    }
}

@Preview(
    name = "Screen",
    widthDp = 393,
    heightDp = 851,
    showBackground = true
)
@Composable
private fun ContentPreview() {
    EditTaskScreen()
}
