package com.group_7.library_management.controller;

import com.group_7.library_management.dto.CreateSupportRequest;
import com.group_7.library_management.dto.SupportRequestResponse;
import com.group_7.library_management.service.SupportRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/support-requests")
public class SupportRequestController {

    private final SupportRequestService supportRequestService;

    public SupportRequestController(SupportRequestService supportRequestService) {
        this.supportRequestService = supportRequestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SupportRequestResponse create(
            Authentication authentication,
            @Valid @RequestBody CreateSupportRequest request
    ) {
        return supportRequestService.create((Long) authentication.getPrincipal(), request);
    }

    @GetMapping
    public List<SupportRequestResponse> getMine(Authentication authentication) {
        return supportRequestService.getMine((Long) authentication.getPrincipal());
    }

    @GetMapping("/{requestId}")
    public SupportRequestResponse getMineById(
            Authentication authentication,
            @PathVariable Long requestId
    ) {
        return supportRequestService.getMineById(
                (Long) authentication.getPrincipal(),
                requestId
        );
    }
}
