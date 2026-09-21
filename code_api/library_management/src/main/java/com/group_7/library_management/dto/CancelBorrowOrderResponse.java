package com.group_7.library_management.dto;

public record CancelBorrowOrderResponse(
        BorrowOrderResponse order,
        int remainingCancellations
) {
}
