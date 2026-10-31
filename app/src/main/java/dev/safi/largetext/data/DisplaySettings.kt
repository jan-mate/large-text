package dev.safi.largetext.data

enum class AppFont { Safi, Atkinson }

enum class DisplayTheme { Dark, Light }

enum class RotationMode { Landscape, Portrait, Auto }

data class Settings(
    val font: AppFont = AppFont.Safi,
    val theme: DisplayTheme = DisplayTheme.Dark,
    val rotation: RotationMode = RotationMode.Landscape,
    val lastText: String = "",
)

fun <T : Enum<T>> T.next(values: Array<T>): T = values[(ordinal + 1) % values.size]
