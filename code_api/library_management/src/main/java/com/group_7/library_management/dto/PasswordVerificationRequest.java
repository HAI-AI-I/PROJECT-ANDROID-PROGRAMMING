package com.group_7.library_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PasswordVerificationRequest(
        @NotBlank(message = "Mã yêu cầu không được để trống")
        String requestId,

        @Pattern(regexp = "^\\d{6}$", message = "Mã xác nhận phải gồm 6 chữ số")
        String code
) {
}
