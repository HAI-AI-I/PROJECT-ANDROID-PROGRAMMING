package com.group_7.library_management.repository;

import com.group_7.library_management.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.util.Collection;
import java.util.Optional;
import com.group_7.library_management.entity.BorrowStatus;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    boolean existsByUserIdAndBookCopyBookIdAndStatusIn(
            Long userId,
            Long bookId,
            Collection<BorrowStatus> statuses
    );

    @EntityGraph(attributePaths = {"user", "bookCopy", "bookCopy.book", "bookCopy.book.authors"})
    Optional<BorrowRecord> findByIdAndUserId(Long id, Long userId);

    @EntityGraph(attributePaths = {"user", "bookCopy", "bookCopy.book", "bookCopy.book.authors"})
    Optional<BorrowRecord> findFirstByUserIdAndBookCopyBookIdAndStatusInOrderByCreatedAtDesc(
            Long userId,
            Long bookId,
            Collection<BorrowStatus> statuses
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"user", "bookCopy", "bookCopy.book", "bookCopy.book.authors"})
    Optional<BorrowRecord> findByReferenceCode(String referenceCode);
}
