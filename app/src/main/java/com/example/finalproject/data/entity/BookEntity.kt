package com.example.finalproject.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "author")
    val author: String,

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo(name = "cover_url")
    val coverUrl: String = "",

    @ColumnInfo(name = "publish_year")
    val publishYear: String = "",

    @ColumnInfo(name = "publisher")
    val publisher: String = "",

    @ColumnInfo(name = "isbn")
    val isbn: String = "",

    @ColumnInfo(name = "page_count")
    val pageCount: Int = 0,

    @ColumnInfo(name = "rating")
    val rating: Float = 0f,

    @ColumnInfo(name = "notes")
    val notes: String = "",

    @ColumnInfo(name = "reading_status")
    val readingStatus: String = "want_to_read",

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
