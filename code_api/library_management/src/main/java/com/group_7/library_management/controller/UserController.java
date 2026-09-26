package com.group_7.library_management.controller;

import com.group_7.library_management.dto.PagedResponse;
import com.group_7.library_management.dto.UserResponse;
import com.group_7.library_management.entity.UserRole;
import com.group_7.library_management.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public PagedResponse<UserResponse> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        int safePage = Math.max(page, 1) - 1;
        int safePageSize = Math.min(Math.max(pageSize, 1), 100);
        return PagedResponse.from(userService.getUsers(
                search,
                role,
                active,
                PageRequest.of(safePage, safePageSize, Sort.by(Sort.Direction.DESC, "createdAt"))
        ));
    }
}
