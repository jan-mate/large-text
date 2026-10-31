package dev.safi.largetext.fit

class BinarySearchTextFitter(
    private val measure: TextMeasure,
    private val minFontSize: Float = 1f,
    private val precision: Float = 0.5f,
) : TextFitter {
    override fun fit(text: String, boxWidth: Float, boxHeight: Float): Float =
        largestFitting(text, boxWidth, boxHeight, allowBreak = false)
            ?: largestFitting(text, boxWidth, boxHeight, allowBreak = true)
            ?: minFontSize

    private fun largestFitting(text: String, boxWidth: Float, boxHeight: Float, allowBreak: Boolean): Float? {
        fun fits(fontSize: Float): Boolean {
            val result = measure.measure(text, fontSize, boxWidth)
            return result.width <= boxWidth && result.height <= boxHeight && (allowBreak || !result.brokeMidWord)
        }
        if (!fits(minFontSize)) return null
        var low = minFontSize
        var high = boxHeight
        while (high - low > precision) {
            val middle = (low + high) / 2
            if (fits(middle)) low = middle else high = middle
        }
        return low
    }
}
