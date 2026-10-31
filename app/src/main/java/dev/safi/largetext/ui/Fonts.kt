package dev.safi.largetext.ui

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import dev.safi.largetext.R
import dev.safi.largetext.data.AppFont

private val safi = FontFamily(Font(R.font.safi))
private val atkinson = FontFamily(Font(R.font.atkinson_hyperlegible_next))

val AppFont.fontFamily: FontFamily
    get() = when (this) {
        AppFont.Safi -> safi
        AppFont.Atkinson -> atkinson
    }
