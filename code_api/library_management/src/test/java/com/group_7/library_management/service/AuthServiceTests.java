package com.group_7.library_management.service;

import com.group_7.library_management.dto.RegisterRequest;
import com.group_7.library_management.dto.UserResponse;
import com.group_7.library_management.entity.User;
import com.group_7.library_management.exception.ConflictException;
import com.group_7.library_management.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTests {

    @Mock
    private UserRepository userRepository;

    @Test
    void registerNormalizesDataAndHashesPassword() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        AuthService authService = new AuthService(userRepository, passwordEncoder);
        RegisterRequest request = new RegisterRequest(
                "  Nguyễn   Văn A  ",
                "  USER@Example.com ",
                "0901234567",
                "password123"
        );

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("Nguyễn Văn A", response.fullName());
        assertEquals("user@example.com", response.email());
        assertNotEquals(request.password(), savedUser.getPasswordHash());
        assertTrue(passwordEncoder.matches(request.password(), savedUser.getPasswordHash()));
    }

    @Test
    void registerRejectsAnExistingEmail() {
        AuthService authService = new AuthService(userRepository, new BCryptPasswordEncoder());
        RegisterRequest request = new RegisterRequest(
                "Nguyễn Văn A",
                "user@example.com",
                "0901234567",
                "password123"
        );
        when(userRepository.existsByEmailIgnoreCase("user@example.com")).thenReturn(true);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> authService.register(request)
        );

        assertEquals("Email đã được sử dụng", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}
