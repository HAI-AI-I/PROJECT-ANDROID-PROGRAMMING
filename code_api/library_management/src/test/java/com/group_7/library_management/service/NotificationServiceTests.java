package com.group_7.library_management.service;

import com.group_7.library_management.dto.NotificationResponse;
import com.group_7.library_management.entity.Book;
import com.group_7.library_management.entity.Notification;
import com.group_7.library_management.entity.NotificationActionType;
import com.group_7.library_management.entity.NotificationType;
import com.group_7.library_management.entity.User;
import com.group_7.library_management.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTests {
    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void marksOnlyTheRequestedUsersNotificationAsRead() {
        Notification notification = notification("notification-1");
        when(notificationRepository.findByIdAndUserIdAndDeletedAtIsNull(11L, 7L))
                .thenReturn(Optional.of(notification));

        NotificationResponse response = notificationService.markAsRead(7L, 11L);

        assertThat(response.isRead()).isTrue();
        assertThat(response.readAt()).isNotNull();
        verify(notificationRepository).findByIdAndUserIdAndDeletedAtIsNull(11L, 7L);
    }

    @Test
    void marksEveryUnreadNotificationForTheCurrentUserAsRead() {
        Notification first = notification("notification-1");
        Notification second = notification("notification-2");
        when(notificationRepository.findByUserIdAndReadAtIsNullAndDeletedAtIsNull(7L))
                .thenReturn(List.of(first, second));

        int updatedCount = notificationService.markAllAsRead(7L);

        assertThat(updatedCount).isEqualTo(2);
        assertThat(first.isRead()).isTrue();
        assertThat(second.isRead()).isTrue();
    }

    @Test
    void clickingANotificationAlsoMarksItAsRead() {
        Notification notification = notification("notification-click");
        when(notificationRepository.findByIdAndUserIdAndDeletedAtIsNull(15L, 7L))
                .thenReturn(Optional.of(notification));

        NotificationResponse response = notificationService.markAsClicked(7L, 15L);

        assertThat(response.isClicked()).isTrue();
        assertThat(response.isRead()).isTrue();
        assertThat(response.clickedAt()).isNotNull();
        assertThat(response.readAt()).isNotNull();
    }

    @Test
    void notificationWithoutDestinationReturnsNoneAction() {
        NotificationResponse response = NotificationResponse.from(notification("notification-none"));

        assertThat(response.actionType()).isEqualTo(NotificationActionType.NONE);
        assertThat(response.targetId()).isNull();
    }

    @Test
    void bookNotificationReturnsBookDetailDestination() {
        Book book = mock(Book.class);
        when(book.getId()).thenReturn(42L);
        User user = new User("Người dùng", "book@example.com", "0900000001", "password");
        Notification notification = new Notification(
                "notification-book",
                user,
                book,
                "Sách đã có lại",
                "Sách hiện có thể mượn.",
                NotificationType.BOOK
        );

        NotificationResponse response = NotificationResponse.from(notification);

        assertThat(response.actionType()).isEqualTo(NotificationActionType.BOOK_DETAIL);
        assertThat(response.targetId()).isEqualTo(42L);
    }

    @Test
    void deletingANotificationUsesSoftDelete() {
        Notification notification = notification("notification-delete");
        when(notificationRepository.findByIdAndUserIdAndDeletedAtIsNull(18L, 7L))
                .thenReturn(Optional.of(notification));

        notificationService.deleteNotification(7L, 18L);

        assertThat(notification.isDeleted()).isTrue();
        assertThat(notification.getDeletedAt()).isNotNull();
    }

    @Test
    void countsOnlyUnreadAndVisibleNotificationsForTheCurrentUser() {
        when(notificationRepository.countByUserIdAndReadAtIsNullAndDeletedAtIsNull(7L))
                .thenReturn(4L);

        long unreadCount = notificationService.getUnreadCount(7L);

        assertThat(unreadCount).isEqualTo(4L);
        verify(notificationRepository)
                .countByUserIdAndReadAtIsNullAndDeletedAtIsNull(7L);
    }

    @Test
    void loadsTheRequestedNotificationPage() {
        when(notificationRepository.findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(
                7L,
                PageRequest.of(2, 20)
        )).thenReturn(List.of());

        List<NotificationResponse> result = notificationService.getNotifications(7L, 2, 20);

        assertThat(result).isEmpty();
        verify(notificationRepository).findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(
                7L,
                PageRequest.of(2, 20)
        );
    }

    private Notification notification(String key) {
        User user = new User("Người dùng", key + "@example.com", "0900000000", "password");
        return new Notification(
                key,
                user,
                null,
                "Thông báo",
                "Nội dung thông báo",
                NotificationType.INFO
        );
    }
}
