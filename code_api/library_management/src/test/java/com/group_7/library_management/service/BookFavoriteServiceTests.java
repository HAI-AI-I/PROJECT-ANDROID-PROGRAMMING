package com.group_7.library_management.service;

import com.group_7.library_management.entity.Book;
import com.group_7.library_management.entity.BookFavorite;
import com.group_7.library_management.entity.Category;
import com.group_7.library_management.entity.User;
import com.group_7.library_management.repository.BookCopyRepository;
import com.group_7.library_management.repository.BookFavoriteRepository;
import com.group_7.library_management.repository.BookRepository;
import com.group_7.library_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookFavoriteServiceTests {
    @Mock BookFavoriteRepository favoriteRepository;
    @Mock UserRepository userRepository;
    @Mock BookRepository bookRepository;
    @Mock BookCopyRepository bookCopyRepository;

    private BookFavoriteService service;

    @BeforeEach
    void setUp() {
        service = new BookFavoriteService(
                favoriteRepository,
                userRepository,
                bookRepository,
                bookCopyRepository
        );
    }

    @Test
    void addFavoriteCreatesRelationWhenItDoesNotExist() {
        long userId = 22L;
        long bookId = 7L;
        User user = new User("Nguyễn Văn An", "an@example.com", "0900000000", "hash");
        Book book = new Book("Clean Code", new Category("Lập trình", "lap-trinh"));

        when(favoriteRepository.existsByUserIdAndBookId(userId, bookId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookRepository.findByIdAndActiveTrue(bookId)).thenReturn(Optional.of(book));

        var response = service.addFavorite(userId, bookId);

        assertThat(response.favorite()).isTrue();
        assertThat(response.bookId()).isEqualTo(bookId);
        verify(favoriteRepository).save(any(BookFavorite.class));
    }

    @Test
    void addFavoriteIsIdempotentWhenRelationAlreadyExists() {
        when(favoriteRepository.existsByUserIdAndBookId(22L, 7L)).thenReturn(true);

        var response = service.addFavorite(22L, 7L);

        assertThat(response.favorite()).isTrue();
        verify(favoriteRepository, never()).save(any(BookFavorite.class));
    }

    @Test
    void removeFavoriteDeletesExistingRelation() {
        BookFavorite favorite = new BookFavorite(
                new User("Nguyễn Văn An", "an@example.com", "0900000000", "hash"),
                new Book("Clean Code", new Category("Lập trình", "lap-trinh"))
        );
        when(favoriteRepository.findByUserIdAndBookId(22L, 7L))
                .thenReturn(Optional.of(favorite));

        var response = service.removeFavorite(22L, 7L);

        assertThat(response.favorite()).isFalse();
        verify(favoriteRepository).delete(favorite);
    }
}
