package com.group_7.library_management.ui.book

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group_7.library_management.data.network.NetworkMonitor
import com.group_7.library_management.data.repository.BorrowRepository
import com.group_7.library_management.models.BorrowPayment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class BorrowPaymentUiState(
    val payment: BorrowPayment? = null,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val isPaid: Boolean = false
)

@HiltViewModel
class BorrowPaymentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val borrowRepository: BorrowRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {
    private val orderId: Long = checkNotNull(savedStateHandle["orderId"])
    private val _uiState = MutableStateFlow(BorrowPaymentUiState())
    val uiState: StateFlow<BorrowPaymentUiState> = _uiState.asStateFlow()
    private var monitorJob: Job? = null

    init {
        startMonitoring()
    }

    fun retry() = startMonitoring(force = true)

    private fun startMonitoring(force: Boolean = false) {
        if (monitorJob?.isActive == true && !force) return
        monitorJob?.cancel()
        monitorJob = viewModelScope.launch {
            var firstLoad = true
            while (isActive) {
                if (!networkMonitor.isConnected.value) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = if (it.payment == null) {
                                "Không có kết nối mạng để tải thông tin thanh toán."
                            } else null
                        )
                    }
                    delay(POLL_INTERVAL_MILLIS)
                    continue
                }

                _uiState.update {
                    it.copy(
                        isLoading = firstLoad && it.payment == null,
                        isRefreshing = !firstLoad && it.payment != null,
                        errorMessage = null
                    )
                }
                runCatching { borrowRepository.getBorrowPayment(orderId) }
                    .onSuccess { payment ->
                        val paid = payment.paymentStatus == "PAID"
                        _uiState.value = BorrowPaymentUiState(
                            payment = payment,
                            isLoading = false,
                            isRefreshing = false,
                            isPaid = paid
                        )
                        if (paid) return@launch
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                errorMessage = if (it.payment == null) error.toUserMessage() else null
                            )
                        }
                    }
                firstLoad = false
                delay(POLL_INTERVAL_MILLIS)
            }
        }
    }

    private fun Throwable.toUserMessage(): String = when (this) {
        is IOException -> "Không thể kết nối đến máy chủ."
        is HttpException -> runCatching {
            JSONObject(response()?.errorBody()?.string().orEmpty()).optString("message")
        }.getOrDefault("").ifBlank { "Máy chủ trả về lỗi HTTP ${code()}." }
        else -> message ?: "Không thể tải thông tin thanh toán."
    }

    private companion object {
        const val POLL_INTERVAL_MILLIS = 3_000L
    }
}
