package dev.safi.largetext.ui.display

import android.content.pm.ActivityInfo
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import dev.safi.largetext.data.AppFont
import dev.safi.largetext.data.DisplayColors
import dev.safi.largetext.data.RotationMode

private const val TAP_AREA_FRACTION = 0.8f
private const val EXIT_HINT_MILLIS = 2500L

@Composable
fun DisplayScreen(text: String, font: AppFont, colors: DisplayColors, rotation: RotationMode, showExitHint: Boolean, onExit: () -> Unit) {
    val activity = LocalActivity.current!!
    val view = LocalView.current
    DisposableEffect(rotation) {
        val insets = WindowCompat.getInsetsController(activity.window, view)
        insets.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insets.hide(WindowInsetsCompat.Type.systemBars())
        view.keepScreenOn = true
        activity.requestedOrientation = rotation.screenOrientation
        onDispose {
            insets.show(WindowInsetsCompat.Type.systemBars())
            view.keepScreenOn = false
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
    Box(Modifier.fillMaxSize()) {
        DisplayContent(text, font, colors)
        Box(
            Modifier
                .fillMaxSize(TAP_AREA_FRACTION)
                .align(Alignment.Center)
                .pointerInput(onExit) { detectTapGestures { onExit() } },
        )
        if (showExitHint) ExitHint(colors, Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun ExitHint(colors: DisplayColors, modifier: Modifier) {
    var visible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(EXIT_HINT_MILLIS)
        visible = false
    }
    AnimatedVisibility(visible, modifier.padding(32.dp), exit = fadeOut()) {
        Text("Tap to close", color = colors.textColor.copy(alpha = 0.6f), fontSize = 20.sp)
    }
}

private val RotationMode.screenOrientation: Int
    get() = when (this) {
        RotationMode.Landscape -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        RotationMode.Portrait -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        RotationMode.Auto -> ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }
