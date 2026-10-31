package dev.safi.largetext.fit

interface TextFitter {
    fun fit(text: String, boxWidth: Float, boxHeight: Float): Float
}
