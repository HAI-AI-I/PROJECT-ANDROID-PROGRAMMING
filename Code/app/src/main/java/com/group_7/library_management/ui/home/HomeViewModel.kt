package com.group_7.library_management.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group_7.library_management.data.repository.BookRepository
import com.group_7.library_management.data.repository.BorrowRepository
import com.group_7.library_management.models.Book
import com.group_7.library_management.models.UserBorrowSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val borrowedBooks: List<Book> = emptyList(),
    val popularBooks: List<Book> = emptyList(),
    val newBooks: List<Book> = emptyList(),
    val recommendedBooks: List<Book> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<Book> = emptyList(),
    val isSearching: Boolean = false,
    val isLoadingMoreSearch: Boolean = false,
    val searchHasMore: Boolean = false,
    val searchPage: Int = 0,
    val searchTotal: Long = 0,
    val searchError: String? = null,
    val isLoadingBooks: Boolean = true,
    val bookLoadError: String? = null,
    val borrowSummary: UserBorrowSummary= UserBorrowSummary(),
    val isLoadingSummary: Boolean = true,
    val summaryLoadError: String? = null
)


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val borrowRepository: BorrowRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private var bookRefreshJob: Job? = null
    private var summaryLoadJob: Job? = null
    private var searchDebounceJob: Job? = null
    private var searchLoadJob: Job? = null

    init {
        observeBooksData()
        refreshBooks()
        refreshBorrowSummary()
    }

    fun onSearchQueryChange(query: String) {
        searchDebounceJob?.cancel()
        searchLoadJob?.cancel()

        if (query.isBlank()) {
            _uiState.update {
                it.copy(
                    searchQuery = query,
                    searchResults = emptyList(),
                    isSearching = false,
                    isLoadingMoreSearch = false,
                    searchHasMore = false,
                    searchPage = 0,
                    searchTotal = 0,
                    searchError = null
                )
            }
            return
        }

        _uiState.update {
            it.copy(
                searchQuery = query,
                searchResults = emptyList(),
                isSearching = true,
                isLoadingMoreSearch = false,
                searchHasMore = false,
                searchPage = 0,
                searchTotal = 0,
                searchError = null
            )
        }
        searchDebounceJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            loadSearchPage(reset = true)
        }
    }

    fun loadNextSearchPage() {
        val state = _uiState.value
        if (state.searchQuery.isBlank() || state.isSearching ||
            state.isLoadingMoreSearch || !state.searchHasMore
        ) return
        loadSearchPage(reset = false)
    }

    fun resetSearch() {
        onSearchQueryChange("")
    }

    private fun loadSearchPage(reset: Boolean) {
        if (reset) searchLoadJob?.cancel()
        val state = _uiState.value
        if (state.searchQuery.isBlank()) return
        if (!reset && (state.isSearching || state.isLoadingMoreSearch || !state.searchHasMore)) return

        val query = state.searchQuery.trim()
        val requestedPage = if (reset) 1 else state.searchPage + 1
        searchLoadJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    searchResults = if (reset) emptyList() else it.searchResults,
                    isSearching = reset,
                    isLoadingMoreSearch = !reset,
                    searchHasMore = if (reset) true else it.searchHasMore,
                    searchError = null
                )
            }
            try {
                val page = bookRepository.getBooksPage(
                    search = query,
                    category = null,
                    categories = null,
                    status = null,
                    minRating = null,
                    minPrice = null,
                    maxPrice = null,
                    createdAfter = null,
                    page = requestedPage,
                    pageSize = SEARCH_PAGE_SIZE,
                    sort = "newest"
                )
                if (_uiState.value.searchQuery.trim() != query) return@launch
                _uiState.update { latest ->
                    val books = if (reset) page.items
                    else (latest.searchResults + page.items).distinctBy { it.id }
                    latest.copy(
                        searchResults = books,
                        isSearching = false,
                        isLoadingMoreSearch = false,
                        searchHasMore = page.page < page.totalPages,
                        searchPage = page.page,
                        searchTotal = page.total,
                        searchError = null
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                if (_uiState.value.searchQuery.trim() != query) return@launch
                if (reset) {
                    val cachedBooks = runCatching { bookRepository.getCachedBooks() }
                        .getOrDefault(emptyList())
                        .filter { book ->
                            book.title.contains(query, ignoreCase = true) ||
                                    book.author.contains(query, ignoreCase = true)
                        }
                        .sortedByDescending { it.createdAt }
                    _uiState.update {
                        it.copy(
                            searchResults = cachedBooks,
                            isSearching = false,
                            isLoadingMoreSearch = false,
                            searchHasMore = false,
                            searchPage = 0,
                            searchTotal = cachedBooks.size.toLong(),
                            searchError = "Không thể tìm kiếm từ máy chủ. Đang hiển thị dữ liệu trên thiết bị."
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoadingMoreSearch = false,
                            searchError = "Không thể tải thêm kết quả tìm kiếm."
                        )
                    }
                }
            }
        }
    }

    private fun observeBooksData() {
        viewModelScope.launch {
            combine(
                bookRepository.getPopularBooks(limit = 10),
                bookRepository.getNewestBooks(limit = 10),
                bookRepository.getRecommendedBooks(limit = 50)
            ) { popular, newest ,recommended->
                Triple(popular, newest,recommended)
            }.collect { (popular, newest,recommended) ->
                _uiState.update {
                    it.copy(
                        popularBooks = popular,
                        newBooks = newest,
                        recommendedBooks = recommended
                    )
                }
            }
        }
    }

    fun refreshBooks() {
        if (bookRefreshJob?.isActive == true) return
        bookRefreshJob = viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isLoadingBooks = state.popularBooks.isEmpty() && state.newBooks.isEmpty(),
                    bookLoadError = null
                )
            }

            runCatching {
                bookRepository.refreshHomeBooks(
                    newestLimit = 10,
                    popularLimit = 10
                )
            }.onFailure {
                _uiState.update { state ->
                    state.copy(bookLoadError = "Không thể cập nhật dữ liệu sách từ máy chủ")
                }
            }

            _uiState.update { it.copy(isLoadingBooks = false) }
        }
    }

    fun refreshBorrowSummary() {
        if (summaryLoadJob?.isActive == true) return
        summaryLoadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoadingSummary = true, summaryLoadError = null) }
            borrowRepository.getBorrowSummary()
                .catch {
                    _uiState.update { state ->
                        state.copy(
                            isLoadingSummary = false,
                            summaryLoadError = "Không thể tải trạng thái mượn sách"
                        )
                    }
                }
                .collect { summary ->
                    _uiState.update {
                        it.copy(
                            borrowSummary = summary,
                            isLoadingSummary = false,
                            summaryLoadError = null
                        )
                    }
                }
        }
    }

    private companion object {
        const val SEARCH_PAGE_SIZE = 20
        const val SEARCH_DEBOUNCE_MS = 400L
    }
}
