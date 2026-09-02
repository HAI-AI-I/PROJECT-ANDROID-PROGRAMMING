package com.group_7.library_management.data.mapper

import com.group_7.library_management.data.local.entity.BookEntity
import com.group_7.library_management.models.Book
import kotlin.String

fun BookEntity.toBookModel(): Book {
    return Book(
        id=id,
        title=title,
        author=author,
        category=category,
        borrowFee=borrowFee,
        availableCopies = availableCopies,
        rating = rating,
        createdAt = createdAt,
        viewCount = viewCount
    )
}


fun Book.toEntity(
    createdAt: Long = System.currentTimeMillis(),
): BookEntity {
    return BookEntity(
        id=id,
        title=title,
        author=author,
        category=category,
        borrowFee=borrowFee,
        availableCopies = availableCopies,
        rating = rating,
        createdAt = createdAt,
        viewCount = viewCount
    )
}