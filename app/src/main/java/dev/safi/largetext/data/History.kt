package dev.safi.largetext.data

const val MAX_UNPINNED = 200

fun List<HistoryEntry>.withDisplayed(text: String, now: Long, newId: () -> String): List<HistoryEntry> {
    val entry = find { it.text == text }?.copy(createdAt = now) ?: HistoryEntry(newId(), text, now)
    val (pinned, unpinned) = (listOf(entry) + filter { it.text != text }).partition { it.pinned }
    return pinned + unpinned.take(MAX_UNPINNED)
}

fun List<HistoryEntry>.withPinned(ids: Set<String>, pinned: Boolean) =
    map { if (it.id in ids) it.copy(pinned = pinned) else it }

fun List<HistoryEntry>.without(ids: Set<String>) = filter { it.id !in ids }

fun List<HistoryEntry>.matching(query: String) = filter { it.text.contains(query, ignoreCase = true) }
