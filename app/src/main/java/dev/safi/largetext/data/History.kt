package dev.safi.largetext.data

const val MAX_UNPINNED = 200

fun List<HistoryEntry>.withDisplayed(text: String, now: Long, newId: () -> String): List<HistoryEntry> {
    val entry = find { it.text == text }?.copy(createdAt = now) ?: HistoryEntry(newId(), text, now)
    val (pinned, unpinned) = (listOf(entry) + filter { it.text != text }).partition { it.pinned }
    return pinned + unpinned.take(MAX_UNPINNED)
}

fun List<HistoryEntry>.withPinToggled(id: String): List<HistoryEntry> =
    map { if (it.id == id) it.copy(pinned = !it.pinned) else it }

fun List<HistoryEntry>.without(id: String): List<HistoryEntry> = filter { it.id != id }
