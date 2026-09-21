package com.group_7.library_management.repository;

import com.group_7.library_management.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import com.group_7.library_management.entity.BorrowStatus;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    long countByUserId(Long userId);

    long countByUserIdAndStatus(Long userId, BorrowStatus status);

    long countByUserIdAndStatusAndCancelledAtGreaterThanEqualAndCancelledAtLessThan(
            Long userId,
            BorrowStatus status,
            Instant from,
            Instant to
    );

    long countByUserIdAndStatusAndDueAtAfter(
            Long userId,
            BorrowStatus status,
            Instant dueAt
    );

    long countByUserIdAndStatusAndDueAtBetween(
            Long userId,
            BorrowStatus status,
            Instant from,
            Instant to
    );

    @Query("""
            select count(b)
            from BorrowRecord b
            where b.user.id = :userId
              and (
                    b.status = com.group_7.library_management.entity.BorrowStatus.OVERDUE
                    or (b.status = com.group_7.library_management.entity.BorrowStatus.BORROWED
                        and b.dueAt < :now)
              )
            """)
    long countOverdueByUserId(
            @Param("userId") Long userId,
            @Param("now") Instant now
    );

    boolean existsByUserIdAndBookCopyBookIdAndStatusIn(
            Long userId,
            Long bookId,
            Collection<BorrowStatus> statuses
    );

    @EntityGraph(attributePaths = {"user", "bookCopy", "bookCopy.book", "bookCopy.book.authors"})
    Optional<BorrowRecord> findByIdAndUserId(Long id, Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"user", "bookCopy", "bookCopy.book", "bookCopy.book.authors"})
    @Query("select b from BorrowRecord b where b.id = :orderId and b.user.id = :userId")
    Optional<BorrowRecord> findForCancellation(
            @Param("orderId") Long orderId,
            @Param("userId") Long userId
    );

    @EntityGraph(attributePaths = {"user", "bookCopy", "bookCopy.book", "bookCopy.book.authors"})
    Optional<BorrowRecord> findFirstByUserIdAndBookCopyBookIdAndStatusInOrderByCreatedAtDesc(
            Long userId,
            Long bookId,
            Collection<BorrowStatus> statuses
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"user", "bookCopy", "bookCopy.book", "bookCopy.book.authors"})
    Optional<BorrowRecord> findByReferenceCode(String referenceCode);

    @EntityGraph(attributePaths = {"bookCopy", "bookCopy.book"})
    Optional<BorrowRecord> findByReferenceCodeAndUserId(String referenceCode, Long userId);

    @EntityGraph(attributePaths = {"user", "bookCopy", "bookCopy.book", "bookCopy.book.authors"})
    List<BorrowRecord> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
