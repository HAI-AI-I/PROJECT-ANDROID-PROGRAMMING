package com.group_7.library_management.ui.book

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group_7.library_management.components.AppSnackbarController
import com.group_7.library_management.data.network.NetworkMonitor
import com.group_7.library_management.data.local.preferences.CheckLogin
import com.group_7.library_management.data.repository.BookRepository
import com.group_7.library_management.models.Book
import com.group_7.library_management.models.BookReview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class BookReviewsUiState(
    val book: Book? = null,
    val reviews: List<BookReview> = emptyList(),
    val totalReviews: Long = 0,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val isCheckingMyReview: Boolean = false,
    val isSubmitting: Boolean = false,
    val isDeleting: Boolean = false,
    val isEditorVisible: Boolean = false,
    val editingReview: BookReview? = null,
    val errorMessage: String? = null
) {
    val canLoadMore: Boolean get() = currentPage < totalPages
}

@HiltViewModel
class BookReviewsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bookRepository: BookRepository,
    private val networkMonitor: NetworkMonitor,
    checkLogin: CheckLogin,
    private val snackbarController: AppSnackbarController
) : ViewModel() {
    private val bookId: String = savedStateHandle["bookId"] ?: ""
    private val _uiState = MutableStateFlow(BookReviewsUiState())
    val uiState: StateFlow<BookReviewsUiState> = _uiState.asStateFlow()
    val currentUserId: Long? = checkLogin.getSavedUserId()?.toLongOrNull()

    init {
        refresh()
    }

    fun refresh() {
        if (bookId.isBlank()) {
            _uiState.value = BookReviewsUiState(
                isLoading = false,
                errorMessage = "Không tìm thấy mã sách."
            )
            return
        }
        if (!networkMonitor.isConnected.value) {
            _uiState.update {
                it.copy(
                    reviews = emptyList(),
                    isLoading = false,
                    errorMessage = "Không có kết nối mạng. Đánh giá chỉ được tải từ máy chủ."
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                val book = bookRepository.getBookDetail(bookId)
                val page = bookRepository.getBookReviews(bookId, 1, PAGE_SIZE)
                book to page
            }.onSuccess { (book, page) ->
                _uiState.update {
                    it.copy(
                        book = book,
                        reviews = page.items,
                        totalReviews = page.total,
                        currentPage = page.page,
                        totalPages = page.totalPages,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = throwable.toMessage())
                }
            }
        }
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (state.isLoading || state.isLoadingMore || !state.canLoadMore) return
        if (!networkMonitor.isConnected.value) {
            snackbarController.show("Không có kết nối mạng.")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            runCatching {
                bookRepository.getBookReviews(bookId, state.currentPage + 1, PAGE_SIZE)
            }.onSuccess { page ->
                _uiState.update {
                    it.copy(
                        reviews = (it.reviews + page.items).distinctBy(BookReview::id),
                        totalReviews = page.total,
                        currentPage = page.page,
                        totalPages = page.totalPages,
                        isLoadingMore = false
                    )
                }
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoadingMore = false) }
                snackbarController.show(throwable.toMessage())
            }
        }
    }

    fun openReviewEditor() {
        if (_uiState.value.isCheckingMyReview) return
        if (!networkMonitor.isConnected.value) {
            snackbarController.show("Không có kết nối mạng.")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingMyReview = true) }
            runCatching { bookRepository.getMyBookReview(bookId) }
                .onSuccess { review ->
                    _uiState.update {
                        it.copy(
                            isCheckingMyReview = false,
                            isEditorVisible = true,
                            editingReview = review
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isCheckingMyReview = false) }
                    snackbarController.show(throwable.toMessage())
                }
        }
    }

    fun editReview(review: BookReview) {
        _uiState.update { it.copy(isEditorVisible = true, editingReview = review) }
    }

    fun dismissEditor() {
        if (_uiState.value.isSubmitting) return
        _uiState.update { it.copy(isEditorVisible = false, editingReview = null) }
    }

    fun submitReview(rating: Int, comment: String) {
        if (_uiState.value.isSubmitting) return
        if (!networkMonitor.isConnected.value) {
            snackbarController.show("Không có kết nối mạng.")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            runCatching {
                val editingReview = _uiState.value.editingReview
                if (editingReview == null) {
                    bookRepository.createBookReview(bookId, rating, comment)
                } else {
                    bookRepository.updateBookReview(
                        bookId,
                        editingReview.id,
                        rating,
                        comment
                    )
                }
            }.onSuccess {
                snackbarController.show(
                    if (_uiState.value.editingReview == null) {
                        "Đã gửi đánh giá."
                    } else {
                        "Đã cập nhật đánh giá."
                    }
                )
                _uiState.update { state ->
                    state.copy(
                        isSubmitting = false,
                        isEditorVisible = false,
                        editingReview = null
                    )
                }
                refresh()
            }.onFailure { throwable ->
                snackbarController.show(throwable.toMessage())
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }

    fun deleteReview(reviewId: Long) {
        if (_uiState.value.isDeleting) return
        if (!networkMonitor.isConnected.value) {
            snackbarController.show("Không có kết nối mạng.")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }
            runCatching { bookRepository.deleteBookReview(bookId, reviewId) }
                .onSuccess {
                    snackbarController.show("Đã xóa đánh giá.")
                    _uiState.update { it.copy(isDeleting = false) }
                    refresh()
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isDeleting = false) }
                    snackbarController.show(throwable.toMessage())
                }
        }
    }

    private fun Throwable.toMessage(): String = when (this) {
        is IOException -> "Không thể kết nối đến máy chủ."
        is HttpException -> when (code()) {
            401 -> "Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại."
            404 -> "Không tìm thấy sách."
            409 -> "Bạn đã đánh giá sách này."
            else -> "Máy chủ trả về lỗi HTTP ${code()}."
        }
        else -> message ?: "Không thể tải đánh giá."
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
