package com.group_7.library_management.ui.home

import androidx.lifecycle.ViewModel
import com.group_7.library_management.models.Book
import com.group_7.library_management.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HomeUiState(
    val borrowedBooks: List<Book> = emptyList(),
    val popularBooks: List<Book> = emptyList(),
    val newBooks: List<Book> = emptyList(),
    val recommendedBooks: List<Book> = emptyList(),
    val isLoading: Boolean = false
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        val allBooks = listOf(
            Book("1", "Clean Architecture", "Robert C. Martin", "Lập trình", borrowFee = 180000, availableCopies = 2, rating = 4.8),
            Book("2", "Design Patterns", "Gang of Four", "Lập trình", borrowFee = 150000, availableCopies = 5, rating = 4.7),
            Book("3", "Kotlin in Action", "Dmitry Jemerov", "Lập trình", borrowFee = 120000, availableCopies = 1, rating = 4.9),
            Book("4", "Cấu trúc dữ liệu và giải thuật nâng cao", "Nguyễn Văn A", "Lập trình", borrowFee = 150000, availableCopies = 3, rating = 4.6),
            Book("5", "Hệ quản trị cơ sở dữ liệu quan hệ", "Trần Thị B", "Cơ sở dữ liệu", borrowFee = 120000, availableCopies = 0, rating = 4.3),
            Book("6", "Mạng máy tính căn bản", "Lê Văn C", "Mạng máy tính", borrowFee = 100000, availableCopies = 5, rating = 4.5)
        )

        _uiState.update {
            it.copy(
                isLoading = false,
                borrowedBooks = listOf(allBooks[0], allBooks[1]),
                popularBooks = allBooks.take(4),
                newBooks = allBooks.takeLast(3),
                recommendedBooks = listOf(allBooks[2], allBooks[4])
            )
        }
    }
}
