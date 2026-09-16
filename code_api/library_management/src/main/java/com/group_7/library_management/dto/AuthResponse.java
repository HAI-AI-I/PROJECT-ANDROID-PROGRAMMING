package com.group_7.library_management.dto;

public record AuthResponse(
        String accessToken,
        String tokenType,
        UserResponse user
) {
}
