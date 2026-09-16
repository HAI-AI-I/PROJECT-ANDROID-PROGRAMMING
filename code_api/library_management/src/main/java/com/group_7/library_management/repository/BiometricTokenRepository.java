package com.group_7.library_management.repository;

import com.group_7.library_management.entity.BiometricToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BiometricTokenRepository extends JpaRepository<BiometricToken, Long> {

    Optional<BiometricToken> findByTokenHash(String tokenHash);

    void deleteAllByUserId(Long userId);
}
