package com.group_7.library_management.service;

import com.group_7.library_management.dto.AuthorRequest;
import com.group_7.library_management.dto.BookResponse;
import com.group_7.library_management.dto.CreateBookRequest;
import com.group_7.library_management.dto.PublisherRequest;
import com.group_7.library_management.dto.PopularBookResponse;
import com.group_7.library_management.entity.Book;
import com.group_7.library_management.entity.BookCopy;
import com.group_7.library_management.entity.BookFavorite;
import com.group_7.library_management.entity.BorrowRecord;
import com.group_7.library_management.entity.Notification;
import com.group_7.library_management.entity.NotificationType;
import com.group_7.library_management.entity.User;
import com.group_7.library_management.repository.AuthorRepository;
import com.group_7.library_management.repository.BookCopyRepository;
import com.group_7.library_management.repository.BookFavoriteRepository;
import com.group_7.library_management.repository.BookRepository;
import com.group_7.library_management.repository.BorrowRecordRepository;
import com.group_7.library_management.repository.NotificationRepository;
import com.group_7.library_management.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class BookServiceAuthorResolutionTests {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private BookFavoriteRepository favoriteRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void reusesExistingAuthorAndPublisherWhenIdentityMatches() {
        String suffix = UUID.randomUUID().toString();
        String authorName = "Tác giả " + suffix;
        String publisherName = "Nhà xuất bản " + suffix;
        AuthorRequest author = new AuthorRequest(
                null, authorName, "Bút danh", LocalDate.of(1980, 2, 3),
                null, "Việt Nam"
        );
        PublisherRequest publisher = new PublisherRequest(
                null, publisherName, "Hà Nội", "publisher@example.com", "0900000000"
        );

        BookResponse first = bookService.createBook(request(suffix + "-1", List.of(author), publisher));
        BookResponse second = bookService.createBook(request(suffix + "-2", List.of(author), publisher));

        assertThat(first.authorDetails().getFirst().id())
                .isEqualTo(second.authorDetails().getFirst().id());
        assertThat(first.publisherDetails().id()).isEqualTo(second.publisherDetails().id());
        assertThat(authorRepository.findAllByNameIgnoreCase(authorName)).hasSize(1);
    }

    @Test
    void allowsDifferentAuthorsWithTheSameName() {
        String suffix = UUID.randomUUID().toString();
        String authorName = "Tác giả trùng tên " + suffix;
        AuthorRequest firstAuthor = new AuthorRequest(
                null, authorName, null, LocalDate.of(1970, 1, 1),
                null, "Việt Nam"
        );
        AuthorRequest secondAuthor = new AuthorRequest(
                null, authorName, null, LocalDate.of(1990, 1, 1),
                null, "Việt Nam"
        );

        BookResponse first = bookService.createBook(request(suffix + "-1", List.of(firstAuthor), null));
        BookResponse second = bookService.createBook(request(suffix + "-2", List.of(secondAuthor), null));

        assertThat(first.authorDetails().getFirst().id())
                .isNotEqualTo(second.authorDetails().getFirst().id());
        assertThat(authorRepository.findAllByNameIgnoreCase(authorName)).hasSize(2);
    }

    @Test
    void returnsRequestedNumberOfLatestBooks() {
        String suffix = UUID.randomUUID().toString();
        AuthorRequest author = new AuthorRequest(
                null, "Tác giả " + suffix, null, LocalDate.of(1985, 1, 1),
                null, "Việt Nam"
        );
        BookResponse firstCreated = null;
        BookResponse lastCreated = null;

        for (int index = 1; index <= 16; index++) {
            BookResponse created = bookService.createBook(
                    request(suffix + "-latest-" + index, List.of(author), null)
            );
            if (index == 1) firstCreated = created;
            if (index == 16) lastCreated = created;
        }

        List<BookResponse> latestBooks = bookService.getLatestBooks(15);
        List<BookResponse> latestFiveBooks = bookService.getLatestBooks(5);

        assertThat(latestBooks).hasSize(15);
        assertThat(latestFiveBooks).hasSize(5);
        assertThat(latestBooks).extracting(BookResponse::id)
                .contains(lastCreated.id())
                .doesNotContain(firstCreated.id());
    }

    @Test
    void calculatesPopularityFromBusinessTablesInTheLastThirtyDays() {
        String suffix = UUID.randomUUID().toString();
        AuthorRequest author = new AuthorRequest(
                null, "Tác giả " + suffix, null, null, null, "Việt Nam"
        );
        BookResponse created = bookService.createBook(request(suffix, List.of(author), null));
        Book book = bookRepository.findById(created.id()).orElseThrow();
        BookCopy copy = bookCopyRepository.findAllByBookIdOrderByIdAsc(book.getId()).getFirst();
        User borrower = createUser(suffix + "-borrower");

        for (int index = 0; index < 4; index++) {
            borrowRecordRepository.save(new BorrowRecord(
                    "test-borrow-" + suffix + "-" + index, borrower, copy, Instant.now()
            ));
        }
        for (int index = 0; index < 3; index++) {
            favoriteRepository.save(new BookFavorite(createUser(suffix + "-favorite-" + index), book));
        }
        for (int index = 0; index < 2; index++) {
            Notification notification = new Notification(
                    "test-notification-" + suffix + "-" + index,
                    borrower,
                    book,
                    "Sách mới",
                    "Thông báo sách mới",
                    NotificationType.BOOK
            );
            notification.setClickedAt(Instant.now());
            notificationRepository.save(notification);
        }
        BorrowRecord expiredBorrow = new BorrowRecord(
                "test-expired-borrow-" + suffix,
                borrower,
                copy,
                Instant.now().minus(31, ChronoUnit.DAYS)
        );
        borrowRecordRepository.save(expiredBorrow);
        borrowRecordRepository.flush();
        favoriteRepository.flush();
        notificationRepository.flush();

        PopularBookResponse result = bookService.getPopularBooks(100).stream()
                .filter(item -> item.book().id().equals(created.id()))
                .findFirst()
                .orElseThrow();

        assertThat(result.borrowCount()).isEqualTo(4);
        assertThat(result.favoriteCount()).isEqualTo(3);
        assertThat(result.notificationClickCount()).isEqualTo(2);
        assertThat(result.popularityScore()).isEqualTo(30);
    }

    private User createUser(String uniqueValue) {
        String compact = uniqueValue.replace("-", "");
        return userRepository.save(new User(
                "Người dùng kiểm thử",
                compact + "@example.com",
                "09" + UUID.randomUUID().toString().replace("-", "").substring(0, 18),
                "test-password-hash"
        ));
    }

    private CreateBookRequest request(
            String uniqueValue,
            List<AuthorRequest> authors,
            PublisherRequest publisher
    ) {
        return new CreateBookRequest(
                UUID.randomUUID().toString().replace("-", "").substring(0, 20),
                "Sách kiểm thử " + uniqueValue,
                null,
                authors,
                "Thể loại kiểm thử",
                null,
                publisher,
                2026,
                1,
                null,
                null,
                0L,
                "A1"
        );
    }
}
