package com.group_7.library_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSupportRequest(
        Long bookId,

        @NotBlank(message = "Chủ đề không được để trống")
        @Size(max = 200, message = "Chủ đề không được vượt quá 200 ký tự")
        String subject,

        @NotBlank(message = "Nội dung không được để trống")
        @Size(max = 2000, message = "Nội dung không được vượt quá 2000 ký tự")
        String message
) {
}
