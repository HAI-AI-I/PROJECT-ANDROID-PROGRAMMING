package com.group_7.library_management.ui.book

import androidx.lifecycle.SavedStateHandle
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
    val isLoading: Boolean = false
)

sealed interface BorrowUiState {
    object Idle : BorrowUiState
    object Loading : BorrowUiState
    data class Success(val transactionId: String) : BorrowUiState
    data class Error(val message: String) : BorrowUiState
}

@HiltViewModel
class BookViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val restoredFilter = BookFilterState(
        sort = enumValueOrDefault(savedStateHandle[KEY_SORT], SortOption.NEWEST),
        selectedGenres = savedStateHandle.get<ArrayList<String>>(KEY_GENRES)?.toSet().orEmpty(),
        minRating = savedStateHandle[KEY_MIN_RATING] ?: 0,
        uploadTime = enumValueOrDefault(savedStateHandle[KEY_UPLOAD_TIME], UploadTimeOption.ANY_TIME),
        priceRange = enumValueOrDefault(savedStateHandle[KEY_PRICE_RANGE], PriceRangeOption.ALL)
    )

    private val _uiState = MutableStateFlow(
        BookListUiState(
            searchQuery = savedStateHandle[KEY_SEARCH_QUERY] ?: "",
            quickGenre = savedStateHandle[KEY_QUICK_GENRE] ?: "Tất cả",
            quickStatus = savedStateHandle[KEY_QUICK_STATUS],
            filter = restoredFilter
        )
    )
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
            val matchingBooks = state.allBooks.filter { book ->
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

            val sortedBooks = when (state.filter.sort) {
                SortOption.NEWEST -> matchingBooks.sortedByDescending { it.createdAt }
                SortOption.POPULAR -> matchingBooks.sortedWith(
                    compareByDescending<Book> { it.popularityScore }
                        .thenByDescending { it.createdAt }
                )
                SortOption.PRICE_ASC -> matchingBooks.sortedBy { it.borrowFee }
                SortOption.PRICE_DESC -> matchingBooks.sortedByDescending { it.borrowFee }
            }

            state.copy(filteredBooks = sortedBooks)
        }
    }
    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        savePageState()
        applyFilters()
    }
    fun onQuickGenreChange(genre: String) {
        _uiState.update { it.copy(quickGenre = genre) }
        savePageState()
        applyFilters()
    }
    fun onQuickStatusToggle(status: String) {
        _uiState.update {
            val newStatus = if (it.quickStatus == status) null else status
            it.copy(quickStatus = newStatus)
        }
        savePageState()
        applyFilters()
    }
    fun onApplyFilter(filter: BookFilterState) {
        _uiState.update { it.copy(filter = filter, showFilterSheet = false) }
        savePageState()
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
        savePageState()
        applyFilters()
    }

    fun applyInitialFilter(filter: String) {
        when (filter) {
            "popular" -> {
                _uiState.update {
                    it.copy(
                        searchQuery = "",
                        quickGenre = "Tất cả",
                        quickStatus = null,
                        filter = BookFilterState(sort = SortOption.POPULAR)
                    )
                }
                savePageState()
                applyFilters()
            }
            "new" -> {
                _uiState.update {
                    it.copy(
                        searchQuery = "",
                        quickGenre = "Tất cả",
                        quickStatus = null,
                        filter = BookFilterState(sort = SortOption.NEWEST)
                    )
                }
                savePageState()
                applyFilters()
            }
            "recommended" -> {
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

    private fun savePageState() {
        val state = _uiState.value
        savedStateHandle[KEY_SEARCH_QUERY] = state.searchQuery
        savedStateHandle[KEY_QUICK_GENRE] = state.quickGenre
        savedStateHandle[KEY_QUICK_STATUS] = state.quickStatus
        savedStateHandle[KEY_SORT] = state.filter.sort.name
        savedStateHandle[KEY_GENRES] = ArrayList(state.filter.selectedGenres)
        savedStateHandle[KEY_MIN_RATING] = state.filter.minRating
        savedStateHandle[KEY_UPLOAD_TIME] = state.filter.uploadTime.name
        savedStateHandle[KEY_PRICE_RANGE] = state.filter.priceRange.name
    }

    companion object {
        private const val KEY_SEARCH_QUERY = "book_search_query"
        private const val KEY_QUICK_GENRE = "book_quick_genre"
        private const val KEY_QUICK_STATUS = "book_quick_status"
        private const val KEY_SORT = "book_sort"
        private const val KEY_GENRES = "book_genres"
        private const val KEY_MIN_RATING = "book_min_rating"
        private const val KEY_UPLOAD_TIME = "book_upload_time"
        private const val KEY_PRICE_RANGE = "book_price_range"

        private inline fun <reified T : Enum<T>> enumValueOrDefault(
            savedValue: String?,
            defaultValue: T
        ): T = enumValues<T>().firstOrNull { it.name == savedValue } ?: defaultValue
    }
}
