package com.group_7.library_management.controller;

import com.group_7.library_management.dto.AdminSupportReplyRequest;
import com.group_7.library_management.dto.PagedResponse;
import com.group_7.library_management.dto.SupportRequestResponse;
import com.group_7.library_management.service.SupportRequestService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/support-requests")
public class AdminSupportRequestController {

    private final SupportRequestService supportRequestService;

    public AdminSupportRequestController(SupportRequestService supportRequestService) {
        this.supportRequestService = supportRequestService;
    }

    @GetMapping
    public PagedResponse<SupportRequestResponse> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "100") int pageSize
    ) {
        int safePage = Math.max(page, 1) - 1;
        int safePageSize = Math.min(Math.max(pageSize, 1), 100);
        return PagedResponse.from(supportRequestService.getAll(
                PageRequest.of(safePage, safePageSize)
        ));
    }

    @GetMapping("/{requestId}")
    public SupportRequestResponse getById(@PathVariable Long requestId) {
        return supportRequestService.getById(requestId);
    }

    @PatchMapping("/{requestId}/reply")
    public SupportRequestResponse reply(
            @PathVariable Long requestId,
            @Valid @RequestBody AdminSupportReplyRequest request
    ) {
        return supportRequestService.reply(requestId, request);
    }

    @PatchMapping("/{requestId}/resolve")
    public SupportRequestResponse resolve(@PathVariable Long requestId) {
        return supportRequestService.resolve(requestId);
    }

    @PatchMapping("/{requestId}/close")
    public SupportRequestResponse close(@PathVariable Long requestId) {
        return supportRequestService.close(requestId);
    }
}
