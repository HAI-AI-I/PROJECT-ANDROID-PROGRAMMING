package com.group_7.library_management.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateBorrowOrderRequest(
        @NotNull(message = "Mã sách không được để trống")
        Long bookId,

        @NotNull(message = "Thời hạn mượn không được để trống")
        @Min(value = 1, message = "Thời hạn mượn tối thiểu là 1 ngày")
        @Max(value = 30, message = "Thời hạn mượn tối đa là 30 ngày")
        Integer borrowDays
) {
}
