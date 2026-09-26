package com.group_7.library_management.service;

import com.group_7.library_management.dto.AdminHistoryResponse;
import com.group_7.library_management.exception.BadRequestException;
import com.group_7.library_management.repository.AdminHistoryProjection;
import com.group_7.library_management.repository.BorrowRecordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Set;

@Service
public class AdminHistoryService {

    private static final Set<String> SUPPORTED_ACTIONS = Set.of(
            "request", "borrow", "return", "cancel_request"
    );

    private final BorrowRecordRepository borrowRecordRepository;

    public AdminHistoryService(BorrowRecordRepository borrowRecordRepository) {
        this.borrowRecordRepository = borrowRecordRepository;
    }

    @Transactional(readOnly = true)
    public Page<AdminHistoryResponse> getHistory(String search, String action, Pageable pageable) {
        String normalizedSearch = search == null || search.isBlank() ? null : search.strip();
        String normalizedAction = normalizeAction(action);
        return borrowRecordRepository.searchAdminHistory(normalizedSearch, normalizedAction, pageable)
                .map(this::toResponse);
    }

    private String normalizeAction(String action) {
        if (action == null || action.isBlank()) return null;
        String normalized = action.strip().toLowerCase(Locale.ROOT);
        if (!SUPPORTED_ACTIONS.contains(normalized)) {
            throw new BadRequestException("Loại hoạt động lịch sử không hợp lệ");
        }
        return normalized;
    }

    private AdminHistoryResponse toResponse(AdminHistoryProjection event) {
        String description = switch (event.getAction()) {
            case "request" -> "Đã tạo yêu cầu mượn sách";
            case "borrow" -> "Đã nhận sách tại thư viện";
            case "return" -> "Đã trả sách cho thư viện";
            case "cancel_request" -> "Đã hủy yêu cầu mượn sách";
            default -> "Hoạt động mượn sách";
        };
        return new AdminHistoryResponse(
                event.getEventId(),
                event.getAction(),
                event.getUserName(),
                event.getBookTitle(),
                event.getReferenceCode(),
                description,
                event.getEventAt()
        );
    }
}
