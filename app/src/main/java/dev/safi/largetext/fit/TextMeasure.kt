package dev.safi.largetext.fit

data class Measurement(val width: Float, val height: Float, val brokeMidWord: Boolean)

interface TextMeasure {
    fun measure(text: String, fontSize: Float, maxWidth: Float): Measurement
}
