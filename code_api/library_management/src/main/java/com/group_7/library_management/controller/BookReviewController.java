package com.group_7.library_management.controller;

import com.group_7.library_management.dto.BookReviewRequest;
import com.group_7.library_management.dto.BookReviewResponse;
import com.group_7.library_management.dto.PagedResponse;
import com.group_7.library_management.dto.MyBookReviewResponse;
import com.group_7.library_management.service.BookReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/books/{bookId}/reviews")
public class BookReviewController {

    private static final int MAX_PAGE_SIZE = 100;

    private final BookReviewService bookReviewService;

    public BookReviewController(BookReviewService bookReviewService) {
        this.bookReviewService = bookReviewService;
    }

    @GetMapping
    public PagedResponse<BookReviewResponse> getReviews(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "newest") String sort
    ) {
        int safePage = Math.max(page, 1) - 1;
        int safePageSize = Math.min(Math.max(pageSize, 1), MAX_PAGE_SIZE);
        return PagedResponse.from(bookReviewService.getReviews(
                bookId,
                PageRequest.of(safePage, safePageSize, resolveSort(sort))
        ));
    }

    @GetMapping("/mine")
    public MyBookReviewResponse getMyReview(
            Authentication authentication,
            @PathVariable Long bookId
    ) {
        return bookReviewService.getMyReview(
                (Long) authentication.getPrincipal(),
                bookId
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookReviewResponse createReview(
            Authentication authentication,
            @PathVariable Long bookId,
            @Valid @RequestBody BookReviewRequest request
    ) {
        return bookReviewService.createReview(
                (Long) authentication.getPrincipal(),
                bookId,
                request
        );
    }

    @PatchMapping("/{reviewId}")
    public BookReviewResponse updateReview(
            Authentication authentication,
            @PathVariable Long bookId,
            @PathVariable Long reviewId,
            @Valid @RequestBody BookReviewRequest request
    ) {
        return bookReviewService.updateReview(
                (Long) authentication.getPrincipal(),
                bookId,
                reviewId,
                request
        );
    }

    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(
            Authentication authentication,
            @PathVariable Long bookId,
            @PathVariable Long reviewId
    ) {
        bookReviewService.deleteReview(
                (Long) authentication.getPrincipal(),
                bookId,
                reviewId
        );
    }

    private Sort resolveSort(String sort) {
        return switch (sort.toLowerCase()) {
            case "oldest" -> Sort.by(Sort.Direction.ASC, "createdAt")
                    .and(Sort.by(Sort.Direction.ASC, "id"));
            case "rating_high" -> Sort.by(Sort.Direction.DESC, "rating")
                    .and(Sort.by(Sort.Direction.DESC, "createdAt"));
            case "rating_low" -> Sort.by(Sort.Direction.ASC, "rating")
                    .and(Sort.by(Sort.Direction.DESC, "createdAt"));
            default -> Sort.by(Sort.Direction.DESC, "createdAt")
                    .and(Sort.by(Sort.Direction.DESC, "id"));
        };
    }
}
