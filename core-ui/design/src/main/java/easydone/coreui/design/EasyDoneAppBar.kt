package easydone.coreui.design

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier


@Composable
private fun DefaultBackIcon() {
    val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    IconButton(onClick = { dispatcher?.onBackPressed() }) {
        Icon(Icons.AutoMirrored.Default.ArrowBack, "")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EasyDoneAppBar(
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit) = { DefaultBackIcon() },
    actions: @Composable RowScope.() -> Unit = {},
    menu: @Composable (ColumnScope.() -> Unit)? = null,
    title: @Composable () -> Unit,
) {
    TopAppBar(
        title = title,
        navigationIcon = navigationIcon,
        actions = {
            actions()
            if (menu != null) {
                var showMenu by remember { mutableStateOf(false) }
                IconButton(onClick = { showMenu = !showMenu }) {
                    Icon(Icons.Default.MoreVert, "")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                ) {
                    menu()
                }
            }
        },
        modifier = modifier
    )
}
