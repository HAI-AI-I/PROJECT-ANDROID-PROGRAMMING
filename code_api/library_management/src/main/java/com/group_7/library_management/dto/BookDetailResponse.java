package com.group_7.library_management.dto;

import java.util.List;

public record BookDetailResponse(
        BookResponse book,
        List<BookCopyResponse> copies
) {
}
