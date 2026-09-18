package com.group_7.library_management.ui.book

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group_7.library_management.data.local.preferences.CheckLogin
import com.group_7.library_management.data.network.NetworkMonitor
import com.group_7.library_management.data.repository.BookRepository
import com.group_7.library_management.data.repository.BorrowRepository
import com.group_7.library_management.data.repository.UserRepository
import com.group_7.library_management.components.AppSnackbarController
import com.group_7.library_management.models.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject

data class BorrowConfirmUiState(
    val book: Book? = null,
    val borrowerName: String = "",
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class BorrowConfirmViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val borrowRepository: BorrowRepository,
    private val checkLogin: CheckLogin,
    private val networkMonitor: NetworkMonitor,
    private val snackbarController: AppSnackbarController
) : ViewModel() {
    private val bookId: String = savedStateHandle["bookId"] ?: ""
    private val _uiState = MutableStateFlow(BorrowConfirmUiState())
    val uiState: StateFlow<BorrowConfirmUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        if (bookId.isBlank()) {
            _uiState.value = BorrowConfirmUiState(
                isLoading = false,
                errorMessage = "Không tìm thấy mã sách."
            )
            return
        }
        if (!networkMonitor.isConnected.value) {
            _uiState.value = BorrowConfirmUiState(
                isLoading = false,
                errorMessage = "Không có kết nối mạng. Không thể tạo yêu cầu mượn sách."
            )
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                val book = bookRepository.getBookDetail(bookId)
                val userId = checkLogin.getSavedUserId()?.toLongOrNull()
                val user = if (userId == null) null else userRepository.getUserById(userId)
                book to (user?.name ?: "Người dùng")
            }.onSuccess { (book, borrowerName) ->
                _uiState.value = BorrowConfirmUiState(
                    book = book,
                    borrowerName = borrowerName,
                    isLoading = false
                )
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = throwable.toMessage())
                }
            }
        }
    }

    fun confirmBorrow(
        borrowDays: Int,
        onSuccess: (Long) -> Unit,
        onBookUnavailable: () -> Unit
    ) {
        val book = _uiState.value.book ?: return
        if (_uiState.value.isSubmitting) return
        if (!networkMonitor.isConnected.value) {
            snackbarController.show("Không có kết nối mạng.")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            runCatching {
                borrowRepository.createBorrowOrder(book.id.toLong(), borrowDays)
            }.onSuccess { order ->
                _uiState.update { it.copy(isSubmitting = false) }
                onSuccess(order.id)
            }.onFailure { throwable ->
                _uiState.update { it.copy(isSubmitting = false) }
                val serverMessage = (throwable as? HttpException)?.readServerMessage().orEmpty()
                if (throwable is HttpException &&
                    throwable.code() == 409 &&
                    serverMessage.contains("đã hết", ignoreCase = true)
                ) {
                    onBookUnavailable()
                } else {
                    snackbarController.show(throwable.toMessage(serverMessage))
                }
            }
        }
    }

    private fun HttpException.readServerMessage(): String = runCatching {
        JSONObject(response()?.errorBody()?.string().orEmpty()).optString("message")
    }.getOrDefault("")

    private fun Throwable.toMessage(serverMessage: String = ""): String = when (this) {
        is IOException -> "Không thể kết nối đến máy chủ."
        is HttpException -> serverMessage.ifBlank {
            if (code() == 404) "Không tìm thấy sách." else "Máy chủ trả về lỗi HTTP ${code()}."
        }
        else -> message ?: "Không thể tải thông tin mượn sách."
    }
}
