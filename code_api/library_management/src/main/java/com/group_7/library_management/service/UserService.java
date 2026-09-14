package com.group_7.library_management.service;

import com.group_7.library_management.dto.UserResponse;
import com.group_7.library_management.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        Sort newestFirst = Sort.by(Sort.Direction.ASC, "id");
        return userRepository.findAll(newestFirst)
                .stream()
                .map(user-> UserResponse.from(user))
                .toList();
    }
}
