package com.group_7.library_management.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordResendRequest(
        @NotBlank(message = "Mã yêu cầu không được để trống")
        String requestId
) {
}
