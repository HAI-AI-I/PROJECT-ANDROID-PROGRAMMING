package com.group_7.library_management.controller;

import com.group_7.library_management.dto.AdminHistoryResponse;
import com.group_7.library_management.dto.PagedResponse;
import com.group_7.library_management.service.AdminHistoryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/history")
public class AdminHistoryController {

    private final AdminHistoryService adminHistoryService;

    public AdminHistoryController(AdminHistoryService adminHistoryService) {
        this.adminHistoryService = adminHistoryService;
    }

    @GetMapping
    public PagedResponse<AdminHistoryResponse> getHistory(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String action,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        int safePage = Math.max(page, 1) - 1;
        int safePageSize = Math.min(Math.max(pageSize, 1), 100);
        return PagedResponse.from(adminHistoryService.getHistory(
                search,
                action,
                PageRequest.of(safePage, safePageSize)
        ));
    }
}
