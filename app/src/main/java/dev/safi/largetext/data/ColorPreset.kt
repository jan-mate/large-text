package dev.safi.largetext.data

import kotlinx.serialization.Serializable

@Serializable
data class ColorPreset(val id: String, val colors: DisplayColors)

fun List<ColorPreset>.withPinned(colors: DisplayColors, newId: () -> String): List<ColorPreset> =
    if (any { it.colors == colors }) this else this + ColorPreset(newId(), colors)

fun List<ColorPreset>.without(ids: Set<String>) = filter { it.id !in ids }

fun List<ColorPreset>.moved(fromId: String, toId: String) =
    moved(indexOfFirst { it.id == fromId }, indexOfFirst { it.id == toId })
