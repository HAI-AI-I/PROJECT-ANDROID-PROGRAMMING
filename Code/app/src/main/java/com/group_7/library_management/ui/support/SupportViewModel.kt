package com.group_7.library_management.ui.support

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class BorrowedBookItem(
    val id: String,
    val title: String,
    val author: String,
    val dueDate: String
)

data class FaqItem(
    val id: String,
    val question: String,
    val answer: String,
    val category: String,
    val subtitle: String
)

data class SupportRequestItem(
    val id: String,
    val bookTitle: String,
    val problemType: String,
    val description: String,
    val date: String,
    val status: String // "Đang xử lý" or "Đã giải quyết"
)

data class SupportUiState(
    val searchQuery: String = "",
    val selectedCategory: String = "Tất cả",
    val borrowedBooks: List<BorrowedBookItem> = listOf(
        BorrowedBookItem("1", "Clean Architecture", "Robert C. Martin", "Hạn trả: 15/08/2026"),
        BorrowedBookItem("2", "Design Patterns", "Gang of Four", "Hạn trả: 19/08/2026"),
        BorrowedBookItem("3", "Mạng máy tính căn bản", "Lê Văn C", "Hạn trả: 03/08/2026"),
        BorrowedBookItem("4", "Code Dạo Ký Sự", "Phạm Huy Hoàng", "Hạn trả: 05/09/2026")
    ),
    val faqs: List<FaqItem> = listOf(
        FaqItem("1", "Làm thế nào để mượn sách?", "Bạn có thể mượn sách bằng cách tìm kiếm sách trên ứng dụng, quét mã QR tại quầy thư viện hoặc liên hệ thủ thư.", "Mượn sách", "Quy trình, thủ tục, điều kiện mượn"),
        FaqItem("2", "Tôi không thể mượn sách", "Kiểm tra lại số lượng sách đang mượn tối đa, tài khoản có bị quá hạn phạt hay không, hoặc liên hệ admin.", "Mượn sách", "Lý do, cách khắc phục"),
        FaqItem("3", "Sách đang được mượn", "Nếu sách đã hết bản sao, bạn có thể thực hiện đặt chỗ (reservation) để được thông báo khi sách được trả.", "Mượn sách", "Kiểm tra tình trạng, đặt chỗ"),
        FaqItem("4", "Điều kiện mượn sách là gì?", "Thẻ thư viện còn hiệu lực, không có sách quá hạn chưa thanh toán tiền phạt.", "Mượn sách", "Điều kiện"),
        FaqItem("5", "Mỗi lần được mượn bao nhiêu cuốn?", "Mỗi thành viên được mượn tối đa 5 cuốn sách trong thời gian 14 ngày.", "Mượn sách", "Số lượng"),
        FaqItem("6", "Làm sao biết sách còn hay không?", "Trang chi tiết sách hiển thị số lượng bản sao sẵn có tại thư viện.", "Mượn sách", "Tình trạng"),
        FaqItem("7", "Đặt chỗ sách như thế nào?", "Nhấn vào nút 'Đặt chỗ' ở trang chi tiết sách khi sách đã được mượn hết.", "Mượn sách", "Đặt chỗ")
    ),
    val supportRequests: List<SupportRequestItem> = listOf(
        SupportRequestItem("1", "Clean Architecture", "Sách bị hỏng / lỗi", "Sách bị rách bìa, khó đọc.", "16/08/2026", "Đang xử lý"),
        SupportRequestItem("2", "Design Patterns", "Không thể gia hạn sách", "Hệ thống báo lỗi khi bấm gia hạn lần 2.", "15/08/2026", "Đã giải quyết"),
        SupportRequestItem("3", "Mạng máy tính căn bản", "Đã trả nhưng chưa cập nhật", "Đã trả sách tại quầy nhưng app vẫn hiện đang mượn.", "04/08/2026", "Đã giải quyết"),
        SupportRequestItem("4", "Code Dạo Ký Sự", "Khác", "Yêu cầu đổi thời gian nhận sách.", "23/08/2026", "Đã giải quyết")
    ),
    val selectedBook: BorrowedBookItem? = null,
    val selectedProblem: String = "",
    val description: String = "",
    val selectedRequestTab: String = "Tất cả",
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class SupportViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(SupportUiState())
    val uiState: StateFlow<SupportUiState> = _uiState.asStateFlow()

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun selectBook(book: BorrowedBookItem?) {
        _uiState.update { it.copy(selectedBook = book) }
    }

    fun selectProblem(problem: String) {
        _uiState.update { it.copy(selectedProblem = problem) }
    }

    fun updateDescription(desc: String) {
        _uiState.update { it.copy(description = desc) }
    }

    fun selectRequestTab(tab: String) {
        _uiState.update { it.copy(selectedRequestTab = tab) }
    }

    fun createSupportRequest(onSuccess: () -> Unit) {
        val current = _uiState.value
        if (current.selectedProblem.isBlank()) {
            _uiState.update { it.copy(error = "Vui lòng chọn vấn đề gặp phải") }
            return
        }
        if (current.selectedProblem == "Khác" && current.description.isBlank()) {
            _uiState.update { it.copy(error = "Vui lòng mô tả chi tiết cho mục Khác") }
            return
        }

        val newReq = SupportRequestItem(
            id = (current.supportRequests.size + 1).toString(),
            bookTitle = current.selectedBook?.title ?: "Sách chung",
            problemType = current.selectedProblem,
            description = current.description.ifBlank { "Không có mô tả chi tiết" },
            date = "Hôm nay",
            status = "Đang xử lý"
        )

        _uiState.update {
            it.copy(
                supportRequests = listOf(newReq) + it.supportRequests,
                error = null,
                successMessage = "Gửi yêu cầu thành công!",
                selectedProblem = "",
                description = "",
                selectedBook = null
            )
        }
        onSuccess()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
