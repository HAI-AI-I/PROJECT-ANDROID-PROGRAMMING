package com.group_7.library_management.dto;

import com.group_7.library_management.entity.Book;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.stream.Collectors;
import java.util.List;

public record BookResponse(
        Long id,
        String isbn,
        String title,
        String author,
        String category,
        String publisher,
        Integer publishYear,
        long quantity,
        long availableQuantity,
        String cover,
        String description,
        long borrowFee,
        BigDecimal rating,
        int ratingCount,
        boolean active,
        Instant createdAt,
        Instant updatedAt,
        List<AuthorResponse> authorDetails,
        PublisherResponse publisherDetails
) {
    public static BookResponse from(Book book, long quantity, long availableQuantity) {
        String authorNames = book.getAuthors().stream()
                .map(author -> author.getName())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.joining(", "));

        return new BookResponse(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                authorNames,
                book.getCategory().getName(),
                book.getPublisher() == null ? null : book.getPublisher().getName(),
                book.getPublishYear(),
                quantity,
                availableQuantity,
                book.getCoverImageUrl(),
                book.getDescription(),
                book.getBorrowFee(),
                book.getAverageRating(),
                book.getRatingCount(),
                book.isActive(),
                book.getCreatedAt(),
                book.getUpdatedAt(),
                book.getAuthors().stream()
                        .sorted((left, right) -> left.getName().compareToIgnoreCase(right.getName()))
                        .map(AuthorResponse::from)
                        .toList(),
                PublisherResponse.from(book.getPublisher())
        );
    }
}
