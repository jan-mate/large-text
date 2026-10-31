package dev.safi.largetext.data

import kotlinx.serialization.Serializable

@Serializable
data class HistoryEntry(val id: String, val text: String, val createdAt: Long, val pinned: Boolean = false)
