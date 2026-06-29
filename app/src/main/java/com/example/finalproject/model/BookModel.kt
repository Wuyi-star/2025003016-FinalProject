package com.example.finalproject.model

/**
 * 用于 UI 层展示的图书模型
 */
data class BookModel(
    val id: Long = 0,
    val title: String,
    val author: String,
    val description: String = "",
    val coverUrl: String = "",
    val publishYear: String = "",
    val publisher: String = "",
    val isbn: String = "",
    val pageCount: Int = 0,
    val rating: Float = 0f,
    val notes: String = "",
    val readingStatus: String = "want_to_read",
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
