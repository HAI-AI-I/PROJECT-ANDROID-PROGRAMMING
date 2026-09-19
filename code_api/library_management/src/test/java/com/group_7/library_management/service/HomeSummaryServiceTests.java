package com.group_7.library_management.service;

import com.group_7.library_management.dto.HomeSummaryResponse;
import com.group_7.library_management.entity.BorrowStatus;
import com.group_7.library_management.repository.BookFavoriteRepository;
import com.group_7.library_management.repository.BorrowRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HomeSummaryServiceTests {
    @Mock
    private BorrowRecordRepository borrowRecordRepository;

    @Mock
    private BookFavoriteRepository bookFavoriteRepository;

    @InjectMocks
    private HomeSummaryService homeSummaryService;

    @Test
    void getSummaryMapsEveryDatabaseCountToItsMatchingField() {
        long userId = 22L;
        when(borrowRecordRepository.countByUserIdAndStatus(userId, BorrowStatus.REQUESTED))
                .thenReturn(2L);
        when(borrowRecordRepository.countByUserIdAndStatusAndDueAtAfter(
                eq(userId), eq(BorrowStatus.BORROWED), any(Instant.class)))
                .thenReturn(3L);
        when(borrowRecordRepository.countByUserIdAndStatusAndDueAtBetween(
                eq(userId), eq(BorrowStatus.BORROWED), any(Instant.class), any(Instant.class)))
                .thenReturn(4L);
        when(borrowRecordRepository.countOverdueByUserId(eq(userId), any(Instant.class)))
                .thenReturn(5L);
        when(bookFavoriteRepository.countByUserId(userId)).thenReturn(6L);
        when(borrowRecordRepository.countByUserIdAndStatus(userId, BorrowStatus.RETURNED))
                .thenReturn(7L);
        when(borrowRecordRepository.countByUserId(userId)).thenReturn(8L);

        HomeSummaryResponse summary = homeSummaryService.getSummary(userId);

        assertEquals(2L, summary.pendingPickupCount());
        assertEquals(3L, summary.borrowingCount());
        assertEquals(4L, summary.dueSoonCount());
        assertEquals(5L, summary.overdueCount());
        assertEquals(6L, summary.favoriteCount());
        assertEquals(7L, summary.returnedCount());
        assertEquals(8L, summary.allBorrowCount());
    }
}
