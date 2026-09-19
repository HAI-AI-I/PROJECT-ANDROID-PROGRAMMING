package com.group_7.library_management.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group_7.library_management.components.AppSnackbarController
import com.group_7.library_management.data.network.NetworkMonitor
import com.group_7.library_management.data.repository.FavoriteRepository
import com.group_7.library_management.models.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class FavoriteUiState(
    val books: List<Book> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val networkMonitor: NetworkMonitor,
    private val snackbarController: AppSnackbarController
) : ViewModel() {
    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            favoriteRepository.observeCachedFavorites().collect { books ->
                _uiState.update { it.copy(books = books, isLoading = false) }
            }
        }
        refresh()
    }

    fun refresh() {
        if (!networkMonitor.isConnected.value) {
            _uiState.update { it.copy(isLoading = false) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { favoriteRepository.refreshFavorites() }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = throwable.toMessage())
                    }
                }
        }
    }

    fun removeFavorite(book: Book) {
        if (!networkMonitor.isConnected.value) {
            snackbarController.show("Không có kết nối mạng. Không thể xóa sách yêu thích.")
            return
        }
        viewModelScope.launch {
            runCatching { favoriteRepository.setFavorite(book, false) }
                .onSuccess { snackbarController.show("Đã xóa khỏi sách yêu thích.") }
                .onFailure { snackbarController.show(it.toMessage()) }
        }
    }

    private fun Throwable.toMessage(): String = when (this) {
        is IOException -> "Không thể kết nối đến máy chủ."
        is HttpException -> "Máy chủ trả về lỗi HTTP ${code()}."
        else -> message ?: "Không thể tải sách yêu thích."
    }
}
