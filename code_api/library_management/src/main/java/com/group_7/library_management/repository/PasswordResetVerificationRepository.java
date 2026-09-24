package com.group_7.library_management.repository;

import com.group_7.library_management.entity.PasswordResetVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetVerificationRepository
        extends JpaRepository<PasswordResetVerification, String> {

    long deleteAllByUserId(Long userId);
}
