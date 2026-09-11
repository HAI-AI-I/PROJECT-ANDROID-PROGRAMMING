package com.group_7.library_management.service;

import com.group_7.library_management.dto.UserResponse;
import com.group_7.library_management.entity.User;
import com.group_7.library_management.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Test
    void getAllUsersReturnsSafeUserResponses() {
        User user = new User(
                "Nguyễn Văn A",
                "user@example.com",
                "0901234567",
                "encoded-password"
        );
        when(userRepository.findAll(any(Sort.class))).thenReturn(List.of(user));

        List<UserResponse> result = new UserService(userRepository).getAllUsers();

        assertEquals(1, result.size());
        assertEquals("Nguyễn Văn A", result.getFirst().fullName());
        assertEquals("user@example.com", result.getFirst().email());
    }
}
