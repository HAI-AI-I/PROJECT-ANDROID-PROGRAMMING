package com.group_7.library_management.ui.book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group_7.library_management.data.repository.BookRepository
import com.group_7.library_management.models.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookListUiState(
    val allBooks: List<Book> = emptyList(),
    val filteredBooks: List<Book> = emptyList(),
    val searchQuery: String = "",
    val quickGenre: String = "Tất cả",
    val quickStatus: String? = null,
    val filter: BookFilterState = BookFilterState(),
    val showFilterSheet: Boolean = false,
    val isLoading: Boolean = false,
    val screenTitle: String = "Sách"
)

sealed interface BorrowUiState {
    object Idle : BorrowUiState
    object Loading : BorrowUiState
    data class Success(val transactionId: String) : BorrowUiState
    data class Error(val message: String) : BorrowUiState
}

@HiltViewModel
class BookViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {
    private val _uiState=MutableStateFlow(BookListUiState())
    val uiState:StateFlow<BookListUiState> = _uiState.asStateFlow()
    private val _borrowState = MutableStateFlow<BorrowUiState>(BorrowUiState.Idle)
    val borrowState: StateFlow<BorrowUiState> = _borrowState

    init{
        loadBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            bookRepository.getPopularBooks(limit = 50).collect { books ->
                _uiState.update { 
                    it.copy(
                        allBooks = books, 
                        filteredBooks = books,
                        isLoading = false
                    ) 
                }
                applyFilters()
            }
        }
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
                filter = BookFilterState(),
                screenTitle = "Sách"
            )
        }
        applyFilters()
    }

    fun applyInitialFilter(filter: String) {
        when (filter) {
            "popular" -> {
                _uiState.update { 
                    it.copy(
                        filter = it.filter.copy(minRating = 4),
                        screenTitle = "Sách phổ biến"
                    ) 
                }
                applyFilters()
                _uiState.update { state ->
                    state.copy(filteredBooks = state.filteredBooks.sortedByDescending { it.rating })
                }
            }
            "new" -> {
                _uiState.update {
                    it.copy(
                        searchQuery = "",
                        quickGenre = "Tất cả",
                        quickStatus = null,
                        filter = BookFilterState(),
                        screenTitle = "Sách mới"
                    )
                }
                applyFilters()
                _uiState.update { state ->
                    state.copy(filteredBooks = state.filteredBooks.sortedByDescending { it.id.toIntOrNull() ?: 0 })
                }
            }
            "recommended" -> {
                _uiState.update {
                    it.copy(
                        screenTitle = "Sách dành cho bạn"
                    )
                }
                // Giả lập logic: lấy các sách có rating cao hoặc thể loại lập trình
                applyFilters()
                _uiState.update { state ->
                    state.copy(filteredBooks = state.allBooks.filter { it.rating >= 4.5 }.shuffled())
                }
            }
        }
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