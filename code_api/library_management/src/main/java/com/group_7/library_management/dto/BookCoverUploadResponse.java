package com.group_7.library_management.dto;

public record BookCoverUploadResponse(
        String fileName,
        String path,
        String url
) {
}
