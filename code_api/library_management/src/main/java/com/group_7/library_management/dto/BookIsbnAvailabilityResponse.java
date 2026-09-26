package com.group_7.library_management.dto;

public record BookIsbnAvailabilityResponse(
        String isbn,
        boolean available
) {
}
