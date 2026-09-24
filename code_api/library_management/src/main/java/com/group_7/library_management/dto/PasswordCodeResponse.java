package com.group_7.library_management.dto;

public record PasswordCodeResponse(
        String requestId,
        String destination,
        long expiresInSeconds
) {
}
