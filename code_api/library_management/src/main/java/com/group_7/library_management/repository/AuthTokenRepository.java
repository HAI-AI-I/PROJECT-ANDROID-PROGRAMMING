package com.group_7.library_management.repository;

import com.group_7.library_management.entity.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

    Optional<AuthToken> findByTokenHash(String tokenHash);

    void deleteByTokenHash(String tokenHash);
}
