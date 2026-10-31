package dev.safi.largetext.ui

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import dev.safi.largetext.R
import dev.safi.largetext.data.AppFont

private val AppFont.resource: Int
    get() = when (this) {
        AppFont.Safi -> R.font.safi
        AppFont.Atkinson -> R.font.atkinson_hyperlegible_next
        AppFont.Inter -> R.font.inter
        AppFont.Lato -> R.font.lato
        AppFont.Montserrat -> R.font.montserrat
        AppFont.OpenSans -> R.font.open_sans
        AppFont.Poppins -> R.font.poppins
        AppFont.Oswald -> R.font.oswald
        AppFont.BebasNeue -> R.font.bebas_neue
        AppFont.Merriweather -> R.font.merriweather
        AppFont.Lora -> R.font.lora
        AppFont.ComicNeue -> R.font.comic_neue
        AppFont.JetBrainsMono -> R.font.jetbrains_mono
        AppFont.Caveat -> R.font.caveat
    }

private val families = AppFont.entries.associateWith { FontFamily(Font(it.resource)) }

val AppFont.fontFamily: FontFamily
    get() = families.getValue(this)
