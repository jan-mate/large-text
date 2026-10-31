package dev.safi.largetext.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import dev.safi.largetext.ui.display.ScaledToDisplay

@Composable
fun PreviewCell(
    displaySize: IntSize,
    selected: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(Modifier.padding(4.dp)) {
        ScaledToDisplay(
            displaySize = displaySize,
            modifier = Modifier
                .aspectRatio(displaySize.width.toFloat() / displaySize.height)
                .border(if (selected) 3.dp else 1.dp, if (selected) Color.White else OutlineGrey)
                .then(modifier),
            content = content,
        )
        if (selected) {
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier.align(Alignment.TopEnd).padding(6.dp).background(Color.Black, CircleShape),
            )
        }
    }
}
