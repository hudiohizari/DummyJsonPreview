package id.my.hizari.dummyjsonpreview.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavBackStackEntry

/**
 * id.my.hizari.dummyjsonpreview.navigation
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

/**
 * Pushes inside a tab slide, tab switches cross-fade. Sliding between tabs would read as a push and
 * make the bottom navigation feel like a stack.
 */
object AppTransitions {

    private const val PUSH_MILLIS = 280
    private const val TAB_MILLIS = 90

    private fun AnimatedContentTransitionScope<NavBackStackEntry>.isSameTab(): Boolean =
        initialState.destination.parent?.route == targetState.destination.parent?.route

    val Enter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        if (isSameTab()) {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(durationMillis = PUSH_MILLIS)
            ) + fadeIn(animationSpec = tween(durationMillis = PUSH_MILLIS))
        } else {
            fadeIn(animationSpec = tween(durationMillis = TAB_MILLIS))
        }
    }

    val Exit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        if (isSameTab()) {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(durationMillis = PUSH_MILLIS)
            ) + fadeOut(animationSpec = tween(durationMillis = PUSH_MILLIS))
        } else {
            fadeOut(animationSpec = tween(durationMillis = TAB_MILLIS))
        }
    }

    val PopEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        if (isSameTab()) {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(durationMillis = PUSH_MILLIS)
            ) + fadeIn(animationSpec = tween(durationMillis = PUSH_MILLIS))
        } else {
            fadeIn(animationSpec = tween(durationMillis = TAB_MILLIS))
        }
    }

    val PopExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        if (isSameTab()) {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(durationMillis = PUSH_MILLIS)
            ) + fadeOut(animationSpec = tween(durationMillis = PUSH_MILLIS))
        } else {
            fadeOut(animationSpec = tween(durationMillis = TAB_MILLIS))
        }
    }
}
