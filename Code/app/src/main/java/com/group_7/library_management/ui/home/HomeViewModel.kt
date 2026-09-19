package com.group_7.library_management.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group_7.library_management.data.repository.BookRepository
import com.group_7.library_management.data.repository.BorrowRepository
import com.group_7.library_management.models.Book
import com.group_7.library_management.models.UserBorrowSummary
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private var summaryLoadJob: Job? = null

    init {
        loadBooksData()
        refreshBorrowSummary()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    private fun loadBooksData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingBooks = true, bookLoadError = null) }

            runCatching {
                bookRepository.refreshHomeBooks(
                    newestLimit = 10,
                    popularLimit = 10
                )
            }.onFailure {
                _uiState.update { state ->
                    state.copy(
                        isLoadingBooks = false,
                        bookLoadError = "Không thể tải sách mới từ máy chủ"
                    )
                }
            }

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
                        recommendedBooks = recommended,
                        isLoadingBooks = false
                    )
                }
            }
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
}
