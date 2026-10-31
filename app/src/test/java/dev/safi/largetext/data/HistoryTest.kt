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
            val displayed = matching.single()
            if (displayed.pinned) {
                assertEquals(history.filter { it.pinned }.map { it.id }, result.filter { it.pinned }.map { it.id })
            } else {
                assertEquals(text, result.first { !it.pinned }.text)
            }
        }
    }

    @Test
    fun redisplayingPinnedKeepsItsPlace() {
        val history = listOf(HistoryEntry("a", "first", 1, pinned = true), HistoryEntry("b", "second", 2, pinned = true))
        val result = history.withDisplayed("second", now = 9, ::newId)
        assertEquals(listOf("a", "b"), result.map { it.id })
        assertEquals(9L, result[1].createdAt)
    }

    @Test
    fun pinnedMoveById() {
        val history = listOf(HistoryEntry("a", "x", 1, true), HistoryEntry("b", "y", 2, true), HistoryEntry("c", "z", 3))
        assertEquals(listOf("b", "a", "c"), history.moved("a", "b").map { it.id })
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
    fun pinUnpinAndDeleteSeveral() {
        val history = listOf(HistoryEntry("a", "x", 1), HistoryEntry("b", "y", 2), HistoryEntry("c", "z", 3, pinned = true))
        assertEquals(listOf(true, true, true), history.withPinned(setOf("a", "b"), true).map { it.pinned })
        assertEquals(listOf(false, false, false), history.withPinned(setOf("a", "c"), false).map { it.pinned })
        assertEquals(listOf("b"), history.without(setOf("a", "c")).map { it.id })
    }

    @Test
    fun searchMatchesContentIgnoringCase() {
        val history = listOf(HistoryEntry("a", "Hello world", 1), HistoryEntry("b", "Gate 12", 2))
        assertEquals(listOf("a"), history.matching("WORLD").map { it.id })
        assertEquals(listOf("a", "b"), history.matching("").map { it.id })
        assertEquals(emptyList<String>(), history.matching("xyz").map { it.id })
    }

    @Test
    fun searchResultsAlwaysContainQuery() = runBlocking<Unit> {
        checkAll(200, histories, Arb.element(listOf("", "text", "1", "TEXT2", "9"))) { history, query ->
            val result = history.matching(query)
            assertTrue(result.all { it.text.contains(query, ignoreCase = true) })
            assertEquals(history.count { it.text.contains(query, ignoreCase = true) }, result.size)
        }
    }
}
