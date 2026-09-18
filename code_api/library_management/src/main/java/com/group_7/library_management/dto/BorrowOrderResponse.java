package com.group_7.library_management.dto;

import com.group_7.library_management.entity.BorrowRecord;
import com.group_7.library_management.entity.BorrowStatus;

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
        long totalAmount
) {
    public static BorrowOrderResponse from(BorrowRecord order) {
        var book = order.getBookCopy().getBook();
        String authors = book.getAuthors().stream()
                .map(author -> author.getName())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .reduce((left, right) -> left + ", " + right)
                .orElse("");
        return new BorrowOrderResponse(
                order.getId(),
                order.getReferenceCode(),
                order.getStatus(),
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
                order.getTotalAmount()
        );
    }
}
