package com.group_7.library_management.controller;

import com.group_7.library_management.dto.AdminDashboardResponse;
import com.group_7.library_management.dto.AdminStatisticsResponse;
import com.group_7.library_management.service.AdminAnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/analytics")
public class AdminAnalyticsController {

    private final AdminAnalyticsService adminAnalyticsService;

    public AdminAnalyticsController(AdminAnalyticsService adminAnalyticsService) {
        this.adminAnalyticsService = adminAnalyticsService;
    }

    @GetMapping("/dashboard")
    public AdminDashboardResponse getDashboard() {
        return adminAnalyticsService.getDashboard();
    }

    @GetMapping("/statistics")
    public AdminStatisticsResponse getStatistics() {
        return adminAnalyticsService.getStatistics();
    }
}
