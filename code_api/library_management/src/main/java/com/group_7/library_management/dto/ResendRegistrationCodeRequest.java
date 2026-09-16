package com.group_7.library_management.dto;

import jakarta.validation.constraints.NotBlank;

public record ResendRegistrationCodeRequest(
        @NotBlank(message = "Mã đăng ký không được để trống")
        String registrationId
) {
}
