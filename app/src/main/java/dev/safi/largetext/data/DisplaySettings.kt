package dev.safi.largetext.data

enum class AppFont(val displayName: String) {
    Safi("safi"),
    Atkinson("Atkinson Hyperlegible"),
    Inter("Inter"),
    Lato("Lato"),
    Montserrat("Montserrat"),
    OpenSans("Open Sans"),
    Poppins("Poppins"),
    Oswald("Oswald"),
    BebasNeue("Bebas Neue"),
    Merriweather("Merriweather"),
    Lora("Lora"),
    ComicNeue("Comic Neue"),
    JetBrainsMono("JetBrains Mono"),
    Caveat("Caveat"),
}

enum class RotationMode { Auto, Landscape, Portrait }

data class Settings(
    val font: AppFont = AppFont.Safi,
    val colors: DisplayColors = DisplayColors.WhiteOnBlack,
    val rotation: RotationMode = RotationMode.Auto,
    val lastText: String = "",
)

fun <T : Enum<T>> T.next(values: Array<T>): T = values[(ordinal + 1) % values.size]
