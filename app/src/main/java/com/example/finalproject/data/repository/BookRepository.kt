package com.example.finalproject.data.repository

import com.example.finalproject.data.dao.BookDao
import com.example.finalproject.data.dao.SearchHistoryDao
import com.example.finalproject.data.entity.BookEntity
import com.example.finalproject.data.entity.SearchHistoryEntity
import com.example.finalproject.data.network.RetrofitClient
import com.example.finalproject.data.network.dto.OpenLibraryDoc
import com.example.finalproject.model.BookModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookRepository(
    private val bookDao: BookDao,
    private val searchHistoryDao: SearchHistoryDao
) {

    // ========== 本地数据操作 ==========

    fun getAllBooks(): Flow<List<BookModel>> {
        return bookDao.getAllBooks().map { entities ->
            entities.map { it.toBookModel() }
        }
    }

    fun getBooksByStatus(status: String): Flow<List<BookModel>> {
        return bookDao.getBooksByStatus(status).map { entities ->
            entities.map { it.toBookModel() }
        }
    }

    fun searchLocalBooks(query: String): Flow<List<BookModel>> {
        return bookDao.searchBooks(query).map { entities ->
            entities.map { it.toBookModel() }
        }
    }

    suspend fun getBookById(id: Long): BookModel? {
        return bookDao.getBookById(id)?.toBookModel()
    }

    suspend fun insertBook(book: BookModel): Long {
        return bookDao.insertBook(book.toEntity())
    }

    suspend fun updateBook(book: BookModel) {
        bookDao.updateBook(book.copy(updatedAt = System.currentTimeMillis()).toEntity())
    }

    suspend fun deleteBook(book: BookModel) {
        bookDao.deleteBook(book.toEntity())
    }

    suspend fun deleteBookById(id: Long) {
        bookDao.deleteBookById(id)
    }

    suspend fun getBookCount(): Int = bookDao.getBookCount()

    // ========== 收藏功能 ==========

    fun getFavoriteBooks(): Flow<List<BookModel>> {
        return bookDao.getFavoriteBooks().map { entities ->
            entities.map { it.toBookModel() }
        }
    }

    suspend fun toggleFavorite(bookId: Long, isFavorite: Boolean) {
        bookDao.updateFavoriteStatus(bookId, isFavorite)
    }

    // ========== 搜索历史 ==========

    fun getSearchHistory(): Flow<List<SearchHistoryEntity>> {
        return searchHistoryDao.getAllHistory()
    }

    suspend fun saveSearchHistory(query: String) {
        searchHistoryDao.insertHistory(SearchHistoryEntity(query = query))
    }

    suspend fun clearSearchHistory() {
        searchHistoryDao.clearAllHistory()
    }

    // ========== 网络数据操作 ==========

    suspend fun searchOnlineBooks(query: String): Result<List<BookModel>> {
        return try {
            val response = RetrofitClient.apiService.searchBooks(query)
            val books = response.docs.map { it.toBookModel() }
            Result.success(books)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== 转换方法 ==========

    private fun BookEntity.toBookModel(): BookModel {
        return BookModel(
            id = id, title = title, author = author,
            description = description, coverUrl = coverUrl,
            publishYear = publishYear, publisher = publisher,
            isbn = isbn, pageCount = pageCount, rating = rating,
            notes = notes, readingStatus = readingStatus,
            isFavorite = isFavorite,
            createdAt = createdAt, updatedAt = updatedAt
        )
    }

    private fun BookModel.toEntity(): BookEntity {
        return BookEntity(
            id = id, title = title, author = author,
            description = description, coverUrl = coverUrl,
            publishYear = publishYear, publisher = publisher,
            isbn = isbn, pageCount = pageCount, rating = rating,
            notes = notes, readingStatus = readingStatus,
            isFavorite = isFavorite,
            createdAt = createdAt, updatedAt = updatedAt
        )
    }

    private fun OpenLibraryDoc.toBookModel(): BookModel {
        val coverUrl = if (coverId != null) {
            "https://covers.openlibrary.org/b/id/$coverId-M.jpg"
        } else ""

        return BookModel(
            title = title,
            author = authorName?.joinToString(", ") ?: "未知作者",
            description = subjects?.joinToString(", ") ?: "",
            coverUrl = coverUrl,
            publishYear = firstPublishYear?.toString() ?: "",
            publisher = publisher?.firstOrNull() ?: "",
            isbn = isbn?.firstOrNull() ?: "",
            pageCount = numberOfPages ?: 0,
            rating = ratingsAverage?.toFloat() ?: 0f,
            readingStatus = "want_to_read"
        )
    }
}
