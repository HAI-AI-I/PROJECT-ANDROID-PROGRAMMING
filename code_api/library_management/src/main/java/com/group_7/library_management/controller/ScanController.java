package com.group_7.library_management.controller;

import com.group_7.library_management.dto.ScanResolveResponse;
import com.group_7.library_management.service.ScanResolveService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/scan")
public class ScanController {
    private final ScanResolveService scanResolveService;

    public ScanController(ScanResolveService scanResolveService) {
        this.scanResolveService = scanResolveService;
    }

    @GetMapping("/resolve")
    public ScanResolveResponse resolve(
            Authentication authentication,
            @RequestParam String code
    ) {
        return scanResolveService.resolve((Long) authentication.getPrincipal(), code);
    }
}
