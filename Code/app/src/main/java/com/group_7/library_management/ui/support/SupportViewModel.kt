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
    val categoryFaqs: Map<String, List<FaqItem>> = mapOf(
        "Mượn sách" to listOf(
            FaqItem("m1", "Cách thức mượn sách?", "Bạn có thể mượn sách bằng cách tìm kiếm sách trên ứng dụng, quét mã QR tại quầy thư viện hoặc liên hệ admin để được hướng dẫn chi tiết.", "Mượn sách", "Quy trình, thủ tục, điều kiện mượn"),
            FaqItem("m2", "Tại sao tôi không thể mượn sách", "Kiểm tra lại số lượng sách đang mượn tối đa, tài khoản có bị quá hạn phạt hay không, hoặc liên hệ admin.", "Mượn sách", "Lý do, cách khắc phục"),
            FaqItem("m3", "Sách đang được mượn", "Nếu sách đã hết bản sao, bạn có thể thực hiện đặt chỗ (reservation) để được thông báo khi sách được trả.", "Mượn sách", "Kiểm tra tình trạng, đặt chỗ"),
            FaqItem("m4", "Điều kiện mượn sách ?", "Thẻ thành viên thư viện còn hiệu lực, tài khoản không có sách quá hạn chưa thanh toán tiền phạt.", "Mượn sách", "Điều kiện")
        ),
        "Trả sách" to listOf(
            FaqItem("t1", "Quy trình trả sách như thế nào?", "Bạn mang sách đến quầy thủ thư hoặc bỏ vào tủ trả sách tự động của thư viện trước hoặc đúng hạn trả.", "Trả sách", "Địa điểm, thời gian, thủ tục trả"),
            FaqItem("t2", "Có thể trả sách hộ người khác được không?", "Được, người trả hộ cần mang theo thẻ thư viện của bạn hoặc cung cấp mã số sinh viên/thành viên chính xác.", "Trả sách", "Quy định ủy quyền trả sách"),
            FaqItem("t3", "Trả sách ngoài giờ hành chính được không?", "Có, bạn có thể sử dụng tủ trả sách tự động đặt tại sảnh chính thư viện hoạt động 24/7.", "Trả sách", "Hướng dẫn bỏ tủ trả sách tự động")
        ),
        "Gia hạn sách" to listOf(
            FaqItem("g1", "Làm sao để gia hạn thời gian mượn sách?", "Vào mục 'Mượn sách' trên app, chọn sách cần gia hạn và nhấn 'Gia hạn' trước khi đến hạn trả ít nhất 1 ngày.", "Gia hạn sách", "Thao tác trên app hoặc tại quầy"),
            FaqItem("g2", "Mỗi cuốn sách được gia hạn tối đa mấy lần?", "Mỗi cuốn sách được phép gia hạn tối đa 2 lần, mỗi lần thêm 7 ngày (với điều kiện không có người khác đặt chỗ).", "Gia hạn sách", "Quy định số lần và thời gian"),
            FaqItem("g3", "Tại sao tôi không thể gia hạn sách?", "Sách đã quá hạn, hoặc đã hết số lần gia hạn cho phép, hoặc có thành viên khác đã đặt chỗ trước.", "Gia hạn sách", "Điều kiện gia hạn")
        ),
        "Quá hạn / Mất / Hỏng" to listOf(
            FaqItem("q1", "Phí phạt quá hạn sách được tính như thế nào?", "Phí phạt quá hạn là 5.000đ/ngày/cuốn đối với sách thường và 10.000đ/ngày/cuốn đối với sách giáo trình/tham khảo.", "Quá hạn / Mất / Hỏng", "Biểu phí phạt theo ngày"),
            FaqItem("q2", "Làm gì khi làm mất sách thư viện?", "Bạn cần đến ngay quầy thủ thư khai báo mất sách để tiến hành thủ tục đền bù bằng tiền mặt theo giá bìa hoặc mua sách mới đền bù.", "Quá hạn / Mất / Hỏng", "Thủ tục đền bù mất sách"),
            FaqItem("q3", "Sách bị rách, hỏng trang thì xử lý ra sao?", "Thủ thư sẽ kiểm tra mức độ hư hỏng để quyết định mức phạt đền bù phục chế hoặc đền sách mới.", "Quá hạn / Mất / Hỏng", "Quy định xử lý hư hỏng")
        )
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

    val selectedCategoryFaqs: List<FaqItem>
        get() = _uiState.value.categoryFaqs[_uiState.value.selectedCategory] ?: emptyList()

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
