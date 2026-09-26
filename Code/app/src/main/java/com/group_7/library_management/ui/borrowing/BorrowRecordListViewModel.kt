package com.group_7.library_management.ui.borrowing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group_7.library_management.data.network.NetworkMonitor
import com.group_7.library_management.data.repository.BorrowRepository
import com.group_7.library_management.components.AppSnackbarController
import com.group_7.library_management.models.BorrowOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import org.json.JSONObject
import javax.inject.Inject

data class BorrowRecordListUiState(
    val orders: List<BorrowOrder> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val cancellingOrderId: Long? = null
)

@HiltViewModel
class BorrowRecordListViewModel @Inject constructor(
    private val repository: BorrowRepository,
    private val networkMonitor: NetworkMonitor,
    private val snackbarController: AppSnackbarController
) : ViewModel() {
    private val _uiState = MutableStateFlow(BorrowRecordListUiState())
    val uiState: StateFlow<BorrowRecordListUiState> = _uiState.asStateFlow()
    private var refreshJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        if (refreshJob?.isActive == true) return
        if (!networkMonitor.isConnected.value) {
            _uiState.value = BorrowRecordListUiState(
                isLoading = false,
                errorMessage = "Không có kết nối mạng để tải danh sách mượn."
            )
            return
        }
        refreshJob = viewModelScope.launch {
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

    fun cancelOrder(orderId: Long) {
        if (_uiState.value.cancellingOrderId != null) return
        if (!networkMonitor.isConnected.value) {
            snackbarController.show("Không có kết nối mạng. Không thể hủy đơn.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(cancellingOrderId = orderId)
            runCatching { repository.cancelBorrowOrder(orderId) }
                .onSuccess { result ->
                    _uiState.value = _uiState.value.copy(
                        orders = _uiState.value.orders.map { order ->
                            if (order.id == result.order.id) result.order else order
                        },
                        cancellingOrderId = null
                    )
                    snackbarController.show(
                        "Đã hủy đơn. Bạn còn ${result.remainingCancellations}/5 lượt hủy trong tháng này."
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(cancellingOrderId = null)
                    snackbarController.show(error.toCancelMessage())
                }
        }
    }

    private fun Throwable.toCancelMessage(): String = when (this) {
        is IOException -> "Không thể kết nối đến máy chủ."
        is HttpException -> readServerMessage().ifBlank {
            "Không thể hủy đơn. Máy chủ trả về lỗi HTTP ${code()}."
        }
        else -> message ?: "Không thể hủy đơn."
    }

    private fun HttpException.readServerMessage(): String = runCatching {
        JSONObject(response()?.errorBody()?.string().orEmpty()).optString("message")
    }.getOrDefault("")

    private fun Throwable.toUserMessage(): String = when (this) {
        is IOException -> "Không thể kết nối đến máy chủ."
        is HttpException -> "Máy chủ trả về lỗi HTTP ${code()}."
        else -> message ?: "Không thể tải danh sách mượn."
    }
}
