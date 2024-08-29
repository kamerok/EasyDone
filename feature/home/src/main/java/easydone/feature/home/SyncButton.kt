package easydone.feature.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import easydone.coreui.design.urgent
import kotlinx.coroutines.launch


@Composable
internal fun SyncButton(
    isInProgress: Boolean,
    isIndicatorEnabled: Boolean,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Box {
            val fraction = remember { Animatable(0f) }
            val scope = rememberCoroutineScope()
            val isInProgressState by rememberUpdatedState(isInProgress)
            LaunchedEffect(isInProgress) {
                scope.launch {
                    while (isInProgressState) {
                        fraction.animateTo(1f, tween(500, easing = LinearEasing))
                        fraction.snapTo(0f)
                    }
                }
            }
            Icon(
                imageVector = Icons.Default.Sync,
                contentDescription = "",
                modifier = Modifier.graphicsLayer {
                    rotationZ = fraction.value * -180f
                }
            )
            AnimatedVisibility(
                visible = isIndicatorEnabled,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Box(
                    Modifier
                        .padding(1.dp)
                        .clip(CircleShape)
                        .size(6.dp)
                        .background(urgent)
                )
            }
        }
    }
}
