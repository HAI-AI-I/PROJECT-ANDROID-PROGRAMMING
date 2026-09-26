package com.group_7.library_management.controller;

import com.group_7.library_management.dto.AdminBroadcastNotificationRequest;
import com.group_7.library_management.dto.AdminBroadcastNotificationResponse;
import com.group_7.library_management.service.AdminNotificationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/notifications")
public class AdminNotificationController {

    private final AdminNotificationService adminNotificationService;

    public AdminNotificationController(AdminNotificationService adminNotificationService) {
        this.adminNotificationService = adminNotificationService;
    }

    @PostMapping("/broadcast")
    public AdminBroadcastNotificationResponse broadcast(
            @Valid @RequestBody AdminBroadcastNotificationRequest request
    ) {
        return adminNotificationService.broadcast(request);
    }
}
