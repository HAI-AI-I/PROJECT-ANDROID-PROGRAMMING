package com.group_7.library_management.controller;

import com.group_7.library_management.dto.BookResponse;
import com.group_7.library_management.dto.BookDetailResponse;
import com.group_7.library_management.dto.CreateBookRequest;
import com.group_7.library_management.dto.PagedResponse;
import com.group_7.library_management.dto.UpdateBookRequest;
import com.group_7.library_management.dto.PopularBookResponse;
import com.group_7.library_management.service.BookService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private static final int MAX_PAGE_SIZE = 100;

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public PagedResponse<BookResponse> getBooks(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdAfter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "newest") String sort
    ) {
        int safePageSize = normalizePageSize(pageSize);
        return bookService.searchBooks(
                search, category, categories, status, minRating, minPrice, maxPrice, createdAfter,
                Math.max(page, 1), safePageSize, sort
        );
    }

    @GetMapping("/latest")
    public List<BookResponse> getLatestBooks(
            @RequestParam(defaultValue = "15") int limit
    ) {
        return bookService.getLatestBooks(normalizePageSize(limit));
    }

    @GetMapping("/popular")
    public List<PopularBookResponse> getPopularBooks(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return bookService.getPopularBooks(normalizePageSize(limit));
    }
    

    @GetMapping("/{id}")
    public BookResponse getBook(@PathVariable Long id) {
        return bookService.getBook(id);
    }

    @GetMapping("/{id}/detail")
    public BookDetailResponse getBookDetail(@PathVariable Long id) {
        return bookService.getBookDetail(id);
    }

    @GetMapping("/{id}/related")
    public List<PopularBookResponse> getRelatedBooks(
            @PathVariable Long id,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return bookService.getRelatedBooks(id, normalizePageSize(limit));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse createBook(@Valid @RequestBody CreateBookRequest request) {
        return bookService.createBook(request);
    }

    @PatchMapping("/{id}")
    public BookResponse updateBook(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookRequest request
    ) {
        return bookService.updateBook(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        bookService.deactivateBook(id);
    }

    private int normalizePageSize(int requestedSize) {
        return Math.min(Math.max(requestedSize, 1), MAX_PAGE_SIZE);
    }

}
