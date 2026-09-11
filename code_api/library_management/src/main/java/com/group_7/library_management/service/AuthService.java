package com.group_7.library_management.service;

import com.group_7.library_management.dto.RegisterRequest;
import com.group_7.library_management.dto.UserResponse;
import com.group_7.library_management.entity.User;
import com.group_7.library_management.exception.ConflictException;
import com.group_7.library_management.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String fullName = normalizeFullName(request.fullName());
        String email = request.email().strip().toLowerCase(Locale.ROOT);
        String phone = request.phone().strip();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Email đã được sử dụng");
        }

        if (userRepository.existsByPhone(phone)) {
            throw new ConflictException("Số điện thoại đã được sử dụng");
        }

        User user = new User(
                fullName,
                email,
                phone,
                passwordEncoder.encode(request.password())
        );

        return UserResponse.from(userRepository.save(user));
    }

    private String normalizeFullName(String fullName) {
        return fullName.strip().replaceAll("\\s+", " ");
    }

}
