package com.example.finalproject.viewmodel

import com.example.finalproject.model.BookModel

data class BookListUiState(
    val localBooks: List<BookModel> = emptyList(),
    val favoriteBooks: List<BookModel> = emptyList(),
    val searchOnlineBooks: List<BookModel> = emptyList(),
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedStatus: String = "all",
    val isOnlineSearch: Boolean = false,
    val showFavorites: Boolean = false
)
