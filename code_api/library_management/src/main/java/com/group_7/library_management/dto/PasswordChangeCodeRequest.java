package com.group_7.library_management.dto;

import com.group_7.library_management.entity.VerificationChannel;
import jakarta.validation.constraints.NotNull;

public record PasswordChangeCodeRequest(
        @NotNull(message = "Phương thức nhận mã không được để trống")
        VerificationChannel channel
) {
}
