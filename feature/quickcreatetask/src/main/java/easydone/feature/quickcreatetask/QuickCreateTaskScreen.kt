package easydone.feature.quickcreatetask

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import easydone.core.domain.DomainRepository
import easydone.core.domain.model.Task
import easydone.core.domain.model.TaskTemplate
import easydone.core.strings.R
import easydone.coreui.design.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val KEYBOARD_WAIT_DELAY = 200L

@Composable
fun QuickCreateTaskScreen(
    repository: DomainRepository,
    closeScreen: () -> Unit
) {
    AppTheme {
        val scope = rememberCoroutineScope()
        val keyboardController = LocalSoftwareKeyboardController.current
        val close: () -> Unit = remember {
            {
                scope.launch {
                    keyboardController?.hide()
                    delay(KEYBOARD_WAIT_DELAY)
                    closeScreen()
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { close() }
        ) {
            Card(
                modifier = Modifier
                    .padding(top = 72.dp, start = 32.dp, end = 32.dp)
                    .fillMaxWidth()
                    .height(200.dp)
                    //prevent click through
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {}
            ) {
                Box {
                    var text by rememberSaveable { mutableStateOf("") }
                    val focusRequester = remember { FocusRequester() }
                    val save: (String) -> Unit = remember {
                        { text ->
                            scope.launch {
                                TaskTemplate.create(
                                    type = Task.Type.Inbox,
                                    title = text,
                                    description = "",
                                    isUrgent = false,
                                    isImportant = false
                                ).onSuccess {
                                    repository.createTask(it)
                                }
                                close()
                            }
                        }
                    }
                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize()
                    ) {
                        if (text.isEmpty()) {
                            Text(
                                text = stringResource(R.string.quick_create_hint),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        BasicTextField(
                            value = text,
                            onValueChange = { text = it },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = LocalContentColor.current
                            ),
                            modifier = Modifier.focusRequester(focusRequester),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { save(text) }
                            )
                        )
                    }
                    this@Card.AnimatedVisibility(
                        visible = text.isNotEmpty(),
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        IconButton(onClick = { save(text) }) {
                            Icon(Icons.AutoMirrored.Default.Send, "")
                        }
                    }
                }
            }
        }
    }
}