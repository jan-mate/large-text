package dev.safi.largetext.ui.display

import android.content.pm.ActivityInfo
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.gestures.detectTapGestures
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
import dev.safi.largetext.data.DisplayTheme
import dev.safi.largetext.data.RotationMode

private const val TAP_AREA_FRACTION = 0.8f

@Composable
fun DisplayScreen(text: String, font: AppFont, theme: DisplayTheme, rotation: RotationMode, onExit: () -> Unit) {
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
        DisplayContent(text, font, theme)
        Box(
            Modifier
                .fillMaxSize(TAP_AREA_FRACTION)
                .align(Alignment.Center)
                .pointerInput(onExit) { detectTapGestures { onExit() } },
        )
    }
}

private val RotationMode.screenOrientation: Int
    get() = when (this) {
        RotationMode.Landscape -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        RotationMode.Portrait -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        RotationMode.Auto -> ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }
