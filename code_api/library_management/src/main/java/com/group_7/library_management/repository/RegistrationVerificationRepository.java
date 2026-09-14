package com.group_7.library_management.repository;

import com.group_7.library_management.entity.RegistrationVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationVerificationRepository
        extends JpaRepository<RegistrationVerification, String> {

    long deleteByEmailIgnoreCaseOrPhone(String email, String phone);
}
