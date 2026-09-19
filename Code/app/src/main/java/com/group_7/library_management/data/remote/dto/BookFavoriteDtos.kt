package com.group_7.library_management.data.remote.dto

data class BookFavoriteStatusResponseDto(
    val bookId: Long,
    val favorite: Boolean
)
