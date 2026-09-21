package com.group_7.library_management.repository;

import com.group_7.library_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.id = :userId")
    Optional<User> findByIdForUpdate(@Param("userId") Long userId);

    public boolean existsByEmailIgnoreCase(String email);

    public boolean existsByPhone(String phone);

    public Optional<User> findByEmailIgnoreCase(String email);

    public Optional<User> findByPhone(String phone);

    public Optional<User> findByEmailIgnoreCaseOrPhone(String email, String phone);
}
