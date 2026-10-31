package dev.safi.largetext.ui.color

private val hexPattern = Regex("#?[0-9a-fA-F]{6}")

fun Int.toHex(): String = "#%06X".format(this and 0xFFFFFF)

fun parseHexColor(text: String): Int? =
    if (hexPattern.matches(text)) text.removePrefix("#").toInt(16) or 0xFF000000.toInt() else null
