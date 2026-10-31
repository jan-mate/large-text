package dev.safi.largetext.data

import kotlinx.serialization.Serializable

@Serializable
data class DisplayColors(val text: Int, val background: Int) {
    fun inverted() = DisplayColors(background, text)

    companion object {
        val WhiteOnBlack = DisplayColors(0xFFFFFFFF.toInt(), 0xFF000000.toInt())
        val BlackOnWhite = WhiteOnBlack.inverted()
    }
}
