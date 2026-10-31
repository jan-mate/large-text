package dev.safi.largetext

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.safi.largetext.ui.AppTheme
import dev.safi.largetext.ui.AppViewModel
import dev.safi.largetext.ui.display.DisplayScreen
import dev.safi.largetext.ui.editor.EditorScreen
import kotlinx.serialization.Serializable

@Serializable
object EditorRoute

@Serializable
object DisplayRoute

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = EditorRoute) {
                    composable<EditorRoute> {
                        EditorScreen(
                            text = viewModel.text,
                            font = viewModel.font,
                            theme = viewModel.theme,
                            rotation = viewModel.rotation,
                            onTextChange = viewModel::changeText,
                            onDisplay = { navController.navigate(DisplayRoute) },
                            onResume = viewModel::selectAll,
                            topBar = {},
                        )
                    }
                    composable<DisplayRoute> {
                        DisplayScreen(viewModel.text.text, viewModel.font, viewModel.theme, viewModel.rotation)
                    }
                }
            }
        }
    }
}
