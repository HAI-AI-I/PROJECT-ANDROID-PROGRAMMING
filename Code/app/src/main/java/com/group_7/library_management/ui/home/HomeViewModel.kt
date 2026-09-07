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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val borrowedBooks: List<Book> = emptyList(),
    val popularBooks: List<Book> = emptyList(),
    val newBooks: List<Book> = emptyList(),
    val recommendedBooks: List<Book> = emptyList(),
    val isLoadingBooks: Boolean = true,
    val borrowSummary: UserBorrowSummary= UserBorrowSummary(),
    val isLoadingSummary:Boolean=true
)


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val borrowRepository: BorrowRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadBooksData()
        loadBorrowSummary()
    }

    private fun loadBooksData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingBooks = true) }

            combine(
                bookRepository.getPopularBooks(limit = 5),
                bookRepository.getNewestBooks(limit = 5),
                bookRepository.getRecommendedBooks(limit = 5)
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
    private fun loadBorrowSummary(){
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingSummary = true) }
            borrowRepository.getBorrowSummary().collect { summary ->
                _uiState.update {
                    it.copy(
                        borrowSummary = summary,
                        isLoadingSummary = false
                    )
                }
            }
        }
    }
}
