package com.group_7.library_management.ui.book

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group_7.library_management.data.repository.BookRepository
import com.group_7.library_management.models.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
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
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val currentPage: Int = 0,
    val total: Long = 0,
    val errorMessage: String? = null
)

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
    val uiState: StateFlow<BookListUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null
    private var searchJob: Job? = null

    init {
        reloadBooks()
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (state.isLoading || state.isLoadingMore || !state.hasMore) return
        loadPage(reset = false)
    }

    fun refreshBooks() {
        if (loadJob?.isActive == true) return
        reloadBooks()
    }

    private fun reloadBooks() {
        loadPage(reset = true)
    }

    private fun loadPage(reset: Boolean) {
        if (reset) loadJob?.cancel()
        val state = _uiState.value
        if (!reset && (state.isLoading || state.isLoadingMore || !state.hasMore)) return
        val requestedPage = if (reset) 1 else state.currentPage + 1

        loadJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    allBooks = if (reset) emptyList() else it.allBooks,
                    filteredBooks = if (reset) emptyList() else it.filteredBooks,
                    isLoading = reset,
                    isLoadingMore = !reset,
                    errorMessage = null,
                    hasMore = if (reset) true else it.hasMore
                )
            }
            try {
                val current = _uiState.value
                val (minPrice, maxPrice) = current.filter.priceRange.toApiPriceRange()
                val page = bookRepository.getBooksPage(
                    search = current.searchQuery.trim().ifBlank { null },
                    category = current.quickGenre.takeUnless { it == "Tất cả" },
                    categories = current.filter.selectedGenres.toList().ifEmpty { null },
                    status = current.quickStatus,
                    minRating = current.filter.minRating.takeIf { it > 0 }?.toDouble(),
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    createdAfter = current.filter.uploadTime.toCreatedAfter(),
                    page = requestedPage,
                    pageSize = PAGE_SIZE,
                    sort = current.filter.sort.toApiValue()
                )
                _uiState.update { latest ->
                    val books = if (reset) page.items
                    else (latest.filteredBooks + page.items).distinctBy { it.id }
                    latest.copy(
                        allBooks = books,
                        filteredBooks = books,
                        isLoading = false,
                        isLoadingMore = false,
                        hasMore = page.page < page.totalPages,
                        currentPage = page.page,
                        total = page.total
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                if (reset) {
                    val cachedBooks = runCatching { bookRepository.getCachedBooks() }
                        .getOrDefault(emptyList())
                    val filteredCache = filterCachedBooks(cachedBooks, _uiState.value)
                    _uiState.update {
                        it.copy(
                            allBooks = filteredCache,
                            filteredBooks = filteredCache,
                            isLoading = false,
                            isLoadingMore = false,
                            hasMore = false,
                            currentPage = 0,
                            total = filteredCache.size.toLong(),
                            errorMessage = "Không thể cập nhật sách từ máy chủ. Đang hiển thị dữ liệu trên thiết bị."
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoadingMore = false, errorMessage = "Không thể tải thêm sách.")
                    }
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        savePageState()
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            reloadBooks()
        }
    }

    fun onQuickGenreChange(genre: String) {
        _uiState.update { it.copy(quickGenre = genre) }
        savePageState()
        reloadBooks()
    }

    fun onQuickStatusToggle(status: String) {
        _uiState.update {
            it.copy(quickStatus = if (it.quickStatus == status) null else status)
        }
        savePageState()
        reloadBooks()
    }

    fun onApplyFilter(filter: BookFilterState) {
        _uiState.update { it.copy(filter = filter, showFilterSheet = false) }
        savePageState()
        reloadBooks()
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
        reloadBooks()
    }

    fun applyInitialFilter(filter: String) {
        val sort = when (filter) {
            "popular" -> SortOption.POPULAR
            "new" -> SortOption.NEWEST
            else -> return
        }
        _uiState.update {
            it.copy(
                searchQuery = "",
                quickGenre = "Tất cả",
                quickStatus = null,
                filter = BookFilterState(sort = sort)
            )
        }
        savePageState()
        reloadBooks()
    }

    private fun filterCachedBooks(books: List<Book>, state: BookListUiState): List<Book> {
        val createdAfter = state.filter.uploadTime.toCreatedAfter()?.let(Instant::parse)?.toEpochMilli()
        val (minPrice, maxPrice) = state.filter.priceRange.toApiPriceRange()
        val matching = books.filter { book ->
            val matchesText = state.searchQuery.isBlank()
                    || book.title.contains(state.searchQuery, ignoreCase = true)
                    || book.author.contains(state.searchQuery, ignoreCase = true)
            val matchesQuickGenre = state.quickGenre == "Tất cả"
                    || book.category.equals(state.quickGenre, ignoreCase = true)
            val matchesGenres = state.filter.selectedGenres.isEmpty()
                    || state.filter.selectedGenres.any { book.category.equals(it, ignoreCase = true) }
            val matchesStatus = when (state.quickStatus) {
                "available" -> book.availableCopies > 0
                "borrowed" -> book.availableCopies == 0
                else -> true
            }
            val matchesRating = state.filter.minRating == 0 || book.rating >= state.filter.minRating
            val matchesDate = createdAfter == null || book.createdAt >= createdAfter
            val matchesMinPrice = minPrice == null || book.borrowFee >= minPrice
            val matchesMaxPrice = maxPrice == null || book.borrowFee <= maxPrice
            matchesText && matchesQuickGenre && matchesGenres && matchesStatus &&
                    matchesRating && matchesDate && matchesMinPrice && matchesMaxPrice
        }
        return when (state.filter.sort) {
            SortOption.NEWEST -> matching.sortedByDescending { it.createdAt }
            SortOption.POPULAR -> matching.sortedWith(
                compareByDescending<Book> { it.popularityScore }.thenByDescending { it.createdAt }
            )
            SortOption.PRICE_ASC -> matching.sortedBy { it.borrowFee }
            SortOption.PRICE_DESC -> matching.sortedByDescending { it.borrowFee }
        }
    }

    private fun SortOption.toApiValue(): String = when (this) {
        SortOption.NEWEST -> "newest"
        SortOption.POPULAR -> "popular"
        SortOption.PRICE_ASC -> "price_asc"
        SortOption.PRICE_DESC -> "price_desc"
    }

    private fun UploadTimeOption.toCreatedAfter(): String? = when (this) {
        UploadTimeOption.ANY_TIME -> null
        UploadTimeOption.THIS_WEEK -> Instant.now().minus(7, ChronoUnit.DAYS).toString()
        UploadTimeOption.THIS_MONTH -> Instant.now().minus(30, ChronoUnit.DAYS).toString()
        UploadTimeOption.THIS_YEAR -> Instant.now().minus(365, ChronoUnit.DAYS).toString()
    }

    private fun PriceRangeOption.toApiPriceRange(): Pair<Long?, Long?> = when (this) {
        PriceRangeOption.ALL -> null to null
        PriceRangeOption.FREE -> 0L to 0L
        PriceRangeOption.UNDER_100K -> null to 99_999L
        PriceRangeOption.FROM_100K_TO_200K -> 100_000L to 200_000L
        PriceRangeOption.OVER_200K -> 200_001L to null
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
        private const val PAGE_SIZE = 20
        private const val SEARCH_DEBOUNCE_MS = 400L
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
