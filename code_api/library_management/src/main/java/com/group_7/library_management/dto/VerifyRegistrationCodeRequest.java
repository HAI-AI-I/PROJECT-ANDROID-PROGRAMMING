package com.group_7.library_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyRegistrationCodeRequest(
        @NotBlank(message = "Mã đăng ký không được để trống")
        String registrationId,

        @NotBlank(message = "Mã xác nhận không được để trống")
        @Pattern(regexp = "^\\d{6}$", message = "Mã xác nhận phải gồm 6 chữ số")
        String code
) {
}
