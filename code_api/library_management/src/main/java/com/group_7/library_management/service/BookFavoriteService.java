package com.group_7.library_management.service;

import com.group_7.library_management.dto.BookFavoriteStatusResponse;
import com.group_7.library_management.dto.BookResponse;
import com.group_7.library_management.entity.Book;
import com.group_7.library_management.entity.BookCopyStatus;
import com.group_7.library_management.entity.BookFavorite;
import com.group_7.library_management.entity.User;
import com.group_7.library_management.exception.ResourceNotFoundException;
import com.group_7.library_management.repository.BookCopyRepository;
import com.group_7.library_management.repository.BookFavoriteRepository;
import com.group_7.library_management.repository.BookRepository;
import com.group_7.library_management.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookFavoriteService {
    private final BookFavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;

    public BookFavoriteService(
            BookFavoriteRepository favoriteRepository,
            UserRepository userRepository,
            BookRepository bookRepository,
            BookCopyRepository bookCopyRepository
    ) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.bookCopyRepository = bookCopyRepository;
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getFavorites(Long userId) {
        return favoriteRepository.findAllByUserIdAndBookActiveTrueOrderByCreatedAtDesc(userId)
                .stream()
                .map(BookFavorite::getBook)
                .map(this::toBookResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookFavoriteStatusResponse getStatus(Long userId, Long bookId) {
        requireBook(bookId);
        return response(bookId, favoriteRepository.existsByUserIdAndBookId(userId, bookId));
    }

    @Transactional
    public BookFavoriteStatusResponse addFavorite(Long userId, Long bookId) {
        if (!favoriteRepository.existsByUserIdAndBookId(userId, bookId)) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
            favoriteRepository.save(new BookFavorite(user, requireBook(bookId)));
        }
        return response(bookId, true);
    }

    @Transactional
    public BookFavoriteStatusResponse removeFavorite(Long userId, Long bookId) {
        favoriteRepository.findByUserIdAndBookId(userId, bookId)
                .ifPresent(favoriteRepository::delete);
        return response(bookId, false);
    }

    private Book requireBook(Long bookId) {
        return bookRepository.findByIdAndActiveTrue(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách"));
    }

    private BookResponse toBookResponse(Book book) {
        long quantity = bookCopyRepository.countByBookId(book.getId());
        long available = bookCopyRepository.countByBookIdAndStatus(
                book.getId(),
                BookCopyStatus.AVAILABLE
        );
        return BookResponse.from(book, quantity, available);
    }

    private BookFavoriteStatusResponse response(Long bookId, boolean favorite) {
        return new BookFavoriteStatusResponse(bookId, favorite);
    }
}
