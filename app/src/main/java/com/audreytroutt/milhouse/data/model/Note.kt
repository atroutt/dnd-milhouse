package com.audreytroutt.milhouse.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val characterId: Long,
    val title: String,
    val content: String,
    val tags: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun tagList(): List<String> = tags
        .split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
}
