package com.group_7.library_management.controller;

import com.group_7.library_management.dto.HomeSummaryResponse;
import com.group_7.library_management.service.HomeSummaryService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/home")
public class HomeController {
    private final HomeSummaryService homeSummaryService;

    public HomeController(HomeSummaryService homeSummaryService) {
        this.homeSummaryService = homeSummaryService;
    }

    @GetMapping("/summary")
    public HomeSummaryResponse getSummary(Authentication authentication) {
        return homeSummaryService.getSummary((Long) authentication.getPrincipal());
    }
}
