package dev.safi.largetext.fit

import org.junit.Assert.assertEquals
import org.junit.Test

class CachedTextFitterTest {
    private class CountingFitter : TextFitter {
        var calls = 0
        override fun fit(text: String, boxWidth: Float, boxHeight: Float): Float {
            calls++
            return text.length + boxWidth + boxHeight
        }
    }

    @Test
    fun sameInputIsComputedOnce() {
        val counting = CountingFitter()
        val cached = CachedTextFitter(counting)
        assertEquals(cached.fit("hi", 10f, 20f), cached.fit("hi", 10f, 20f))
        assertEquals(1, counting.calls)
    }

    @Test
    fun differentBoxIsComputedAgain() {
        val counting = CountingFitter()
        val cached = CachedTextFitter(counting)
        cached.fit("hi", 10f, 20f)
        assertEquals(2f + 11f + 20f, cached.fit("hi", 11f, 20f))
        assertEquals(2, counting.calls)
    }

    @Test
    fun evictsOldestBeyondCapacity() {
        val counting = CountingFitter()
        val cached = CachedTextFitter(counting, capacity = 2)
        cached.fit("a", 1f, 1f)
        cached.fit("b", 1f, 1f)
        cached.fit("c", 1f, 1f)
        cached.fit("a", 1f, 1f)
        assertEquals(4, counting.calls)
    }
}
