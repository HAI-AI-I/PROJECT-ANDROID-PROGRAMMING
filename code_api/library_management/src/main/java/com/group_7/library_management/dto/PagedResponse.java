package com.group_7.library_management.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PagedResponse<T>(
        List<T> items,
        long total,
        int page,
        int pageSize,
        int totalPages
) {
    public static <T> PagedResponse<T> from(Page<T> result) {
        return new PagedResponse<>(
                result.getContent(),
                result.getTotalElements(),
                result.getNumber() + 1,
                result.getSize(),
                result.getTotalPages()
        );
    }
}
