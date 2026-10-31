package dev.safi.largetext.ui.display

import android.content.pm.ActivityInfo
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import dev.safi.largetext.data.AppFont
import dev.safi.largetext.data.DisplayTheme
import dev.safi.largetext.data.RotationMode

@Composable
fun DisplayScreen(text: String, font: AppFont, theme: DisplayTheme, rotation: RotationMode) {
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
    DisplayContent(text, font, theme)
}

private val RotationMode.screenOrientation: Int
    get() = when (this) {
        RotationMode.Landscape -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        RotationMode.Portrait -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        RotationMode.Auto -> ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }
