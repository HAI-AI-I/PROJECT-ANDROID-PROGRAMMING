package com.group_7.library_management.controller;

import com.group_7.library_management.dto.BookAvailabilitySubscriptionResponse;
import com.group_7.library_management.service.BookAvailabilitySubscriptionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notification-subscriptions/books")
public class BookAvailabilitySubscriptionController {
    private final BookAvailabilitySubscriptionService subscriptionService;

    public BookAvailabilitySubscriptionController(
            BookAvailabilitySubscriptionService subscriptionService
    ) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/{bookId}")
    public BookAvailabilitySubscriptionResponse getStatus(
            Authentication authentication,
            @PathVariable Long bookId
    ) {
        return subscriptionService.getStatus((Long) authentication.getPrincipal(), bookId);
    }

    @PutMapping("/{bookId}")
    public BookAvailabilitySubscriptionResponse subscribe(
            Authentication authentication,
            @PathVariable Long bookId
    ) {
        return subscriptionService.subscribe((Long) authentication.getPrincipal(), bookId);
    }

    @DeleteMapping("/{bookId}")
    public BookAvailabilitySubscriptionResponse unsubscribe(
            Authentication authentication,
            @PathVariable Long bookId
    ) {
        return subscriptionService.unsubscribe((Long) authentication.getPrincipal(), bookId);
    }
}
