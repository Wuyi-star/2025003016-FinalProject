package com.example.finalproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finalproject.ui.components.BookCard
import com.example.finalproject.viewmodel.BookListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(
    viewModel: BookListViewModel,
    onBookClick: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSearchBar by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // 显示错误 Snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            if (showSearchBar) {
                CustomSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = { 
                        viewModel.updateSearchQuery(it)
                        // 实时搜索本地
                        if (it.isNotEmpty()) {
                            viewModel.searchLocal()
                        } else {
                            viewModel.loadAllBooks()
                        }
                    },
                    onSearch = { viewModel.searchOnline() },
                    active = false,
                    onActiveChange = { },
                    placeholder = { Text("搜索图书...") },
                    leadingIcon = {
                        IconButton(onClick = {
                            showSearchBar = false
                            viewModel.loadAllBooks()
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                        }
                    },
                    trailingIcon = {
                        Row {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { 
                                    viewModel.updateSearchQuery("")
                                    viewModel.loadAllBooks()
                                }) {
                                    Icon(Icons.Default.Clear, contentDescription = "清除")
                                }
                            }
                            IconButton(onClick = { 
                                // 先搜本地，再搜网络
                                viewModel.searchLocal()
                                viewModel.searchOnline() 
                            }) {
                                Icon(Icons.Default.Search, contentDescription = "搜索")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {}
            } else {
                TopAppBar(
                    title = { Text("图书收藏", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    actions = {
                        IconButton(onClick = {
                            if (uiState.showFavorites) {
                                viewModel.hideFavorites()
                            } else {
                                viewModel.showFavorites()
                            }
                        }) {
                            Icon(
                                if (uiState.showFavorites) Icons.Default.Favorite
                                else Icons.Default.FavoriteBorder,
                                contentDescription = "收藏",
                                tint = if (uiState.showFavorites) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                        }
                        IconButton(onClick = { showSearchBar = true }) {
                            Icon(Icons.Default.Search, contentDescription = "搜索")
                        }
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            // 切换搜索类型
            if (uiState.isOnlineSearch) {
                FloatingActionButton(
                    onClick = { viewModel.loadAllBooks() },
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(Icons.Default.Home, contentDescription = "返回本地收藏")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 状态筛选标签
            if (!uiState.isOnlineSearch && !showSearchBar && !uiState.showFavorites) {
                FilterChips(
                    selectedStatus = uiState.selectedStatus,
                    onStatusSelected = { viewModel.updateSelectedStatus(it) }
                )
            }

            // 内容区域
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("正在搜索...", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                uiState.isOnlineSearch -> {
                    // 网络搜索结果
                    if (uiState.searchOnlineBooks.isEmpty() && !uiState.isLoading) {
                        EmptyState(message = "未搜索到相关图书")
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.searchOnlineBooks) { book ->
                                BookCard(
                                    book = book,
                                    onClick = {
                                        viewModel.addBookFromSearch(book)
                                    }
                                )
                            }
                        }
                    }
                }

                uiState.showFavorites -> {
                    // 收藏列表
                    if (uiState.favoriteBooks.isEmpty()) {
                        EmptyState(message = "还没有收藏图书\n点击书籍卡片中的收藏图标添加")
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.favoriteBooks, key = { it.id }) { book ->
                                BookCard(
                                    book = book,
                                    onClick = { onBookClick(book.id) },
                                    onFavoriteClick = {
                                        viewModel.toggleFavorite(book.id, !book.isFavorite)
                                    }
                                )
                            }
                        }
                    }
                }

                else -> {
                    // 本地收藏列表
                    val filteredBooks = when (uiState.selectedStatus) {
                        "all" -> uiState.localBooks
                        else -> uiState.localBooks.filter { it.readingStatus == uiState.selectedStatus }
                    }

                    if (filteredBooks.isEmpty()) {
                        EmptyState(
                            message = if (uiState.localBooks.isEmpty()) {
                                "还没有收藏图书\n点击搜索按钮添加图书"
                            } else {
                                "该分类下暂无图书"
                            }
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredBooks, key = { it.id }) { book ->
                                BookCard(
                                    book = book,
                                    onClick = { onBookClick(book.id) },
                                    onFavoriteClick = {
                                        viewModel.toggleFavorite(book.id, !book.isFavorite)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChips(
    selectedStatus: String,
    onStatusSelected: (String) -> Unit
) {
    val filters = listOf(
        "all" to "全部",
        "want_to_read" to "想读",
        "reading" to "在读",
        "finished" to "已读"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { (key, label) ->
            FilterChip(
                selected = selectedStatus == key,
                onClick = { onStatusSelected(key) },
                label = { Text(label) }
            )
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "📚",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    placeholder: @Composable () -> Unit,
    leadingIcon: @Composable () -> Unit,
    trailingIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = { trailingIcon() },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        )
    )
}
