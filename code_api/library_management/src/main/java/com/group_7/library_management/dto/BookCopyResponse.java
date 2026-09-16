package com.group_7.library_management.dto;

import com.group_7.library_management.entity.BookCopy;
import com.group_7.library_management.entity.BookCopyStatus;

import java.time.Instant;
import java.time.LocalDate;

public record BookCopyResponse(
        Long id,
        String barcode,
        BookCopyStatus status,
        String shelfLocation,
        LocalDate acquiredDate,
        Instant createdAt,
        Instant updatedAt
) {
    public static BookCopyResponse from(BookCopy copy) {
        return new BookCopyResponse(
                copy.getId(),
                copy.getBarcode(),
                copy.getStatus(),
                copy.getShelfLocation(),
                copy.getAcquiredDate(),
                copy.getCreatedAt(),
                copy.getUpdatedAt()
        );
    }
}
