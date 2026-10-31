package dev.safi.largetext.fit

class CachedTextFitter(private val fitter: TextFitter, private val capacity: Int = 256) : TextFitter {
    private data class Key(val text: String, val boxWidth: Float, val boxHeight: Float)

    private val cache = object : LinkedHashMap<Key, Float>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Key, Float>) = size > capacity
    }

    override fun fit(text: String, boxWidth: Float, boxHeight: Float): Float =
        cache.getOrPut(Key(text, boxWidth, boxHeight)) { fitter.fit(text, boxWidth, boxHeight) }
}
