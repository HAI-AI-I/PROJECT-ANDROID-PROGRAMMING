package com.group_7.library_management.controller;

import com.group_7.library_management.dto.BookResponse;
import com.group_7.library_management.dto.CreateBookRequest;
import com.group_7.library_management.dto.PagedResponse;
import com.group_7.library_management.dto.UpdateBookRequest;
import com.group_7.library_management.dto.PopularBookResponse;
import com.group_7.library_management.service.BookService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "newest") String sort
    ) {
        int safePage = Math.max(page, 1) - 1;
        int safePageSize = normalizePageSize(pageSize);
        Pageable pageable = PageRequest.of(safePage, safePageSize, resolveSort(sort));
        return PagedResponse.from(bookService.searchBooks(search, category, pageable));
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

    private Sort resolveSort(String sort) {
        return switch (sort.toLowerCase()) {
            case "title" -> Sort.by(Sort.Direction.ASC, "title");
            case "rating" -> Sort.by(Sort.Direction.DESC, "averageRating");
            default -> Sort.by(Sort.Direction.DESC, "createdAt")
                    .and(Sort.by(Sort.Direction.DESC, "id"));
        };
    }

    private int normalizePageSize(int requestedSize) {
        return Math.min(Math.max(requestedSize, 1), MAX_PAGE_SIZE);
    }

}
