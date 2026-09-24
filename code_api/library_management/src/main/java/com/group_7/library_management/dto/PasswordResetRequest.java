package com.group_7.library_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordResetRequest(
        @NotBlank(message = "Mã đặt lại mật khẩu không được để trống")
        String resetToken,

        @NotBlank(message = "Mật khẩu mới không được để trống")
        @Size(min = 6, max = 72, message = "Mật khẩu phải có từ 6 đến 72 ký tự")
        String newPassword
) {
}
