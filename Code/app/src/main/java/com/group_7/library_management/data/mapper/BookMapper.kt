package com.group_7.library_management.data.mapper

import com.group_7.library_management.data.local.entity.BookEntity
import com.group_7.library_management.data.remote.dto.BookResponseDto
import com.group_7.library_management.data.remote.dto.PopularBookResponseDto
import com.group_7.library_management.models.Book
import java.time.Instant

fun BookEntity.toBookModel(): Book {
    return Book(
        id=id,
        title=title,
        author=author,
        category=category,
        coverImageUrl = coverImageUrl,
        borrowFee=borrowFee,
        availableCopies = availableCopies,
        rating = rating,
        createdAt = createdAt,
        ratingCount = ratingCount,
        popularityScore = popularityScore
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
        coverImageUrl = coverImageUrl,
        borrowFee=borrowFee,
        availableCopies = availableCopies,
        rating = rating,
        createdAt = createdAt,
        ratingCount = ratingCount,
        popularityScore = popularityScore
    )
}

fun BookResponseDto.toEntity(popularityScore: Long = 0): BookEntity {
    return BookEntity(
        id = id.toString(),
        title = title,
        author = author,
        category = category,
        coverImageUrl = cover,
        borrowFee = borrowFee,
        availableCopies = availableQuantity.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
        rating = rating,
        createdAt = runCatching { Instant.parse(createdAt).toEpochMilli() }.getOrDefault(0L),
        ratingCount = ratingCount,
        popularityScore = popularityScore
    )
}

fun PopularBookResponseDto.toEntity(): BookEntity = book.toEntity(popularityScore)
