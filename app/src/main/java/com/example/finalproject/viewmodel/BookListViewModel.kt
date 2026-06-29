package com.example.finalproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.MockBooks
import com.example.finalproject.data.database.AppDatabase
import com.example.finalproject.data.repository.BookRepository
import com.example.finalproject.datastore.UserPreferencesRepository
import com.example.finalproject.model.BookModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BookListViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = BookRepository(database.bookDao(), database.searchHistoryDao())
    private val preferencesRepo = UserPreferencesRepository(application)

    private val _uiState = MutableStateFlow(BookListUiState())
    val uiState: StateFlow<BookListUiState> = _uiState.asStateFlow()

    init {
        insertMockBooksIfNeeded()
        loadLocalBooks()
        loadFavoriteBooks()
        loadSearchHistory()
    }

    private fun insertMockBooksIfNeeded() {
        viewModelScope.launch {
            val count = repository.getBookCount()
            if (count == 0) {
                MockBooks.sampleBooks.forEach { book ->
                    repository.insertBook(book)
                }
            }
        }
    }

    private fun loadLocalBooks() {
        viewModelScope.launch {
            repository.getAllBooks().collect { books ->
                _uiState.update { state ->
                    state.copy(localBooks = books)
                }
            }
        }
    }

    private fun loadFavoriteBooks() {
        viewModelScope.launch {
            repository.getFavoriteBooks().collect { books ->
                _uiState.update { state ->
                    state.copy(favoriteBooks = books)
                }
            }
        }
    }

    private fun loadSearchHistory() {
        // 读取 DataStore 中保存的最近搜索
        viewModelScope.launch {
            preferencesRepo.lastSearch.collect { lastSearch ->
                if (lastSearch.isNotEmpty()) {
                    _uiState.update { it.copy(searchQuery = lastSearch) }
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun updateSelectedStatus(status: String) {
        _uiState.update { it.copy(selectedStatus = status, isOnlineSearch = false) }
    }

    // ========== 网络搜索 ==========

    fun searchOnline() {
        val query = _uiState.value.searchQuery.trim()
        if (query.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "请输入搜索关键词") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, isOnlineSearch = true) }

            // 保存搜索词到 DataStore 和 Room
            preferencesRepo.setLastSearch(query)
            repository.saveSearchHistory(query)

            // 同时搜索本地数据库
            searchLocalInBackground(query)

            val result = repository.searchOnlineBooks(query)
            result.onSuccess { books ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        searchOnlineBooks = books,
                        errorMessage = if (books.isEmpty()) "未找到相关图书" else null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "搜索失败: ${error.message ?: "未知错误"}"
                    )
                }
            }
        }
    }

    private fun searchLocalInBackground(query: String) {
        viewModelScope.launch {
            repository.searchLocalBooks(query).collect { books ->
                _uiState.update {
                    it.copy(
                        localBooks = books
                    )
                }
            }
        }
    }

    // ========== 本地搜索 ==========

    fun searchLocal() {
        val query = _uiState.value.searchQuery.trim()
        _uiState.update { it.copy(isOnlineSearch = false) }

        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true) }
            repository.searchLocalBooks(query).collect { books ->
                _uiState.update {
                    it.copy(
                        localBooks = books,
                        isSearching = false,
                        errorMessage = if (books.isEmpty() && query.isNotEmpty()) "本地未找到匹配的图书" else null
                    )
                }
            }
        }
    }

    fun loadAllBooks() {
        _uiState.update { it.copy(searchQuery = "", selectedStatus = "all", errorMessage = null, isOnlineSearch = false, showFavorites = false) }
        loadLocalBooks()
    }

    // ========== CRUD ==========

    fun deleteBook(book: BookModel) {
        viewModelScope.launch {
            repository.deleteBook(book)
        }
    }

    fun addBookFromSearch(book: BookModel) {
        viewModelScope.launch {
            val existingBooks = repository.searchLocalBooks(book.title)
            // 简单去重检查
            var isDuplicate = false
            existingBooks.collect { books ->
                if (books.any { it.title == book.title && it.author == book.author }) {
                    isDuplicate = true
                }
            }
            if (!isDuplicate) {
                repository.insertBook(book)
                _uiState.update { it.copy(errorMessage = null) }
            } else {
                _uiState.update { it.copy(errorMessage = "该书已在收藏中") }
            }
        }
    }

    // ========== 收藏功能 ==========

    fun toggleFavorite(bookId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(bookId, isFavorite)
        }
    }

    fun showFavorites() {
        _uiState.update { it.copy(showFavorites = true, isOnlineSearch = false) }
    }

    fun hideFavorites() {
        _uiState.update { it.copy(showFavorites = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
