package com.group_7.library_management.service;

import com.group_7.library_management.dto.HomeSummaryResponse;
import com.group_7.library_management.entity.BorrowStatus;
import com.group_7.library_management.repository.BookFavoriteRepository;
import com.group_7.library_management.repository.BorrowRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Service
public class HomeSummaryService {
    private static final Duration DUE_SOON_WINDOW = Duration.ofDays(1);

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookFavoriteRepository bookFavoriteRepository;

    public HomeSummaryService(
            BorrowRecordRepository borrowRecordRepository,
            BookFavoriteRepository bookFavoriteRepository
    ) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.bookFavoriteRepository = bookFavoriteRepository;
    }

    @Transactional(readOnly = true)
    public HomeSummaryResponse getSummary(Long userId) {
        Instant now = Instant.now();
        Instant dueSoonUntil = now.plus(DUE_SOON_WINDOW);

        long pendingPickupCount = borrowRecordRepository.countByUserIdAndStatus(
                userId,
                BorrowStatus.REQUESTED
        );
        long borrowingCount = borrowRecordRepository.countByUserIdAndStatusAndDueAtAfter(
                userId,
                BorrowStatus.BORROWED,
                dueSoonUntil
        );
        long dueSoonCount = borrowRecordRepository.countByUserIdAndStatusAndDueAtBetween(
                userId,
                BorrowStatus.BORROWED,
                now,
                dueSoonUntil
        );
        long overdueCount = borrowRecordRepository.countOverdueByUserId(userId, now);
        long favoriteCount = bookFavoriteRepository.countByUserId(userId);
        long returnedCount = borrowRecordRepository.countByUserIdAndStatus(
                userId,
                BorrowStatus.RETURNED
        );
        long allBorrowCount = borrowRecordRepository.countByUserId(userId);

        return new HomeSummaryResponse(
                pendingPickupCount,
                borrowingCount,
                dueSoonCount,
                overdueCount,
                favoriteCount,
                returnedCount,
                allBorrowCount
        );
    }
}
