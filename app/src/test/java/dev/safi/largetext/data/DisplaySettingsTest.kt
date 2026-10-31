package dev.safi.largetext.data

import org.junit.Assert.assertEquals
import org.junit.Test

class DisplaySettingsTest {
    @Test
    fun rotationCyclesAutoLandscapePortrait() {
        val cycle = generateSequence(RotationMode.Auto) { it.next(RotationMode.entries.toTypedArray()) }.take(4).toList()
        assertEquals(listOf(RotationMode.Auto, RotationMode.Landscape, RotationMode.Portrait, RotationMode.Auto), cycle)
    }

    @Test
    fun fontAndThemeToggle() {
        assertEquals(AppFont.Atkinson, AppFont.Safi.next(AppFont.entries.toTypedArray()))
        assertEquals(AppFont.Safi, AppFont.Atkinson.next(AppFont.entries.toTypedArray()))
        assertEquals(DisplayTheme.Light, DisplayTheme.Dark.next(DisplayTheme.entries.toTypedArray()))
    }

    @Test
    fun autoRotationIsDefault() {
        assertEquals(RotationMode.Auto, Settings().rotation)
    }
}
