package com.group_7.library_management.controller;

import com.group_7.library_management.dto.BookFavoriteStatusResponse;
import com.group_7.library_management.dto.BookResponse;
import com.group_7.library_management.service.BookFavoriteService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favorites")
public class BookFavoriteController {
    private final BookFavoriteService favoriteService;

    public BookFavoriteController(BookFavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public List<BookResponse> getFavorites(Authentication authentication) {
        return favoriteService.getFavorites((Long) authentication.getPrincipal());
    }

    @GetMapping("/books/{bookId}")
    public BookFavoriteStatusResponse getStatus(
            Authentication authentication,
            @PathVariable Long bookId
    ) {
        return favoriteService.getStatus((Long) authentication.getPrincipal(), bookId);
    }

    @PutMapping("/books/{bookId}")
    public BookFavoriteStatusResponse addFavorite(
            Authentication authentication,
            @PathVariable Long bookId
    ) {
        return favoriteService.addFavorite((Long) authentication.getPrincipal(), bookId);
    }

    @DeleteMapping("/books/{bookId}")
    public BookFavoriteStatusResponse removeFavorite(
            Authentication authentication,
            @PathVariable Long bookId
    ) {
        return favoriteService.removeFavorite((Long) authentication.getPrincipal(), bookId);
    }
}
