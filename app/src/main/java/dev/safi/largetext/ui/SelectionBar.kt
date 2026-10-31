package dev.safi.largetext.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp

@Composable
fun SelectionBar(count: Int, onClear: () -> Unit, actions: @Composable RowScope.() -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onClear) {
            Icon(Icons.Outlined.Close, contentDescription = "Cancel selection")
        }
        Text("$count selected", fontSize = 20.sp, modifier = Modifier.weight(1f))
        actions()
    }
}

@Composable
fun BarButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Icon(icon, contentDescription = null, tint = Color.White)
        Text(" $label", color = Color.White, fontSize = 18.sp)
    }
}

fun Set<String>.toggled(id: String) = if (id in this) this - id else this + id
