package com.group_7.library_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BiometricLoginRequest(
        @NotBlank(message = "Credential sinh trắc học không được để trống")
        @Size(max = 200, message = "Credential sinh trắc học không hợp lệ")
        String credential
) {
}
