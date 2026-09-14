package com.group_7.library_management.dto;

public record RegistrationCodeResponse(
        String registrationId,
        String destination,
        long expiresInSeconds
) {
}
