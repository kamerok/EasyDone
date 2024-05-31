package easydone.feature.login

import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import easydone.coreui.design.AppTheme

@Composable
fun LoginScreen(onLoginClick: () -> Unit) {
    AppTheme {
        Button(
            onClick = onLoginClick,
            modifier = Modifier.wrapContentSize()
        ) {
            Text("Login with Trello")
        }
    }
}

@Preview
@Composable
fun ScreenPreview() {
    LoginScreen(onLoginClick = {})
}