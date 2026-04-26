package com.audreytroutt.milhouse.data.model

data class Note(
    val id: Long = 0,
    val characterId: Long = 0,
    val title: String,
    val content: String,
    val tags: String = "",
    val createdAt: Long = currentTimeMillis(),
    val updatedAt: Long = currentTimeMillis()
) {
    fun tagList(): List<String> = tags
        .split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
}

// expect/actual so iOS can provide its own clock if needed
expect fun currentTimeMillis(): Long
