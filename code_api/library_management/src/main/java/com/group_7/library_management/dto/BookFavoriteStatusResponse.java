package com.group_7.library_management.dto;

public record BookFavoriteStatusResponse(
        Long bookId,
        boolean favorite
) {}
