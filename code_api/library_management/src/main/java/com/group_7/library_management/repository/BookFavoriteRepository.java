package com.group_7.library_management.repository;

import com.group_7.library_management.entity.BookFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookFavoriteRepository extends JpaRepository<BookFavorite, Long> {
    boolean existsByUserIdAndBookId(Long userId, Long bookId);
    void deleteByUserIdAndBookId(Long userId, Long bookId);
}
