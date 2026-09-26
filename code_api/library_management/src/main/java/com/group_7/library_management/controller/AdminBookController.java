package com.group_7.library_management.controller;

import com.group_7.library_management.dto.BookIsbnAvailabilityResponse;
import com.group_7.library_management.service.BookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/books")
public class AdminBookController {

    private final BookService bookService;

    public AdminBookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/isbn-availability")
    public BookIsbnAvailabilityResponse checkIsbnAvailability(
            @RequestParam String isbn,
            @RequestParam(required = false) Long excludeBookId
    ) {
        String normalizedIsbn = isbn.trim();
        return new BookIsbnAvailabilityResponse(
                normalizedIsbn,
                bookService.isIsbnAvailable(normalizedIsbn, excludeBookId)
        );
    }
}
