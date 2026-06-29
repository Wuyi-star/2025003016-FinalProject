package com.example.finalproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.database.AppDatabase
import com.example.finalproject.data.repository.BookRepository
import com.example.finalproject.model.BookModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookDetailUiState(
    val book: BookModel? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false
)

class BookDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = BookRepository(database.bookDao(), database.searchHistoryDao())

    private val _uiState = MutableStateFlow(BookDetailUiState())
    val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

    fun loadBook(bookId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val book = repository.getBookById(bookId)
            _uiState.update {
                it.copy(
                    book = book,
                    isLoading = false,
                    isSaved = book != null
                )
            }
        }
    }

    fun updateBookNotes(notes: String) {
        val currentBook = _uiState.value.book ?: return
        val updatedBook = currentBook.copy(notes = notes)
        viewModelScope.launch {
            repository.updateBook(updatedBook)
            _uiState.update { it.copy(book = updatedBook) }
        }
    }

    fun updateReadingStatus(status: String) {
        val currentBook = _uiState.value.book ?: return
        val updatedBook = currentBook.copy(readingStatus = status)
        viewModelScope.launch {
            repository.updateBook(updatedBook)
            _uiState.update { it.copy(book = updatedBook) }
        }
    }

    fun updateRating(rating: Float) {
        val currentBook = _uiState.value.book ?: return
        val updatedBook = currentBook.copy(rating = rating)
        viewModelScope.launch {
            repository.updateBook(updatedBook)
            _uiState.update { it.copy(book = updatedBook) }
        }
    }

    fun deleteBook() {
        val currentBook = _uiState.value.book ?: return
        viewModelScope.launch {
            repository.deleteBook(currentBook)
            _uiState.update { it.copy(book = null, isSaved = false) }
        }
    }

    fun saveBook(book: BookModel) {
        viewModelScope.launch {
            repository.insertBook(book)
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}
