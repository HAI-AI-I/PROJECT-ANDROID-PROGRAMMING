package com.group_7.library_management.repository;

import com.group_7.library_management.entity.BookFavorite;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookFavoriteRepository extends JpaRepository<BookFavorite, Long> {
    long countByUserId(Long userId);

    boolean existsByUserIdAndBookId(Long userId, Long bookId);
    Optional<BookFavorite> findByUserIdAndBookId(Long userId, Long bookId);

    @EntityGraph(attributePaths = {"book", "book.authors", "book.category", "book.publisher"})
    List<BookFavorite> findAllByUserIdAndBookActiveTrueOrderByCreatedAtDesc(Long userId);
}
