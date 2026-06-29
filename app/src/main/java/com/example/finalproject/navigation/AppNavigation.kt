package com.example.finalproject.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.finalproject.ui.screens.BookDetailScreen
import com.example.finalproject.ui.screens.BookListScreen
import com.example.finalproject.viewmodel.BookDetailViewModel
import com.example.finalproject.viewmodel.BookListViewModel

object Routes {
    const val BOOK_LIST = "book_list"
    const val BOOK_DETAIL = "book_detail/{bookId}"

    fun bookDetail(bookId: Long) = "book_detail/$bookId"
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    bookListViewModel: BookListViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.BOOK_LIST
    ) {
        composable(Routes.BOOK_LIST) {
            BookListScreen(
                viewModel = bookListViewModel,
                onBookClick = { bookId ->
                    navController.navigate(Routes.bookDetail(bookId))
                }
            )
        }

        composable(
            route = Routes.BOOK_DETAIL,
            arguments = listOf(
                navArgument("bookId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getLong("bookId") ?: 0L
            val detailViewModel: BookDetailViewModel = viewModel()
            BookDetailScreen(
                bookId = bookId,
                viewModel = detailViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
