package dev.safi.largetext.data

import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ListMovesTest {
    @Test
    fun movedPutsItemAtTargetAndKeepsTheRestInOrder() = runBlocking<Unit> {
        checkAll(300, Arb.list(Arb.int(), 1..20), Arb.int(0..100), Arb.int(0..100)) { items, a, b ->
            val from = a % items.size
            val to = b % items.size
            val result = items.moved(from, to)
            assertEquals(items[from], result[to])
            assertEquals(items.filterIndexed { i, _ -> i != from }, result.filterIndexed { i, _ -> i != to })
        }
    }

    @Test
    fun movesForwardAndBackward() {
        assertEquals(listOf("b", "c", "a", "d"), listOf("a", "b", "c", "d").moved(0, 2))
        assertEquals(listOf("a", "d", "b", "c"), listOf("a", "b", "c", "d").moved(3, 1))
    }
}
