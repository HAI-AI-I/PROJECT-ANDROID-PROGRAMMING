package com.group_7.library_management.service;

import com.group_7.library_management.dto.BookReviewRequest;
import com.group_7.library_management.dto.BookReviewResponse;
import com.group_7.library_management.dto.MyBookReviewResponse;
import com.group_7.library_management.entity.Book;
import com.group_7.library_management.entity.BookReview;
import com.group_7.library_management.entity.User;
import com.group_7.library_management.exception.ConflictException;
import com.group_7.library_management.exception.ResourceNotFoundException;
import com.group_7.library_management.repository.BookRatingStatistics;
import com.group_7.library_management.repository.BookRepository;
import com.group_7.library_management.repository.BookReviewRepository;
import com.group_7.library_management.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class BookReviewService {

    private final BookReviewRepository bookReviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public BookReviewService(
            BookReviewRepository bookReviewRepository,
            BookRepository bookRepository,
            UserRepository userRepository
    ) {
        this.bookReviewRepository = bookReviewRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<BookReviewResponse> getReviews(Long bookId, Pageable pageable) {
        requireActiveBook(bookId);
        return bookReviewRepository.findByBookId(bookId, pageable)
                .map(BookReviewResponse::from);
    }

    @Transactional(readOnly = true)
    public MyBookReviewResponse getMyReview(Long userId, Long bookId) {
        requireActiveBook(bookId);
        return bookReviewRepository.findByUserIdAndBookId(userId, bookId)
                .map(BookReviewResponse::from)
                .map(MyBookReviewResponse::reviewed)
                .orElseGet(MyBookReviewResponse::notReviewed);
    }

    @Transactional
    public BookReviewResponse createReview(Long userId, Long bookId, BookReviewRequest request) {
        if (bookReviewRepository.existsByUserIdAndBookId(userId, bookId)) {
            throw new ConflictException("Bạn đã đánh giá sách này");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        Book book = requireActiveBook(bookId);
        BookReview review = new BookReview(
                user,
                book,
                request.rating(),
                normalizeComment(request.comment())
        );

        BookReview savedReview = bookReviewRepository.saveAndFlush(review);
        updateBookRating(book);
        return BookReviewResponse.from(savedReview);
    }

    @Transactional
    public BookReviewResponse updateReview(
            Long userId,
            Long bookId,
            Long reviewId,
            BookReviewRequest request
    ) {
        requireActiveBook(bookId);
        BookReview review = findOwnedReview(userId, bookId, reviewId);
        review.update(request.rating(), normalizeComment(request.comment()));
        bookReviewRepository.flush();
        updateBookRating(review.getBook());
        return BookReviewResponse.from(review);
    }

    @Transactional
    public void deleteReview(Long userId, Long bookId, Long reviewId) {
        BookReview review = findOwnedReview(userId, bookId, reviewId);
        Book book = review.getBook();
        bookReviewRepository.delete(review);
        bookReviewRepository.flush();
        updateBookRating(book);
    }

    private BookReview findOwnedReview(Long userId, Long bookId, Long reviewId) {
        BookReview review = bookReviewRepository.findByIdAndBookId(reviewId, bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá"));
        if (!review.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Không tìm thấy đánh giá");
        }
        return review;
    }

    private Book requireActiveBook(Long bookId) {
        return bookRepository.findByIdAndActiveTrue(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách"));
    }

    private void updateBookRating(Book book) {
        BookRatingStatistics statistics = bookReviewRepository.calculateStatistics(book.getId());
        long ratingCount = statistics == null ? 0 : statistics.getRatingCount();
        double averageRating = ratingCount == 0 || statistics == null || statistics.getAverageRating() == null
                ? 5.0
                : statistics.getAverageRating();

        book.setRatingCount(Math.toIntExact(ratingCount));
        book.setAverageRating(BigDecimal.valueOf(averageRating).setScale(2, RoundingMode.HALF_UP));
    }

    private String normalizeComment(String comment) {
        if (comment == null || comment.isBlank()) return null;
        return comment.trim();
    }
}
