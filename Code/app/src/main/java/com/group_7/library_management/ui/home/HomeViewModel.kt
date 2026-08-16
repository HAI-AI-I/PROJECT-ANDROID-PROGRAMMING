package com.group_7.library_management.ui.home

import androidx.lifecycle.ViewModel
import com.group_7.library_management.models.Book
import com.group_7.library_management.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


data class HomeUiState(
    val currentUser: User? = null,
    val borrowedBooks: List<Book> = emptyList(),
    val popularBooks: List<Book> = emptyList(),
    val isLoading: Boolean = false
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        _uiState.value = HomeUiState(
            currentUser = User(
                id = "1",
                name = "Nguyễn Văn An",
                studentId = "24510000",
                qrCodeData = "STUDENT_24510000"
            ),
            borrowedBooks = listOf(
                Book(
                    id = "b1",
                    title = "Lập trình Android",
                    author = "Author A",
                    remainingDays = 3,
                    isOverdue = false
                ),
                Book(
                    id = "b2",
                    title = "Tổng hợp ngôn ngữ lập trình",
                    author = "Author B",
                    remainingDays = 0,
                    isOverdue = true
                )
            ),
            popularBooks = List(5) { index ->
                Book(
                    id = "p$index",
                    title = "Sách phổ biến $index",
                    author = "Tác giả $index",
                    isAvailable = index % 2 == 0
                )
            }
        )
    }
}
