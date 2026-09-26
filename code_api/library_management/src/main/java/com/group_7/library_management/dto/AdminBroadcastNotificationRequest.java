package com.group_7.library_management.dto;

import com.group_7.library_management.entity.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdminBroadcastNotificationRequest(
        @NotBlank(message = "Tiêu đề không được để trống")
        @Size(max = 200, message = "Tiêu đề không được vượt quá 200 ký tự")
        String title,

        @NotBlank(message = "Nội dung không được để trống")
        @Size(max = 1000, message = "Nội dung không được vượt quá 1000 ký tự")
        String message,

        @NotNull(message = "Loại thông báo không được để trống")
        NotificationType type
) {
}
