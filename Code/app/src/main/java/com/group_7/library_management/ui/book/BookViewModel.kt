package com.group_7.library_management.ui.book

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed interface BorrowUiState {
    object Idle : BorrowUiState
    object Loading : BorrowUiState
    data class Success(val transactionId: String) : BorrowUiState
    data class Error(val message: String) : BorrowUiState
}

class BookViewModel : ViewModel() {
    private val _borrowState = MutableStateFlow<BorrowUiState>(BorrowUiState.Idle)
    val borrowState: StateFlow<BorrowUiState> = _borrowState

    fun confirmBorrowBook(bookId: String) {
        _borrowState.value = BorrowUiState.Loading

        val isSuccess = true
        if (isSuccess) {
            _borrowState.value = BorrowUiState.Success(transactionId = "TX-998823")
        } else {
            _borrowState.value = BorrowUiState.Error("Không thể kết nối máy chủ")
        }
    }

    fun resetState() {
        _borrowState.value = BorrowUiState.Idle
    }
}