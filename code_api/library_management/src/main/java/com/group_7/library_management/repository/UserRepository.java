package com.group_7.library_management.repository;

import com.group_7.library_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    public boolean existsByEmailIgnoreCase(String email);

    public boolean existsByPhone(String phone);

    public Optional<User> findByEmailIgnoreCase(String email);

    public Optional<User> findByPhone(String phone);

    public Optional<User> findByEmailIgnoreCaseOrPhone(String email, String phone);
}
