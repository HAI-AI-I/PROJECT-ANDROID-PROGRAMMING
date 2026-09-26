package com.group_7.library_management.controller;

import com.group_7.library_management.dto.AdminCreateReaderRequest;
import com.group_7.library_management.dto.AdminReaderResponse;
import com.group_7.library_management.dto.AdminUpdateReaderRequest;
import com.group_7.library_management.dto.PagedResponse;
import com.group_7.library_management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/readers")
public class AdminReaderController {

    private final UserService userService;

    public AdminReaderController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public PagedResponse<AdminReaderResponse> getReaders(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        int safePage = Math.max(page, 1) - 1;
        int safePageSize = Math.min(Math.max(pageSize, 1), 100);
        return PagedResponse.from(userService.getReaders(
                search,
                active,
                PageRequest.of(safePage, safePageSize, Sort.by(Sort.Direction.DESC, "createdAt"))
        ));
    }

    @GetMapping("/{readerId}")
    public AdminReaderResponse getReader(@PathVariable Long readerId) {
        return userService.getReader(readerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdminReaderResponse createReader(@Valid @RequestBody AdminCreateReaderRequest request) {
        return userService.createReader(request);
    }

    @PatchMapping("/{readerId}")
    public AdminReaderResponse updateReader(
            @PathVariable Long readerId,
            @Valid @RequestBody AdminUpdateReaderRequest request
    ) {
        return userService.updateReader(readerId, request);
    }

    @DeleteMapping("/{readerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateReader(@PathVariable Long readerId) {
        userService.deactivateReader(readerId);
    }
}
