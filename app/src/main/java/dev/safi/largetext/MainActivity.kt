package dev.safi.largetext

import android.widget.Toast
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.datastore.dataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.safi.largetext.ui.AppTheme
import dev.safi.largetext.ui.AppViewModel
import dev.safi.largetext.ui.display.DisplayScreen
import dev.safi.largetext.ui.display.rememberDisplaySize
import dev.safi.largetext.data.ColorPresetSerializer
import dev.safi.largetext.data.DataStoreListRepository
import dev.safi.largetext.data.DataStoreSettingsRepository
import dev.safi.largetext.data.HistorySerializer
import dev.safi.largetext.ui.ColorPresetsViewModel
import dev.safi.largetext.ui.color.ColorScreen
import dev.safi.largetext.ui.font.FontScreen
import dev.safi.largetext.ui.editor.EditorControls
import dev.safi.largetext.ui.editor.EditorScreen
import dev.safi.largetext.ui.history.HistoryScreen
import kotlinx.serialization.Serializable

private val Context.settingsDataStore by preferencesDataStore("settings")
private val Context.historyDataStore by dataStore("history.json", HistorySerializer)
private val Context.colorPresetsDataStore by dataStore("color_presets.json", ColorPresetSerializer)

@Serializable
object EditorRoute

@Serializable
object DisplayRoute

@Serializable
object HistoryRoute

@Serializable
object FontRoute

@Serializable
object ColorRoute

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels {
        viewModelFactory {
            initializer {
                AppViewModel(
                    DataStoreSettingsRepository(applicationContext.settingsDataStore),
                    DataStoreListRepository(applicationContext.historyDataStore),
                )
            }
        }
    }

    private val colorPresets: ColorPresetsViewModel by viewModels {
        viewModelFactory { initializer { ColorPresetsViewModel(DataStoreListRepository(applicationContext.colorPresetsDataStore)) } }
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
                            colors = viewModel.colors,
                            rotation = viewModel.rotation,
                            onTextChange = viewModel::changeText,
                            onDisplay = {
                                viewModel.display()
                                navController.navigate(DisplayRoute)
                            },
                            onResume = viewModel::selectAll,
                            controls = {
                                EditorControls(
                                    font = viewModel.font,
                                    colors = viewModel.colors,
                                    rotation = viewModel.rotation,
                                    onOpenHistory = { navController.navigate(HistoryRoute) },
                                    onOpenFonts = { navController.navigate(FontRoute) },
                                    onOpenColors = { navController.navigate(ColorRoute) },
                                    onCycleRotation = {
                                        viewModel.cycleRotation()
                                        Toast.makeText(this@MainActivity, "Rotation: ${viewModel.rotation.name}", Toast.LENGTH_SHORT).show()
                                    },
                                )
                            },
                        )
                    }
                    composable<HistoryRoute> {
                        HistoryScreen(
                            entries = viewModel.history.collectAsStateWithLifecycle().value,
                            displaySize = rememberDisplaySize(viewModel.rotation),
                            onOpen = { entry ->
                                viewModel.display(entry)
                                navController.navigate(DisplayRoute) { popUpTo(EditorRoute) }
                            },
                            onSetPinned = viewModel::setPinned,
                            onDelete = viewModel::delete,
                            onMove = viewModel::moveHistory,
                        )
                    }
                    composable<FontRoute> {
                        FontScreen(
                            current = viewModel.font,
                            colors = viewModel.colors,
                            displaySize = rememberDisplaySize(viewModel.rotation),
                            onChoose = { font ->
                                viewModel.chooseFont(font)
                                navController.popBackStack()
                            },
                        )
                    }
                    composable<ColorRoute> {
                        ColorScreen(
                            text = viewModel.text.text,
                            font = viewModel.font,
                            colors = viewModel.colors,
                            presets = colorPresets.presets.collectAsStateWithLifecycle().value,
                            displaySize = rememberDisplaySize(viewModel.rotation),
                            onTextColor = viewModel::setTextColor,
                            onBackgroundColor = viewModel::setBackgroundColor,
                            onInvert = viewModel::invertColors,
                            onApply = viewModel::applyColors,
                            onPin = colorPresets::pin,
                            onDelete = colorPresets::delete,
                            onMove = colorPresets::move,
                        )
                    }
                    composable<DisplayRoute> {
                        DisplayScreen(
                            text = viewModel.text.text,
                            font = viewModel.font,
                            colors = viewModel.colors,
                            rotation = viewModel.rotation,
                            showExitHint = viewModel.showExitHint,
                            onExit = dropUnlessResumed { navController.popBackStack() },
                        )
                    }
                }
            }
        }
    }
}
