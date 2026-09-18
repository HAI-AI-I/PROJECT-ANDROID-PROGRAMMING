package com.group_7.library_management.repository;

import com.group_7.library_management.entity.BookReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookReviewRepository extends JpaRepository<BookReview, Long> {

    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    @EntityGraph(attributePaths = "user")
    Optional<BookReview> findByUserIdAndBookId(Long userId, Long bookId);

    @EntityGraph(attributePaths = "user")
    Page<BookReview> findByBookId(Long bookId, Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Optional<BookReview> findByIdAndBookId(Long id, Long bookId);

    @Query("""
            select avg(r.rating) as averageRating, count(r) as ratingCount
            from BookReview r
            where r.book.id = :bookId
            """)
    BookRatingStatistics calculateStatistics(@Param("bookId") Long bookId);
}
