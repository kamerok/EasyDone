package easydone.coreui.design

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier


@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }
val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.shareTaskBounds(id: String): Modifier =
    withSharedTransitionScope { animatedVisibilityScope, modifier ->
        modifier.sharedBounds(
            rememberSharedContentState(key = "card-$id"),
            animatedVisibilityScope = animatedVisibilityScope,
            resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
            renderInOverlayDuringTransition = false
        )
    }

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.shareTaskTitle(id: String): Modifier =
    withSharedTransitionScope { animatedVisibilityScope, modifier ->
        modifier.sharedBounds(
            rememberSharedContentState(key = "title-$id"),
            animatedVisibilityScope = animatedVisibilityScope
        )
    }

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.withSharedTransitionScope(
    applyModifier: @Composable SharedTransitionScope.(
        animatedVisibilityScope: AnimatedVisibilityScope,
        modifier: Modifier
    ) -> Modifier
): Modifier =
    let {
        val sharedTransitionScope = LocalSharedTransitionScope.current
        val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        if (sharedTransitionScope != null && animatedVisibilityScope != null) {
            with(sharedTransitionScope) {
                applyModifier(animatedVisibilityScope, it)
            }
        } else {
            it
        }
    }

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.skipToLookaheadSize() = withSharedTransitionScope { _, modifier ->
    modifier.skipToLookaheadSize()
}