package com.group_7.library_management.service;

import com.group_7.library_management.dto.AdminSupportReplyRequest;
import com.group_7.library_management.dto.CreateSupportRequest;
import com.group_7.library_management.dto.SupportRequestResponse;
import com.group_7.library_management.entity.Notification;
import com.group_7.library_management.entity.NotificationActionType;
import com.group_7.library_management.entity.NotificationType;
import com.group_7.library_management.entity.SupportRequest;
import com.group_7.library_management.entity.SupportRequestStatus;
import com.group_7.library_management.entity.User;
import com.group_7.library_management.exception.ConflictException;
import com.group_7.library_management.exception.ResourceNotFoundException;
import com.group_7.library_management.repository.NotificationRepository;
import com.group_7.library_management.repository.BookRepository;
import com.group_7.library_management.repository.SupportRequestRepository;
import com.group_7.library_management.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SupportRequestService {

    private final SupportRequestRepository supportRequestRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final BookRepository bookRepository;

    public SupportRequestService(
            SupportRequestRepository supportRequestRepository,
            UserRepository userRepository,
            NotificationRepository notificationRepository,
            BookRepository bookRepository
    ) {
        this.supportRequestRepository = supportRequestRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public SupportRequestResponse create(Long userId, CreateSupportRequest request) {
        User user = requireUser(userId);
        SupportRequest supportRequest = new SupportRequest(
                user,
                request.bookId() == null ? null : bookRepository.findById(request.bookId())
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách")),
                normalize(request.subject()),
                request.message().strip()
        );
        return SupportRequestResponse.from(supportRequestRepository.save(supportRequest));
    }

    @Transactional(readOnly = true)
    public List<SupportRequestResponse> getMine(Long userId) {
        requireUser(userId);
        return supportRequestRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(SupportRequestResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SupportRequestResponse getMineById(Long userId, Long requestId) {
        return SupportRequestResponse.from(supportRequestRepository.findByIdAndUserId(requestId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu hỗ trợ")));
    }

    @Transactional(readOnly = true)
    public Page<SupportRequestResponse> getAll(Pageable pageable) {
        return supportRequestRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(SupportRequestResponse::from);
    }

    @Transactional(readOnly = true)
    public SupportRequestResponse getById(Long requestId) {
        return SupportRequestResponse.from(requireRequest(requestId));
    }

    @Transactional
    public SupportRequestResponse reply(Long requestId, AdminSupportReplyRequest request) {
        SupportRequest supportRequest = requireRequest(requestId);
        ensureEditable(supportRequest);
        supportRequest.reply(request.adminReply().strip());
        notifyUser(
                supportRequest,
                "Thư viện đã phản hồi yêu cầu hỗ trợ",
                request.adminReply().strip(),
                NotificationType.INFO
        );
        return SupportRequestResponse.from(supportRequest);
    }

    @Transactional
    public SupportRequestResponse resolve(Long requestId) {
        SupportRequest supportRequest = requireRequest(requestId);
        ensureEditable(supportRequest);
        supportRequest.resolve();
        notifyUser(
                supportRequest,
                "Yêu cầu hỗ trợ đã được xử lý",
                "Yêu cầu “" + supportRequest.getSubject() + "” đã được thư viện đánh dấu là đã xử lý.",
                NotificationType.SUCCESS
        );
        return SupportRequestResponse.from(supportRequest);
    }

    @Transactional
    public SupportRequestResponse close(Long requestId) {
        SupportRequest supportRequest = requireRequest(requestId);
        if (supportRequest.getStatus() == SupportRequestStatus.CLOSED) {
            throw new ConflictException("Yêu cầu hỗ trợ đã được đóng");
        }
        supportRequest.close();
        return SupportRequestResponse.from(supportRequest);
    }

    private User requireUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }

    private SupportRequest requireRequest(Long requestId) {
        return supportRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu hỗ trợ"));
    }

    private void ensureEditable(SupportRequest request) {
        if (request.getStatus() == SupportRequestStatus.RESOLVED
                || request.getStatus() == SupportRequestStatus.CLOSED) {
            throw new ConflictException("Yêu cầu hỗ trợ này không còn có thể chỉnh sửa");
        }
    }

    private void notifyUser(
            SupportRequest request,
            String title,
            String message,
            NotificationType type
    ) {
        notificationRepository.save(new Notification(
                "support:" + request.getId() + ":" + UUID.randomUUID(),
                request.getUser(),
                null,
                title,
                message,
                type,
                NotificationActionType.SUPPORT,
                request.getId()
        ));
    }

    private String normalize(String value) {
        return value.strip().replaceAll("\\s+", " ");
    }
}
