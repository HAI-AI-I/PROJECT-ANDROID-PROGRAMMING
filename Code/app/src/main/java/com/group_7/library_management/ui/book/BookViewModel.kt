package com.group_7.library_management.ui.book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group_7.library_management.models.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookListUiState(
    val allBooks: List<Book> = emptyList(),
    val filteredBooks: List<Book> = emptyList(),
    val searchQuery: String = "",
    val quickGenre: String = "Tất cả",
    val quickStatus: String? = null,
    val filter: BookFilterState = BookFilterState(),
    val showFilterSheet: Boolean = false,
    val isLoading: Boolean = false
)
data class BookFilterState(
    val selectedGenres: Set<String> = emptySet(),
    val minRating: Int = 0
)

sealed interface BorrowUiState {
    object Idle : BorrowUiState
    object Loading : BorrowUiState
    data class Success(val transactionId: String) : BorrowUiState
    data class Error(val message: String) : BorrowUiState
}

class BookViewModel : ViewModel() {
    private val _uiState=MutableStateFlow(BookListUiState())
    val uiState:StateFlow<BookListUiState> = _uiState.asStateFlow()
    private val _borrowState = MutableStateFlow<BorrowUiState>(BorrowUiState.Idle)
    val borrowState: StateFlow<BorrowUiState> = _borrowState
    init{
        loadBooks()
    }
    private fun loadBooks() {
        val sampleBooks = listOf(
            Book("1", "Clean Architecture", "Robert C. Martin", "Lập trình", borrowFee = 180000, availableCopies = 2, rating = 4.8),
            Book("2", "Design Patterns", "Gang of Four", "Lập trình", borrowFee = 150000, availableCopies = 5, rating = 4.7),
            Book("3", "Kotlin in Action", "Dmitry Jemerov", "Lập trình", borrowFee = 120000, availableCopies = 1, rating = 4.9),
            Book("4", "Cấu trúc dữ liệu và giải thuật nâng cao", "Nguyễn Văn A", "Lập trình", borrowFee = 150000, availableCopies = 3, rating = 4.6),
            Book("5", "Hệ quản trị cơ sở dữ liệu quan hệ", "Trần Thị B", "Cơ sở dữ liệu", borrowFee = 120000, availableCopies = 0, rating = 4.3),
            Book("6", "Mạng máy tính căn bản", "Lê Văn C", "Mạng máy tính", borrowFee = 100000, availableCopies = 5, rating = 4.5)
        )
        _uiState.update { it.copy(allBooks = sampleBooks, filteredBooks = sampleBooks) }
    }
    private fun applyFilters() {
        _uiState.update { state ->
            val result = state.allBooks.filter { book ->
                val matchesQuery = state.searchQuery.isBlank() ||
                        book.title.contains(state.searchQuery, ignoreCase = true) ||
                        book.author.contains(state.searchQuery, ignoreCase = true)

                val matchesQuickGenre = state.quickGenre == "Tất cả" || book.category == state.quickGenre

                val matchesQuickStatus = when (state.quickStatus) {
                    "available" -> book.availableCopies > 0
                    "borrowed" -> book.availableCopies == 0
                    else -> true
                }

                val matchesFilterGenre = state.filter.selectedGenres.isEmpty() || book.category in state.filter.selectedGenres
                val matchesRating = state.filter.minRating == 0 || book.rating >= state.filter.minRating

                matchesQuery && matchesQuickGenre && matchesQuickStatus && matchesFilterGenre && matchesRating
            }
            state.copy(filteredBooks = result)
        }
    }
    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }
    fun onQuickGenreChange(genre: String) {
        _uiState.update { it.copy(quickGenre = genre) }
        applyFilters()
    }
    fun onQuickStatusToggle(status: String) {
        _uiState.update {
            val newStatus = if (it.quickStatus == status) null else status
            it.copy(quickStatus = newStatus)
        }
        applyFilters()
    }
    fun onApplyFilter(filter: BookFilterState) {
        _uiState.update { it.copy(filter = filter, showFilterSheet = false) }
        applyFilters()
    }
    fun setFilterSheetVisible(visible: Boolean) {
        _uiState.update { it.copy(showFilterSheet = visible) }
    }
    fun resetFilters() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                quickGenre = "Tất cả",
                quickStatus = null,
                filter = BookFilterState()
            )
        }
        applyFilters()
    }
    fun confirmBorrowBook(bookId: String) {
        viewModelScope.launch {
            _borrowState.value = BorrowUiState.Loading
            val isSuccess = true
            if (isSuccess) {
                _borrowState.value = BorrowUiState.Success(transactionId = "TX-998823")
            } else {
                _borrowState.value = BorrowUiState.Error("Không thể kết nối máy chủ")
            }
        }
    }


    fun resetBorrowState() {
        _borrowState.value = BorrowUiState.Idle
    }
}