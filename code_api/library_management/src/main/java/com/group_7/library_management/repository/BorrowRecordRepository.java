package com.group_7.library_management.repository;

import com.group_7.library_management.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import com.group_7.library_management.entity.BorrowStatus;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    @Query(
            value = """
                    select history.event_id as eventId,
                           history.action as action,
                           history.user_name as userName,
                           history.book_title as bookTitle,
                           history.reference_code as referenceCode,
                           history.event_at as eventAt
                    from (
                        select concat('REQUEST-', br.id) as event_id,
                               'request' as action,
                               u.full_name as user_name,
                               b.title as book_title,
                               br.reference_code as reference_code,
                               br.created_at as event_at
                        from borrow_records br
                        join users u on u.id = br.user_id
                        join book_copies bc on bc.id = br.book_copy_id
                        join books b on b.id = bc.book_id
                        union all
                        select concat('BORROW-', br.id), 'borrow', u.full_name, b.title,
                               br.reference_code, br.borrowed_at
                        from borrow_records br
                        join users u on u.id = br.user_id
                        join book_copies bc on bc.id = br.book_copy_id
                        join books b on b.id = bc.book_id
                        where br.borrowed_at is not null
                        union all
                        select concat('RETURN-', br.id), 'return', u.full_name, b.title,
                               br.reference_code, br.returned_at
                        from borrow_records br
                        join users u on u.id = br.user_id
                        join book_copies bc on bc.id = br.book_copy_id
                        join books b on b.id = bc.book_id
                        where br.returned_at is not null
                        union all
                        select concat('CANCEL-', br.id), 'cancel_request', u.full_name, b.title,
                               br.reference_code, br.cancelled_at
                        from borrow_records br
                        join users u on u.id = br.user_id
                        join book_copies bc on bc.id = br.book_copy_id
                        join books b on b.id = bc.book_id
                        where br.cancelled_at is not null
                    ) history
                    where (:search is null
                           or lower(history.user_name) like lower(concat('%', :search, '%'))
                           or lower(history.book_title) like lower(concat('%', :search, '%'))
                           or lower(history.reference_code) like lower(concat('%', :search, '%')))
                      and (:action is null or history.action = :action)
                    order by history.event_at desc
                    """,
            countQuery = """
                    select count(*) from (
                        select 'request' as action, u.full_name as user_name, b.title as book_title,
                               br.reference_code as reference_code
                        from borrow_records br
                        join users u on u.id = br.user_id
                        join book_copies bc on bc.id = br.book_copy_id
                        join books b on b.id = bc.book_id
                        union all
                        select 'borrow', u.full_name, b.title, br.reference_code
                        from borrow_records br
                        join users u on u.id = br.user_id
                        join book_copies bc on bc.id = br.book_copy_id
                        join books b on b.id = bc.book_id
                        where br.borrowed_at is not null
                        union all
                        select 'return', u.full_name, b.title, br.reference_code
                        from borrow_records br
                        join users u on u.id = br.user_id
                        join book_copies bc on bc.id = br.book_copy_id
                        join books b on b.id = bc.book_id
                        where br.returned_at is not null
                        union all
                        select 'cancel_request', u.full_name, b.title, br.reference_code
                        from borrow_records br
                        join users u on u.id = br.user_id
                        join book_copies bc on bc.id = br.book_copy_id
                        join books b on b.id = bc.book_id
                        where br.cancelled_at is not null
                    ) history
                    where (:search is null
                           or lower(history.user_name) like lower(concat('%', :search, '%'))
                           or lower(history.book_title) like lower(concat('%', :search, '%'))
                           or lower(history.reference_code) like lower(concat('%', :search, '%')))
                      and (:action is null or history.action = :action)
                    """,
            nativeQuery = true
    )
    Page<AdminHistoryProjection> searchAdminHistory(
            @Param("search") String search,
            @Param("action") String action,
            Pageable pageable
    );
    long countByUserId(Long userId);

    long countByUserIdAndStatus(Long userId, BorrowStatus status);

    long countByUserIdAndStatusIn(Long userId, Collection<BorrowStatus> statuses);

    long countByBorrowedAtIsNotNull();

    long countByReturnedAtIsNotNull();

    @Query("""
            select count(b) from BorrowRecord b
            where b.status = com.group_7.library_management.entity.BorrowStatus.BORROWED
              and (b.dueAt is null or b.dueAt >= :now)
            """)
    long countCurrentlyBorrowed(@Param("now") Instant now);

    @Query("""
            select count(b) from BorrowRecord b
            where b.status = com.group_7.library_management.entity.BorrowStatus.OVERDUE
               or (b.status = com.group_7.library_management.entity.BorrowStatus.BORROWED
                   and b.dueAt < :now)
            """)
    long countAllOverdue(@Param("now") Instant now);

    @Query(value = """
            select date_format(br.borrowed_at, '%Y-%m-%d') as period, count(*) as total
            from borrow_records br
            where br.borrowed_at >= :since
            group by date_format(br.borrowed_at, '%Y-%m-%d')
            order by period
            """, nativeQuery = true)
    List<PeriodCountProjection> countBorrowingsByDaySince(@Param("since") Instant since);

    @Query(value = """
            select date_format(br.borrowed_at, '%Y-%m') as period, count(*) as total
            from borrow_records br
            where br.borrowed_at >= :since
            group by date_format(br.borrowed_at, '%Y-%m')
            order by period
            """, nativeQuery = true)
    List<PeriodCountProjection> countBorrowingsByMonthSince(@Param("since") Instant since);

    @Query(value = """
            select date_format(br.returned_at, '%Y-%m') as period, count(*) as total
            from borrow_records br
            where br.returned_at >= :since
            group by date_format(br.returned_at, '%Y-%m')
            order by period
            """, nativeQuery = true)
    List<PeriodCountProjection> countReturnsByMonthSince(@Param("since") Instant since);

    @Query(value = """
            select b.title as label, count(*) as total
            from borrow_records br
            join book_copies bc on bc.id = br.book_copy_id
            join books b on b.id = bc.book_id
            where br.borrowed_at is not null
            group by b.id, b.title
            order by total desc, b.title
            """, nativeQuery = true)
    List<LabelCountProjection> findMostBorrowedBooks(Pageable pageable);

    @Query(value = """
            select u.full_name as label, count(*) as total
            from borrow_records br
            join users u on u.id = br.user_id
            where br.borrowed_at is not null and u.role = 'USER'
            group by u.id, u.full_name
            order by total desc, u.full_name
            """, nativeQuery = true)
    List<LabelCountProjection> findMostActiveReaders(Pageable pageable);

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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"user", "bookCopy", "bookCopy.book", "bookCopy.book.authors"})
    @Query("select b from BorrowRecord b where b.paymentCode = :paymentCode")
    Optional<BorrowRecord> findByPaymentCodeForUpdate(@Param("paymentCode") String paymentCode);

    @EntityGraph(attributePaths = {"bookCopy", "bookCopy.book"})
    Optional<BorrowRecord> findByReferenceCodeAndUserId(String referenceCode, Long userId);

    @EntityGraph(attributePaths = {"user", "bookCopy", "bookCopy.book", "bookCopy.book.authors"})
    Optional<BorrowRecord> findFirstByReferenceCodeIgnoreCaseOrBookCopyBarcodeIgnoreCaseOrderByCreatedAtDesc(
            String referenceCode,
            String barcode
    );

    @EntityGraph(attributePaths = {"user", "bookCopy", "bookCopy.book", "bookCopy.book.authors"})
    List<BorrowRecord> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("""
            select b from BorrowRecord b
            where b.user.id = :userId
              and (
                    :filter = 'ALL'
                    or (:filter = 'PENDING_PAYMENT'
                        and b.status = com.group_7.library_management.entity.BorrowStatus.PENDING_PAYMENT)
                    or (:filter = 'REQUESTED'
                        and b.status = com.group_7.library_management.entity.BorrowStatus.REQUESTED)
                    or (:filter = 'BORROWING'
                        and b.status = com.group_7.library_management.entity.BorrowStatus.BORROWED
                        and (b.dueAt is null or b.dueAt > :dueSoonUntil))
                    or (:filter = 'DUE_SOON'
                        and b.status = com.group_7.library_management.entity.BorrowStatus.BORROWED
                        and b.dueAt between :now and :dueSoonUntil)
                    or (:filter = 'OVERDUE'
                        and (b.status = com.group_7.library_management.entity.BorrowStatus.OVERDUE
                             or (b.status = com.group_7.library_management.entity.BorrowStatus.BORROWED
                                 and b.dueAt < :now)))
                    or (:filter = 'RETURNED'
                        and b.status = com.group_7.library_management.entity.BorrowStatus.RETURNED)
                    or (:filter = 'CANCELLED'
                        and b.status = com.group_7.library_management.entity.BorrowStatus.CANCELLED)
              )
            """)
    Page<BorrowRecord> findPageForUser(
            @Param("userId") Long userId,
            @Param("filter") String filter,
            @Param("now") Instant now,
            @Param("dueSoonUntil") Instant dueSoonUntil,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"user", "bookCopy", "bookCopy.book", "bookCopy.book.authors"})
    List<BorrowRecord> findAllByBookCopyBookIdOrderByCreatedAtDesc(Long bookId);

    @EntityGraph(attributePaths = {"user", "bookCopy", "bookCopy.book", "bookCopy.book.authors"})
    @Query(
            value = """
                    select b from BorrowRecord b
                    where (:search is null
                           or lower(b.referenceCode) like lower(concat('%', :search, '%'))
                           or lower(b.user.fullName) like lower(concat('%', :search, '%'))
                           or lower(b.user.email) like lower(concat('%', :search, '%'))
                           or lower(b.user.phone) like lower(concat('%', :search, '%'))
                           or lower(b.bookCopy.book.title) like lower(concat('%', :search, '%'))
                           or lower(b.bookCopy.book.isbn) like lower(concat('%', :search, '%'))
                           or lower(b.bookCopy.barcode) like lower(concat('%', :search, '%')))
                      and (
                           :status is null
                           or (:status = com.group_7.library_management.entity.BorrowStatus.OVERDUE
                               and (b.status = com.group_7.library_management.entity.BorrowStatus.OVERDUE
                                    or (b.status = com.group_7.library_management.entity.BorrowStatus.BORROWED
                                        and b.dueAt < :now)))
                           or (:status = com.group_7.library_management.entity.BorrowStatus.BORROWED
                               and b.status = com.group_7.library_management.entity.BorrowStatus.BORROWED
                               and (b.dueAt is null or b.dueAt >= :now))
                           or (:status <> com.group_7.library_management.entity.BorrowStatus.OVERDUE
                               and :status <> com.group_7.library_management.entity.BorrowStatus.BORROWED
                               and b.status = :status)
                      )
                    """,
            countQuery = """
                    select count(b) from BorrowRecord b
                    where (:search is null
                           or lower(b.referenceCode) like lower(concat('%', :search, '%'))
                           or lower(b.user.fullName) like lower(concat('%', :search, '%'))
                           or lower(b.user.email) like lower(concat('%', :search, '%'))
                           or lower(b.user.phone) like lower(concat('%', :search, '%'))
                           or lower(b.bookCopy.book.title) like lower(concat('%', :search, '%'))
                           or lower(b.bookCopy.book.isbn) like lower(concat('%', :search, '%'))
                           or lower(b.bookCopy.barcode) like lower(concat('%', :search, '%')))
                      and (
                           :status is null
                           or (:status = com.group_7.library_management.entity.BorrowStatus.OVERDUE
                               and (b.status = com.group_7.library_management.entity.BorrowStatus.OVERDUE
                                    or (b.status = com.group_7.library_management.entity.BorrowStatus.BORROWED
                                        and b.dueAt < :now)))
                           or (:status = com.group_7.library_management.entity.BorrowStatus.BORROWED
                               and b.status = com.group_7.library_management.entity.BorrowStatus.BORROWED
                               and (b.dueAt is null or b.dueAt >= :now))
                           or (:status <> com.group_7.library_management.entity.BorrowStatus.OVERDUE
                               and :status <> com.group_7.library_management.entity.BorrowStatus.BORROWED
                               and b.status = :status)
                      )
                    """
    )
    Page<BorrowRecord> searchForAdmin(
            @Param("search") String search,
            @Param("status") BorrowStatus status,
            @Param("now") Instant now,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"user", "bookCopy", "bookCopy.book"})
    List<BorrowRecord> findAllByStatusAndDueAtBetween(
            BorrowStatus status,
            Instant from,
            Instant to
    );

    @EntityGraph(attributePaths = {"user", "bookCopy", "bookCopy.book"})
    List<BorrowRecord> findAllByStatusInAndDueAtBefore(
            Collection<BorrowStatus> statuses,
            Instant dueAt
    );
}
