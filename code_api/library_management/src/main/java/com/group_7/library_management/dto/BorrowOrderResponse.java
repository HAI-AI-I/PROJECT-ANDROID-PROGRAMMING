package com.group_7.library_management.dto;

import com.group_7.library_management.entity.BorrowRecord;
import com.group_7.library_management.entity.BorrowStatus;
import com.group_7.library_management.entity.PaymentMethod;

import java.time.Instant;

public record BorrowOrderResponse(
        Long id,
        String referenceCode,
        BorrowStatus status,
        Long bookId,
        String bookTitle,
        String bookAuthor,
        String coverImageUrl,
        String copyBarcode,
        Long borrowerId,
        String borrowerName,
        String pickupLocation,
        int borrowDays,
        Instant requestedAt,
        Instant borrowedAt,
        Instant dueAt,
        Instant returnedAt,
        long borrowFee,
        long depositAmount,
        long totalAmount,
        long paidAmount,
        String paymentCode,
        String paymentStatus,
        PaymentMethod paymentMethod,
        Instant paidAt,
        boolean depositRefunded,
        long remainingRefundAmount
) {
    public static BorrowOrderResponse from(BorrowRecord order) {
        var book = order.getBookCopy().getBook();
        BorrowStatus effectiveStatus = order.getStatus() == BorrowStatus.BORROWED
                && order.getDueAt() != null
                && order.getDueAt().isBefore(Instant.now())
                ? BorrowStatus.OVERDUE
                : order.getStatus();
        String authors = book.getAuthors().stream()
                .map(author -> author.getName())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .reduce((left, right) -> left + ", " + right)
                .orElse("");
        long paidAmount = order.getPaidAmount();
        if (paidAmount == 0L
                && effectiveStatus != BorrowStatus.PENDING_PAYMENT
                && effectiveStatus != BorrowStatus.REQUESTED
                && effectiveStatus != BorrowStatus.CANCELLED) {
            paidAmount = order.getTotalAmount();
        }
        boolean depositRefunded = order.getDepositRefundedAt() != null;
        long remainingRefundAmount = effectiveStatus == BorrowStatus.RETURNED && !depositRefunded
                ? order.getDepositAmount()
                : 0L;
        return new BorrowOrderResponse(
                order.getId(),
                order.getReferenceCode(),
                effectiveStatus,
                book.getId(),
                book.getTitle(),
                authors,
                book.getCoverImageUrl(),
                order.getBookCopy().getBarcode(),
                order.getUser().getId(),
                order.getUser().getFullName(),
                order.getPickupLocation(),
                order.getBorrowDays(),
                order.getCreatedAt(),
                order.getBorrowedAt(),
                order.getDueAt(),
                order.getReturnedAt(),
                order.getBorrowFee(),
                order.getDepositAmount(),
                order.getTotalAmount(),
                paidAmount,
                order.getPaymentCode(),
                paidAmount >= order.getTotalAmount() ? "PAID" : "UNPAID",
                order.getPaymentMethod(),
                order.getPaidAt(),
                depositRefunded,
                remainingRefundAmount
        );
    }
}
