package easydone.feature.quickcreatetask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import easydone.core.domain.DomainRepository
import easydone.core.domain.model.Task
import easydone.core.domain.model.TaskTemplate
import easydone.core.strings.R
import easydone.coreui.design.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QuickCreateTaskFragment(
    private val repository: DomainRepository,
    private val navigator: QuickCreateTaskNavigator
) : Fragment() {

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            AppTheme {
                val scope = rememberCoroutineScope()
                val keyboardController = LocalSoftwareKeyboardController.current
                val close: () -> Unit = remember {
                    {
                        scope.launch {
                            keyboardController?.hide()
                            delay(KEYBOARD_WAIT_DELAY)
                            navigator.closeScreen()
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
                            var text by remember { mutableStateOf("") }
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
                            Box(modifier = Modifier.padding(16.dp)) {
                                if (text.isEmpty()) {
                                    Text(
                                        text = stringResource(R.string.quick_create_hint),
                                        style = MaterialTheme.typography.body1,
                                        color = MaterialTheme.colors.onSurface.copy(ContentAlpha.medium)
                                    )
                                }
                                BasicTextField(
                                    value = text,
                                    onValueChange = { text = it },
                                    textStyle = MaterialTheme.typography.body1.copy(
                                        color = LocalContentColor.current
                                    ),
                                    modifier = Modifier.focusRequester(focusRequester),
                                    cursorBrush = SolidColor(MaterialTheme.colors.primary),
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Sentences,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = { save(text) }
                                    )
                                )
                            }
                            AnimatedVisibility(
                                visible = text.isNotEmpty(),
                                modifier = Modifier.align(Alignment.BottomEnd)
                            ) {
                                IconButton(onClick = { save(text) }) {
                                    Icon(Icons.Default.Send, "")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val KEYBOARD_WAIT_DELAY = 200L
    }

}
