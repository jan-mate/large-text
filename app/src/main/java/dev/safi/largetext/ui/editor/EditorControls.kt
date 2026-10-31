package dev.safi.largetext.ui.editor

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.ScreenRotation
import androidx.compose.material.icons.outlined.StayCurrentLandscape
import androidx.compose.material.icons.outlined.StayCurrentPortrait
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import dev.safi.largetext.data.AppFont
import dev.safi.largetext.data.RotationMode
import dev.safi.largetext.ui.fontFamily

@Composable
fun EditorControls(
    font: AppFont,
    rotation: RotationMode,
    onOpenHistory: () -> Unit,
    onToggleFont: () -> Unit,
    onToggleTheme: () -> Unit,
    onCycleRotation: () -> Unit,
) {
    IconButton(onClick = onOpenHistory) {
        Icon(Icons.Outlined.History, contentDescription = "History")
    }
    IconButton(onClick = onToggleFont) {
        Text("Aa", fontFamily = font.fontFamily, fontSize = 22.sp)
    }
    IconButton(onClick = onToggleTheme) {
        Icon(Icons.Outlined.Contrast, contentDescription = "Light or dark")
    }
    IconButton(onClick = onCycleRotation) {
        val icon = when (rotation) {
            RotationMode.Landscape -> Icons.Outlined.StayCurrentLandscape
            RotationMode.Portrait -> Icons.Outlined.StayCurrentPortrait
            RotationMode.Auto -> Icons.Outlined.ScreenRotation
        }
        Icon(icon, contentDescription = "Rotation: ${rotation.name}")
    }
}
