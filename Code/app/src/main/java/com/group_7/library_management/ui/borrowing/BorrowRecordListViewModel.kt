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
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val selectedTab: BorrowTab = BorrowTab.ALL,
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
    private var currentPage = 0

    init {
        refresh()
    }

    fun selectTab(tab: BorrowTab) {
        if (_uiState.value.selectedTab == tab && _uiState.value.orders.isNotEmpty()) return
        if (_uiState.value.selectedTab != tab) refreshJob?.cancel()
        refresh(tab)
    }

    fun refresh(tab: BorrowTab = _uiState.value.selectedTab) {
        if (refreshJob?.isActive == true) {
            if (_uiState.value.selectedTab == tab) return
            refreshJob?.cancel()
        }
        refreshJob = viewModelScope.launch {
            currentPage = 0
            _uiState.value = _uiState.value.copy(
                orders = emptyList(),
                isLoading = true,
                isLoadingMore = false,
                hasMore = true,
                selectedTab = tab,
                errorMessage = null
            )
            runCatching {
                repository.getBorrowOrders(tab.apiStatus, page = 1, pageSize = PAGE_SIZE)
            }
                .onSuccess { result ->
                    currentPage = result.page
                    _uiState.value = _uiState.value.copy(
                        orders = result.items,
                        isLoading = false,
                        hasMore = result.page < result.totalPages,
                        errorMessage = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        hasMore = false,
                        errorMessage = error.toUserMessage()
                    )
                }
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isLoading || state.isLoadingMore || !state.hasMore) return
        viewModelScope.launch {
            _uiState.value = state.copy(isLoadingMore = true)
            runCatching {
                repository.getBorrowOrders(
                    status = state.selectedTab.apiStatus,
                    page = currentPage + 1,
                    pageSize = PAGE_SIZE
                )
            }.onSuccess { result ->
                currentPage = result.page
                _uiState.value = _uiState.value.copy(
                    orders = (_uiState.value.orders + result.items).distinctBy { it.id },
                    isLoadingMore = false,
                    hasMore = result.page < result.totalPages
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(isLoadingMore = false)
                snackbarController.show(error.toUserMessage())
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
                        orders = if (_uiState.value.selectedTab == BorrowTab.ALL) {
                            _uiState.value.orders.map { order ->
                                if (order.id == result.order.id) result.order else order
                            }
                        } else {
                            _uiState.value.orders.filterNot { it.id == result.order.id }
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

    private companion object {
        const val PAGE_SIZE = 20
    }
}
