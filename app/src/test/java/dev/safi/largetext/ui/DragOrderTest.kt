package dev.safi.largetext.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class DragOrderTest {
    @Test
    fun stepwiseMovesGiveOneMoveTarget() {
        val order = DragOrder(listOf("a", "b", "c", "d"))
        order.move("a", "b")
        order.move("a", "c")
        assertEquals(listOf("b", "c", "a", "d"), order.ids)
        assertEquals("c", order.targetOf("a"))
    }

    @Test
    fun unknownKeysAreIgnored() {
        val order = DragOrder(listOf("a", "b"))
        order.move("a", "header")
        assertEquals(listOf("a", "b"), order.ids)
        assertEquals("a", order.targetOf("a"))
    }
}
