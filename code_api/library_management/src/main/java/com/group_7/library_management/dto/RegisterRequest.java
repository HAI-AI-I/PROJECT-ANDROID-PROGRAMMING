package com.group_7.library_management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public record RegisterRequest(
        @NotBlank(message = "Họ và tên không được để trống")
        @Size(min = 2, max = 100, message = "Họ và tên phải có từ 2 đến 100 ký tự")
        String fullName,

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không hợp lệ")
        @Size(max = 150, message = "Email không được vượt quá 150 ký tự")
        String email,

        @NotBlank(message = "Số điện thoại không được để trống")
        @Pattern(regexp = "^\\d{10,15}$", message = "Số điện thoại phải có từ 10 đến 15 chữ số")
        String phone,

        @NotBlank(message = "Mật khẩu không được để trống")
        @Size(min = 6, max = 72, message = "Mật khẩu phải có từ 6 đến 72 ký tự")
        String password
) {
}
