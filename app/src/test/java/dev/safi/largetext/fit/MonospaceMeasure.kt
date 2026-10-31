package dev.safi.largetext.fit

class MonospaceMeasure(
    private val charWidth: Float = 0.6f,
    private val lineHeight: Float = 1.2f,
) : TextMeasure {
    override fun measure(text: String, fontSize: Float, maxWidth: Float): Measurement {
        val maxChars = maxOf(1, (maxWidth / (charWidth * fontSize)).toInt())
        var brokeMidWord = false
        val lines = text.split("\n").flatMap { paragraph ->
            val wrapped = mutableListOf<String>()
            var line = ""
            for (word in paragraph.split(" ")) {
                var rest = if (line.isEmpty()) word else "$line $word"
                if (rest.length > maxChars && line.isNotEmpty()) {
                    wrapped += line
                    rest = word
                }
                while (rest.length > maxChars) {
                    brokeMidWord = true
                    wrapped += rest.take(maxChars)
                    rest = rest.drop(maxChars)
                }
                line = rest
            }
            wrapped + line
        }
        val longest = lines.maxOf { it.length }
        return Measurement(longest * charWidth * fontSize, lines.size * lineHeight * fontSize, brokeMidWord)
    }
}
