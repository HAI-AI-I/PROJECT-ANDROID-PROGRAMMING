package com.group_7.library_management.service;

import com.group_7.library_management.dto.AdminDashboardResponse;
import com.group_7.library_management.dto.AdminStatisticsResponse;
import com.group_7.library_management.dto.BorrowOrderResponse;
import com.group_7.library_management.entity.UserRole;
import com.group_7.library_management.repository.BookRepository;
import com.group_7.library_management.repository.BorrowRecordRepository;
import com.group_7.library_management.repository.LabelCountProjection;
import com.group_7.library_management.repository.PeriodCountProjection;
import com.group_7.library_management.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class AdminAnalyticsService {

    private static final ZoneId LIBRARY_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter DAY_LABEL = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter MONTH_KEY = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter MONTH_LABEL = DateTimeFormatter.ofPattern("MM/yyyy");

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final BorrowOrderService borrowOrderService;
    private final Clock clock;

    @Autowired
    public AdminAnalyticsService(
            BookRepository bookRepository,
            UserRepository userRepository,
            BorrowRecordRepository borrowRecordRepository,
            BorrowOrderService borrowOrderService
    ) {
        this(bookRepository, userRepository, borrowRecordRepository, borrowOrderService, Clock.systemUTC());
    }

    AdminAnalyticsService(
            BookRepository bookRepository,
            UserRepository userRepository,
            BorrowRecordRepository borrowRecordRepository,
            BorrowOrderService borrowOrderService,
            Clock clock
    ) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.borrowRecordRepository = borrowRecordRepository;
        this.borrowOrderService = borrowOrderService;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboard() {
        Instant now = clock.instant();
        LocalDate today = LocalDate.ofInstant(now, LIBRARY_ZONE);
        Instant chartStart = today.minusDays(6).atStartOfDay(LIBRARY_ZONE).toInstant();

        List<BorrowOrderResponse> recent = borrowOrderService.getOrdersForAdmin(
                null, null, PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent();
        List<BorrowOrderResponse> overdue = borrowOrderService.getOrdersForAdmin(
                null, "overdue", PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "dueAt"))
        ).getContent();

        return new AdminDashboardResponse(
                bookRepository.countByActiveTrue(),
                borrowRecordRepository.countCurrentlyBorrowed(now),
                userRepository.countByRole(UserRole.USER),
                borrowRecordRepository.countAllOverdue(now),
                buildDailyTrend(today, chartStart),
                recent,
                overdue
        );
    }

    @Transactional(readOnly = true)
    public AdminStatisticsResponse getStatistics() {
        Instant now = clock.instant();
        YearMonth currentMonth = YearMonth.from(LocalDate.ofInstant(now, LIBRARY_ZONE));
        Instant trendStart = currentMonth.minusMonths(5).atDay(1).atStartOfDay(LIBRARY_ZONE).toInstant();

        return new AdminStatisticsResponse(
                borrowRecordRepository.countByBorrowedAtIsNotNull(),
                borrowRecordRepository.countByReturnedAtIsNotNull(),
                borrowRecordRepository.countAllOverdue(now),
                buildMonthlyTrend(currentMonth, borrowRecordRepository.countBorrowingsByMonthSince(trendStart)),
                buildMonthlyTrend(currentMonth, borrowRecordRepository.countReturnsByMonthSince(trendStart)),
                bookRepository.countActiveBooksByCategory().stream()
                        .map(point -> new AdminStatisticsResponse.CategoryPoint(point.getLabel(), point.getTotal()))
                        .toList(),
                toRankedPoints(borrowRecordRepository.findMostBorrowedBooks(PageRequest.of(0, 5))),
                toRankedPoints(borrowRecordRepository.findMostActiveReaders(PageRequest.of(0, 5)))
        );
    }

    private List<AdminDashboardResponse.ChartPoint> buildDailyTrend(LocalDate today, Instant since) {
        Map<String, Long> counts = borrowRecordRepository.countBorrowingsByDaySince(since).stream()
                .collect(Collectors.toMap(PeriodCountProjection::getPeriod, PeriodCountProjection::getTotal));
        return IntStream.rangeClosed(0, 6)
                .mapToObj(offset -> today.minusDays(6L - offset))
                .map(day -> new AdminDashboardResponse.ChartPoint(
                        day.format(DAY_LABEL),
                        counts.getOrDefault(day.toString(), 0L)
                ))
                .toList();
    }

    private List<AdminStatisticsResponse.TrendPoint> buildMonthlyTrend(
            YearMonth currentMonth,
            List<PeriodCountProjection> source
    ) {
        Map<String, Long> counts = source.stream().collect(Collectors.toMap(
                PeriodCountProjection::getPeriod,
                PeriodCountProjection::getTotal
        ));
        return IntStream.rangeClosed(0, 5)
                .mapToObj(offset -> currentMonth.minusMonths(5L - offset))
                .map(month -> new AdminStatisticsResponse.TrendPoint(
                        month.format(MONTH_LABEL),
                        counts.getOrDefault(month.format(MONTH_KEY), 0L)
                ))
                .toList();
    }

    private List<AdminStatisticsResponse.RankedPoint> toRankedPoints(List<LabelCountProjection> source) {
        return source.stream()
                .map(point -> new AdminStatisticsResponse.RankedPoint(point.getLabel(), point.getTotal()))
                .toList();
    }
}
