package dev.safi.largetext.fit

class BinarySearchTextFitter(
    private val measure: TextMeasure,
    private val minFontSize: Float = 1f,
    private val precision: Float = 0.5f,
    private val minGainToBreakWords: Float = 2f,
    private val minWordLengthToBreak: Int = 8,
) : TextFitter {
    override fun fit(text: String, boxWidth: Float, boxHeight: Float): Float {
        val whole = largestFitting(text, boxWidth, boxHeight, allowBreak = false)
        if (whole != null && longestWordLength(text) < minWordLengthToBreak) return whole
        val broken = largestFitting(text, boxWidth, boxHeight, allowBreak = true) ?: return minFontSize
        return if (whole == null || broken >= whole * minGainToBreakWords) broken else whole
    }

    private fun longestWordLength(text: String) = text.split(' ', '\n').maxOf { it.length }

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
