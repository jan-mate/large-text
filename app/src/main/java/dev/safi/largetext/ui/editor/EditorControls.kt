package dev.safi.largetext.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.ScreenRotation
import androidx.compose.material.icons.outlined.StayCurrentLandscape
import androidx.compose.material.icons.outlined.StayCurrentPortrait
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.safi.largetext.data.AppFont
import dev.safi.largetext.data.DisplayColors
import dev.safi.largetext.data.RotationMode
import dev.safi.largetext.ui.OutlineGrey
import dev.safi.largetext.ui.display.backgroundColor
import dev.safi.largetext.ui.display.textColor
import dev.safi.largetext.ui.fontFamily

@Composable
fun EditorControls(
    font: AppFont,
    colors: DisplayColors,
    rotation: RotationMode,
    onOpenHistory: () -> Unit,
    onOpenFonts: () -> Unit,
    onOpenColors: () -> Unit,
    onCycleRotation: () -> Unit,
) {
    IconButton(onClick = onOpenHistory) {
        Icon(Icons.Outlined.History, contentDescription = "History")
    }
    IconButton(onClick = onOpenFonts, modifier = Modifier.semantics { contentDescription = "Font" }) {
        Text("Aa", fontFamily = font.fontFamily, fontSize = 22.sp)
    }
    IconButton(onClick = onOpenColors, modifier = Modifier.semantics { contentDescription = "Colors" }) {
        val shape = RoundedCornerShape(4.dp)
        Box(
            Modifier.size(28.dp).background(colors.backgroundColor, shape).border(1.dp, OutlineGrey, shape),
            contentAlignment = Alignment.Center,
        ) {
            Text("A", color = colors.textColor, fontSize = 18.sp)
        }
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
