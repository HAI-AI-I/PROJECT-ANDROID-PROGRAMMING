package com.group_7.library_management.repository;

public interface BookPopularityStatistics {
    Long getBookId();
    Long getBorrowCount();
    Long getFavoriteCount();
    Long getNotificationClickCount();
    Long getPopularityScore();
}
