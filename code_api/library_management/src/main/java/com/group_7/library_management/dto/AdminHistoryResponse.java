package com.group_7.library_management.dto;

import java.time.Instant;

public record AdminHistoryResponse(
        String id,
        String action,
        String userName,
        String bookTitle,
        String referenceCode,
        String description,
        Instant occurredAt
) {
}
