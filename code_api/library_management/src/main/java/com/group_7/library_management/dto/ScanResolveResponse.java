package com.group_7.library_management.dto;

public record ScanResolveResponse(
        ScanTargetType type,
        Long targetId
) {
}
