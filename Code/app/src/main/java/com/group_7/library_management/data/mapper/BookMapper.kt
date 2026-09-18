package com.group_7.library_management.data.mapper

import com.group_7.library_management.data.local.entity.BookEntity
import com.group_7.library_management.data.remote.dto.BookResponseDto
import com.group_7.library_management.data.remote.dto.PopularBookResponseDto
import com.group_7.library_management.models.Book
import java.time.Instant

fun BookEntity.toBookModel(): Book {
    return Book(
        id=id,
        isbn=isbn,
        title=title,
        author=author,
        category=category,
        publisher=publisher,
        publishYear=publishYear,
        totalCopies=totalCopies,
        coverImageUrl = coverImageUrl,
        description=description,
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
        isbn = null,
        title = title,
        author = author,
        category = category,
        publisher = null,
        publishYear = null,
        totalCopies = 0,
        coverImageUrl = cover,
        description = null,
        borrowFee = borrowFee,
        availableCopies = availableQuantity.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
        rating = rating,
        createdAt = runCatching { Instant.parse(createdAt).toEpochMilli() }.getOrDefault(0L),
        ratingCount = ratingCount,
        popularityScore = popularityScore
    )
}

fun PopularBookResponseDto.toEntity(): BookEntity = book.toEntity(popularityScore)

fun BookResponseDto.toBookModel(popularityScore: Long = 0): Book {
    return Book(
        id = id.toString(),
        isbn = isbn,
        title = title,
        author = author,
        category = category,
        publisher = publisher,
        publishYear = publishYear,
        totalCopies = quantity.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
        coverImageUrl = cover,
        description = description,
        borrowFee = borrowFee,
        availableCopies = availableQuantity.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
        rating = rating,
        createdAt = runCatching { Instant.parse(createdAt).toEpochMilli() }.getOrDefault(0L),
        ratingCount = ratingCount,
        popularityScore = popularityScore
    )
}

fun PopularBookResponseDto.toBookModel(): Book = book.toBookModel(popularityScore)
