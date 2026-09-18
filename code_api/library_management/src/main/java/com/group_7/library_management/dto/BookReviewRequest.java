package com.group_7.library_management.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookReviewRequest(
        @NotNull(message = "Số sao không được để trống")
        @Min(value = 1, message = "Số sao phải từ 1 đến 5")
        @Max(value = 5, message = "Số sao phải từ 1 đến 5")
        Integer rating,

        @Size(max = 2000, message = "Bình luận không được dài quá 2000 ký tự")
        String comment
) {
}
