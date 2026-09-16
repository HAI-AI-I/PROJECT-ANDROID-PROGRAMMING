package com.group_7.library_management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record PublisherRequest(
        Long id,

        @Size(max = 150, message = "Tên nhà xuất bản không được dài quá 150 ký tự")
        String name,

        @Size(max = 500, message = "Địa chỉ nhà xuất bản không được dài quá 500 ký tự")
        String address,

        @Email(message = "Email nhà xuất bản không hợp lệ")
        @Size(max = 150, message = "Email nhà xuất bản không được dài quá 150 ký tự")
        String email,

        @Size(max = 20, message = "Số điện thoại nhà xuất bản không được dài quá 20 ký tự")
        String phone
) {
}
