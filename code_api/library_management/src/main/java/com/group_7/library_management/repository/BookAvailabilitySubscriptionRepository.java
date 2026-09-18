package com.group_7.library_management.repository;

import com.group_7.library_management.entity.BookAvailabilitySubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookAvailabilitySubscriptionRepository
        extends JpaRepository<BookAvailabilitySubscription, Long> {
    Optional<BookAvailabilitySubscription> findByUserIdAndBookId(Long userId, Long bookId);
    List<BookAvailabilitySubscription> findAllByBookId(Long bookId);
}
