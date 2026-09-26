package com.group_7.library_management.repository;

import com.group_7.library_management.entity.SupportRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SupportRequestRepository extends JpaRepository<SupportRequest, Long> {
    Page<SupportRequest> findAllByOrderByCreatedAtDesc(Pageable pageable);
    List<SupportRequest> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<SupportRequest> findByIdAndUserId(Long id, Long userId);
}
