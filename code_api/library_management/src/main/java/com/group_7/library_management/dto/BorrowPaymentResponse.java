package com.group_7.library_management.dto;

import java.time.Instant;

public record BorrowPaymentResponse(
        Long orderId,
        String referenceCode,
        String paymentCode,
        long amount,
        long paidAmount,
        String paymentStatus,
        String bankCode,
        String accountNumber,
        String accountName,
        String qrUrl,
        Instant paidAt
) {
}
