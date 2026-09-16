package com.group_7.library_management.repository;

import com.group_7.library_management.entity.BookCopy;
import com.group_7.library_management.entity.BookCopyStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {

    boolean existsByBarcode(String barcode);

    Optional<BookCopy> findByBarcode(String barcode);

    List<BookCopy> findAllByBookIdOrderByIdAsc(Long bookId);

    long countByBookId(Long bookId);

    long countByBookIdAndStatus(Long bookId, BookCopyStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BookCopy> findFirstByBookIdAndStatusOrderByIdAsc(
            Long bookId,
            BookCopyStatus status
    );
}
