package com.group_7.library_management.ui.book

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group_7.library_management.components.AppSnackbarController
import com.group_7.library_management.data.network.NetworkMonitor
import com.group_7.library_management.data.repository.BookRepository
import com.group_7.library_management.data.repository.BorrowRepository
import com.group_7.library_management.data.repository.FavoriteRepository
import com.group_7.library_management.models.Book
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

data class BookDetailUiState(
    val book: Book? = null,
    val relatedBooks: List<Book> = emptyList(),
    val currentBorrowOrder: BorrowOrder? = null,
    val isFavorite: Boolean = false,
    val isUpdatingFavorite: Boolean = false,
    val isAvailabilitySubscribed: Boolean = false,
    val isUpdatingSubscription: Boolean = false,
    val isLoading: Boolean = true,
    val isLoadingRelated: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bookRepository: BookRepository,
    private val borrowRepository: BorrowRepository,
    private val favoriteRepository: FavoriteRepository,
    private val networkMonitor: NetworkMonitor,
    private val snackbarController: AppSnackbarController
) : ViewModel() {
    private val bookId: String = savedStateHandle["bookId"] ?: ""
    private val _uiState = MutableStateFlow(BookDetailUiState())
    val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

    init {
        refreshBook()
        loadAvailabilitySubscription()
        loadFavoriteStatus()
    }

    fun refreshBook() {
        if (bookId.isBlank()) {
            _uiState.value = BookDetailUiState(
                isLoading = false,
                errorMessage = "Không tìm thấy mã sách."
            )
            return
        }

        if (!networkMonitor.isConnected.value) {
            _uiState.update {
                it.copy(
                    book = null,
                    relatedBooks = emptyList(),
                    isLoading = false,
                    isLoadingRelated = false,
                    errorMessage = "Không có kết nối mạng. Chi tiết sách chỉ được tải từ máy chủ."
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isLoadingRelated = true,
                    errorMessage = null
                )
            }

            runCatching {
                val book = bookRepository.getBookDetail(bookId)
                val relatedBooks = runCatching {
                    bookRepository.getRelatedBookDetails(bookId, RELATED_BOOK_LIMIT)
                }.getOrDefault(emptyList())
                val currentBorrowOrder = runCatching {
                    borrowRepository.getCurrentBorrowOrder(bookId.toLong())
                }.getOrNull()
                Triple(book, relatedBooks, currentBorrowOrder)
            }
                .onSuccess { (book, relatedBooks, currentBorrowOrder) ->
                    _uiState.update {
                        it.copy(
                            book = book,
                            relatedBooks = relatedBooks,
                            currentBorrowOrder = currentBorrowOrder,
                            isLoading = false,
                            isLoadingRelated = false
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            book = null,
                            relatedBooks = emptyList(),
                            isLoading = false,
                            isLoadingRelated = false,
                            errorMessage = throwable.toMessage()
                        )
                    }
                }
        }
    }

    fun toggleAvailabilitySubscription() {
        if (_uiState.value.isUpdatingSubscription) return
        if (!networkMonitor.isConnected.value) {
            snackbarController.show("Không có kết nối mạng.")
            return
        }

        val shouldSubscribe = !_uiState.value.isAvailabilitySubscribed
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingSubscription = true) }
            runCatching {
                bookRepository.setAvailabilitySubscription(bookId, shouldSubscribe)
            }.onSuccess { subscribed ->
                _uiState.update { it.copy(isAvailabilitySubscribed = subscribed) }
                snackbarController.show(
                    if (subscribed) {
                        "Đã bật thông báo khi sách sắp hết hoặc có lại."
                    } else {
                        "Đã tắt thông báo cho sách này."
                    }
                )
            }.onFailure { throwable ->
                snackbarController.show(throwable.toMessage())
            }
            _uiState.update { it.copy(isUpdatingSubscription = false) }
        }
    }

    fun toggleFavorite() {
        val state = _uiState.value
        if (state.isUpdatingFavorite) return
        val book = state.book ?: return
        if (!networkMonitor.isConnected.value) {
            snackbarController.show("Không có kết nối mạng. Không thể cập nhật yêu thích.")
            return
        }

        val shouldFavorite = !state.isFavorite
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingFavorite = true) }
            runCatching { favoriteRepository.setFavorite(book, shouldFavorite) }
                .onSuccess { favorite ->
                    _uiState.update {
                        it.copy(isFavorite = favorite, isUpdatingFavorite = false)
                    }
                    snackbarController.show(
                        if (favorite) "Đã thêm vào sách yêu thích."
                        else "Đã xóa khỏi sách yêu thích."
                    )
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isUpdatingFavorite = false) }
                    snackbarController.show(throwable.toMessage())
                }
        }
    }

    fun requestBorrow(onAvailable: () -> Unit) {
        if (_uiState.value.currentBorrowOrder != null) return
        val book = _uiState.value.book
        if (book == null) {
            snackbarController.show("Không thể kiểm tra tình trạng sách.")
            return
        }
        if (book.availableCopies <= 0) {
            snackbarController.show("Sách hiện đã hết. Bạn có thể bật chuông để nhận thông báo khi sách có lại.")
            return
        }
        onAvailable()
    }

    private fun loadAvailabilitySubscription() {
        if (bookId.isBlank() || !networkMonitor.isConnected.value) return
        viewModelScope.launch {
            runCatching { bookRepository.isAvailabilitySubscribed(bookId) }
                .onSuccess { subscribed ->
                    _uiState.update { it.copy(isAvailabilitySubscribed = subscribed) }
                }
        }
    }

    private fun loadFavoriteStatus() {
        if (bookId.isBlank() || !networkMonitor.isConnected.value) return
        viewModelScope.launch {
            runCatching { favoriteRepository.isFavorite(bookId) }
                .onSuccess { favorite ->
                    _uiState.update { it.copy(isFavorite = favorite) }
                }
        }
    }

    private fun Throwable.toMessage(): String = when (this) {
        is IOException -> "Không thể kết nối đến máy chủ."
        is HttpException -> if (code() == 404) {
            "Không tìm thấy sách."
        } else {
            "Máy chủ trả về lỗi HTTP ${code()}."
        }
        else -> message ?: "Không thể tải chi tiết sách."
    }

    companion object {
        private const val RELATED_BOOK_LIMIT = 10
    }
}
