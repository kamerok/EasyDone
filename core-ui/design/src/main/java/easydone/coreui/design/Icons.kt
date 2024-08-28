package easydone.coreui.design

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Subject
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun IconUrgent(modifier: Modifier = Modifier) {
    Icon(Icons.Default.Bolt, "", tint = urgent, modifier = modifier)
}

@Composable
fun IconImportant(modifier: Modifier = Modifier) {
    Icon(Icons.Default.PriorityHigh, "", tint = important, modifier = modifier)
}

@Composable
fun IconText(modifier: Modifier = Modifier) {
    Icon(Icons.AutoMirrored.Default.Subject, "", modifier = modifier)
}