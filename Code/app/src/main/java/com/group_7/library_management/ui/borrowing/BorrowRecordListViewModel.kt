package com.group_7.library_management.ui.borrowing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group_7.library_management.data.network.NetworkMonitor
import com.group_7.library_management.data.repository.BorrowRepository
import com.group_7.library_management.models.BorrowOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class BorrowRecordListUiState(
    val orders: List<BorrowOrder> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class BorrowRecordListViewModel @Inject constructor(
    private val repository: BorrowRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {
    private val _uiState = MutableStateFlow(BorrowRecordListUiState())
    val uiState: StateFlow<BorrowRecordListUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        if (!networkMonitor.isConnected.value) {
            _uiState.value = BorrowRecordListUiState(
                isLoading = false,
                errorMessage = "Không có kết nối mạng để tải danh sách mượn."
            )
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            runCatching { repository.getBorrowOrders() }
                .onSuccess { orders ->
                    _uiState.value = BorrowRecordListUiState(orders = orders, isLoading = false)
                }
                .onFailure { error ->
                    _uiState.value = BorrowRecordListUiState(
                        isLoading = false,
                        errorMessage = error.toUserMessage()
                    )
                }
        }
    }

    private fun Throwable.toUserMessage(): String = when (this) {
        is IOException -> "Không thể kết nối đến máy chủ."
        is HttpException -> "Máy chủ trả về lỗi HTTP ${code()}."
        else -> message ?: "Không thể tải danh sách mượn."
    }
}
