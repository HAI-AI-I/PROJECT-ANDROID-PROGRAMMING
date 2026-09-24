package com.group_7.library_management.dto;

import com.group_7.library_management.entity.VerificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PasswordCodeRequest(
        @NotBlank(message = "Email hoặc số điện thoại không được để trống")
        String identifier,

        @NotNull(message = "Phương thức nhận mã không được để trống")
        VerificationChannel channel
) {
}
