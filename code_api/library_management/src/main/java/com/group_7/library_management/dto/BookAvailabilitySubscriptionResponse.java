package com.group_7.library_management.dto;

public record BookAvailabilitySubscriptionResponse(
        Long bookId,
        boolean subscribed
) {}
