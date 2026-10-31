package dev.safi.largetext.data

fun <T> List<T>.moved(fromIndex: Int, toIndex: Int): List<T> =
    toMutableList().apply { add(toIndex, removeAt(fromIndex)) }
