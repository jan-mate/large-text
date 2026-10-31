package dev.safi.largetext.data

enum class AppFont { Safi, Atkinson }

enum class DisplayTheme { Dark, Light }

enum class RotationMode { Auto, Landscape, Portrait }

data class Settings(
    val font: AppFont = AppFont.Safi,
    val theme: DisplayTheme = DisplayTheme.Dark,
    val rotation: RotationMode = RotationMode.Auto,
    val lastText: String = "",
)

fun <T : Enum<T>> T.next(values: Array<T>): T = values[(ordinal + 1) % values.size]
