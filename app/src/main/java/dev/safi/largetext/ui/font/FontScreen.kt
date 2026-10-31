package dev.safi.largetext.ui.font

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import dev.safi.largetext.data.AppFont
import dev.safi.largetext.data.DisplayColors
import dev.safi.largetext.ui.PreviewCell
import dev.safi.largetext.ui.display.DisplayContent

@Composable
fun FontScreen(current: AppFont, colors: DisplayColors, displaySize: IntSize, onChoose: (AppFont) -> Unit) {
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.safeDrawingPadding(),
            contentPadding = PaddingValues(12.dp),
        ) {
            items(AppFont.entries) { font ->
                PreviewCell(displaySize, selected = font == current, modifier = Modifier.clickable { onChoose(font) }) {
                    DisplayContent(font.displayName, font, colors)
                }
            }
        }
    }
}
