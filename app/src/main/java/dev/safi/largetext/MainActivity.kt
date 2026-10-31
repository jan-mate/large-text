package dev.safi.largetext

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.safi.largetext.ui.AppTheme
import dev.safi.largetext.ui.AppViewModel
import dev.safi.largetext.ui.display.DisplayScreen
import dev.safi.largetext.data.DataStoreSettingsRepository
import dev.safi.largetext.ui.editor.EditorControls
import dev.safi.largetext.ui.editor.EditorScreen
import kotlinx.serialization.Serializable

private val Context.settingsDataStore by preferencesDataStore("settings")

@Serializable
object EditorRoute

@Serializable
object DisplayRoute

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels {
        viewModelFactory { initializer { AppViewModel(DataStoreSettingsRepository(applicationContext.settingsDataStore)) } }
    }

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
                            controls = {
                                EditorControls(
                                    font = viewModel.font,
                                    rotation = viewModel.rotation,
                                    onToggleFont = viewModel::toggleFont,
                                    onToggleTheme = viewModel::toggleTheme,
                                    onCycleRotation = viewModel::cycleRotation,
                                )
                            },
                        )
                    }
                    composable<DisplayRoute> {
                        DisplayScreen(
                            text = viewModel.text.text,
                            font = viewModel.font,
                            theme = viewModel.theme,
                            rotation = viewModel.rotation,
                            onExit = dropUnlessResumed { navController.popBackStack() },
                        )
                    }
                }
            }
        }
    }
}
