package com.group_7.library_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Email hoặc số điện thoại không được để trống")
        @Size(max = 150, message = "Email hoặc số điện thoại không được vượt quá 150 ký tự")
        String identifier,

        @NotBlank(message = "Mật khẩu không được để trống")
        @Size(max = 72, message = "Mật khẩu không được vượt quá 72 ký tự")
        String password
) {
}
