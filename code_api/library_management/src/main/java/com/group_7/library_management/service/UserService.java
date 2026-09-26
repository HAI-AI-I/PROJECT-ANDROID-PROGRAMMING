package com.group_7.library_management.service;

import com.group_7.library_management.dto.AdminCreateReaderRequest;
import com.group_7.library_management.dto.AdminReaderResponse;
import com.group_7.library_management.dto.AdminUpdateReaderRequest;
import com.group_7.library_management.dto.UserResponse;
import com.group_7.library_management.entity.BorrowStatus;
import com.group_7.library_management.entity.User;
import com.group_7.library_management.entity.UserRole;
import com.group_7.library_management.exception.ConflictException;
import com.group_7.library_management.exception.ResourceNotFoundException;
import com.group_7.library_management.repository.BorrowRecordRepository;
import com.group_7.library_management.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public UserService(
            UserRepository userRepository,
            BorrowRecordRepository borrowRecordRepository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService
    ) {
        this.userRepository = userRepository;
        this.borrowRecordRepository = borrowRecordRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        Sort newestFirst = Sort.by(Sort.Direction.ASC, "id");
        return userRepository.findAll(newestFirst)
                .stream()
                .map(user-> UserResponse.from(user))
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getUsers(
            String search,
            UserRole role,
            Boolean active,
            Pageable pageable
    ) {
        String normalizedSearch = search == null || search.isBlank() ? null : search.strip();
        return userRepository.searchUsers(normalizedSearch, role, active, pageable)
                .map(UserResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<AdminReaderResponse> getReaders(String search, Boolean active, Pageable pageable) {
        String normalizedSearch = search == null || search.isBlank() ? null : search.strip();
        return userRepository.searchReaders(normalizedSearch, active, pageable)
                .map(this::toReaderResponse);
    }

    @Transactional(readOnly = true)
    public AdminReaderResponse getReader(Long readerId) {
        return toReaderResponse(requireReader(readerId));
    }

    @Transactional
    public AdminReaderResponse createReader(AdminCreateReaderRequest request) {
        String email = normalizeEmail(request.email());
        String phone = request.phone().strip();
        ensureUniqueContact(null, email, phone);

        User reader = new User(
                normalizeFullName(request.fullName()),
                email,
                phone,
                passwordEncoder.encode(request.password())
        );
        reader.setRole(UserRole.USER);
        reader.setActive(request.active());
        return toReaderResponse(userRepository.save(reader));
    }

    @Transactional
    public AdminReaderResponse updateReader(Long readerId, AdminUpdateReaderRequest request) {
        User reader = requireReader(readerId);
        String email = normalizeEmail(request.email());
        String phone = request.phone().strip();
        ensureUniqueContact(readerId, email, phone);

        reader.setFullName(normalizeFullName(request.fullName()));
        reader.setEmail(email);
        reader.setPhone(phone);
        reader.setActive(request.active());
        return toReaderResponse(userRepository.save(reader));
    }

    @Transactional
    public void deactivateReader(Long readerId) {
        User reader = requireReader(readerId);
        reader.setActive(false);
        userRepository.save(reader);
        tokenService.revokeAllUserTokens(readerId);
    }

    private AdminReaderResponse toReaderResponse(User reader) {
        long activeBorrowingCount = borrowRecordRepository.countByUserIdAndStatusIn(
                reader.getId(),
                Set.of(BorrowStatus.BORROWED, BorrowStatus.OVERDUE)
        );
        return AdminReaderResponse.from(reader, activeBorrowingCount);
    }

    private User requireReader(Long readerId) {
        return userRepository.findById(readerId)
                .filter(user -> user.getRole() == UserRole.USER)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy độc giả"));
    }

    private void ensureUniqueContact(Long currentUserId, String email, String phone) {
        userRepository.findByEmailIgnoreCase(email)
                .filter(user -> !user.getId().equals(currentUserId))
                .ifPresent(user -> { throw new ConflictException("Email đã được sử dụng"); });
        userRepository.findByPhone(phone)
                .filter(user -> !user.getId().equals(currentUserId))
                .ifPresent(user -> { throw new ConflictException("Số điện thoại đã được sử dụng"); });
    }

    private String normalizeFullName(String fullName) {
        return fullName.strip().replaceAll("\\s+", " ");
    }

    private String normalizeEmail(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }
}
