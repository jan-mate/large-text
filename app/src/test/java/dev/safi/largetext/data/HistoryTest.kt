package dev.safi.largetext.data

import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HistoryTest {
    private var nextId = 0
    private fun newId() = "id${nextId++}"

    private val texts = Arb.element((1..300).map { "text$it" })
    private val histories = Arb.list(Arb.int(1..300), 0..260).let { numbers ->
        Arb.bind(numbers, Arb.list(Arb.boolean(), 260..260)) { ns: List<Int>, pins: List<Boolean> ->
            ns.distinct().mapIndexed { i, n -> HistoryEntry("old$n", "text$n", (1000 - i).toLong(), pins[i]) }
        }
    }

    @Test
    fun displayedTextAppearsOnceAndIsNewest() = runBlocking<Unit> {
        checkAll(300, histories, texts) { history, text ->
            val result = history.withDisplayed(text, now = 5000, ::newId)
            val matching = result.filter { it.text == text }
            assertEquals(1, matching.size)
            assertEquals(5000L, matching.single().createdAt)
            val section = result.filter { it.pinned == matching.single().pinned }
            assertEquals(text, section.first().text)
        }
    }

    @Test
    fun unpinnedAreCappedAndPinnedNeverExpire() = runBlocking<Unit> {
        checkAll(300, histories, texts) { history, text ->
            val result = history.withDisplayed(text, now = 5000, ::newId)
            assertTrue(result.count { !it.pinned } <= MAX_UNPINNED)
            assertTrue(result.map { it.id }.containsAll(history.filter { it.pinned }.map { it.id }))
            assertEquals(result.size, result.map { it.text }.toSet().size)
        }
    }

    @Test
    fun redisplayKeepsIdAndPin() {
        val history = listOf(HistoryEntry("a", "first", 1, pinned = true), HistoryEntry("b", "second", 2))
        val result = history.withDisplayed("first", now = 9, ::newId)
        assertEquals(HistoryEntry("a", "first", 9, pinned = true), result.single { it.text == "first" })
    }

    @Test
    fun keepsTheNewestUnpinned() {
        val history = (1..MAX_UNPINNED).map { HistoryEntry("$it", "t$it", (1000 - it).toLong()) }
        val result = history.withDisplayed("new", now = 5000, ::newId)
        assertEquals(listOf("new") + (1 until MAX_UNPINNED).map { "t$it" }, result.map { it.text })
    }

    @Test
    fun pinToggleAndDelete() {
        val history = listOf(HistoryEntry("a", "x", 1), HistoryEntry("b", "y", 2))
        assertEquals(listOf(HistoryEntry("a", "x", 1, pinned = true), HistoryEntry("b", "y", 2)), history.withPinToggled("a"))
        assertEquals(listOf(HistoryEntry("b", "y", 2)), history.without("a"))
    }
}
