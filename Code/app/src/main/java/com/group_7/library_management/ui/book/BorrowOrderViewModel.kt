package com.group_7.library_management.ui.book

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group_7.library_management.data.network.NetworkMonitor
import com.group_7.library_management.data.repository.BorrowRepository
import com.group_7.library_management.models.BorrowOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class BorrowOrderUiState(
    val order: BorrowOrder? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class BorrowOrderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val borrowRepository: BorrowRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {
    private val orderId: Long = checkNotNull(savedStateHandle["orderId"])
    private val _uiState = MutableStateFlow(BorrowOrderUiState())
    val uiState: StateFlow<BorrowOrderUiState> = _uiState.asStateFlow()

    init {
        loadOrder()
    }

    fun loadOrder() {
        if (!networkMonitor.isConnected.value) {
            _uiState.value = BorrowOrderUiState(
                isLoading = false,
                errorMessage = "Không có kết nối mạng để tải đơn mượn."
            )
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { borrowRepository.getBorrowOrder(orderId) }
                .onSuccess { order -> _uiState.value = BorrowOrderUiState(order = order, isLoading = false) }
                .onFailure { error ->
                    _uiState.value = BorrowOrderUiState(
                        isLoading = false,
                        errorMessage = error.toUserMessage()
                    )
                }
        }
    }

    private fun Throwable.toUserMessage(): String = when (this) {
        is IOException -> "Không thể kết nối đến máy chủ."
        is HttpException -> if (code() == 404) "Không tìm thấy đơn mượn." else "Máy chủ trả về lỗi HTTP ${code()}."
        else -> message ?: "Không thể tải thông tin đơn mượn."
    }
}
