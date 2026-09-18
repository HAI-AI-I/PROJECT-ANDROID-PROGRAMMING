package com.group_7.library_management.data.repository

import com.group_7.library_management.data.local.dao.BookDAO
import com.group_7.library_management.data.mapper.toBookModel
import com.group_7.library_management.data.mapper.toEntity
import com.group_7.library_management.data.remote.api.BookApi
import com.group_7.library_management.data.remote.dto.BookReviewRequestDto
import com.group_7.library_management.data.remote.dto.BookReviewResponseDto
import com.group_7.library_management.data.remote.dto.PagedResponseDto
import com.group_7.library_management.models.Book
import com.group_7.library_management.models.BookReview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookRepository(
    private val bookDao: BookDAO,
    private val bookApi: BookApi
) {
    suspend fun getBookDetail(id: String): Book {
        return bookApi.getBook(id).toBookModel()
    }

    suspend fun isAvailabilitySubscribed(id: String): Boolean {
        return bookApi.getAvailabilitySubscription(id).subscribed
    }

    suspend fun setAvailabilitySubscription(id: String, subscribed: Boolean): Boolean {
        val response = if (subscribed) {
            bookApi.subscribeToAvailability(id)
        } else {
            bookApi.unsubscribeFromAvailability(id)
        }
        return response.subscribed
    }

    suspend fun getRelatedBookDetails(id: String, limit: Int): List<Book> {
        return bookApi.getRelatedBooks(id, limit).map { it.toBookModel() }
    }

    suspend fun getBookReviews(
        id: String,
        page: Int,
        pageSize: Int
    ): PagedResponseDto<BookReview> {
        val response = bookApi.getBookReviews(id, page, pageSize)
        return PagedResponseDto(
            items = response.items.map { it.toBookReview() },
            total = response.total,
            page = response.page,
            pageSize = response.pageSize,
            totalPages = response.totalPages
        )
    }

    suspend fun createBookReview(id: String, rating: Int, comment: String?): BookReview {
        return bookApi.createBookReview(
            id,
            BookReviewRequestDto(rating, comment?.trim()?.ifBlank { null })
        ).toBookReview()
    }

    suspend fun getMyBookReview(id: String): BookReview? {
        return bookApi.getMyBookReview(id).review?.toBookReview()
    }

    suspend fun updateBookReview(
        id: String,
        reviewId: Long,
        rating: Int,
        comment: String?
    ): BookReview {
        return bookApi.updateBookReview(
            id,
            reviewId,
            BookReviewRequestDto(rating, comment?.trim()?.ifBlank { null })
        ).toBookReview()
    }

    suspend fun deleteBookReview(id: String, reviewId: Long) {
        bookApi.deleteBookReview(id, reviewId)
    }

    suspend fun refreshNewestBooks(limit: Int) {
        val remoteBooks = bookApi.getLatestBooks(limit)
        bookDao.replaceBooks(remoteBooks.map { it.toEntity() })
    }

    suspend fun refreshHomeBooks(newestLimit: Int, popularLimit: Int) {
        val newestBooks = bookApi.getLatestBooks(newestLimit).map { it.toEntity() }
        val popularBooks = bookApi.getPopularBooks(popularLimit).map { it.toEntity() }
        val homeBooks = (newestBooks + popularBooks)
            .associateBy { it.id }
            .values
            .toList()
        bookDao.replaceBooks(homeBooks)
    }

    fun getNewestBooks(limit: Int = 10): Flow<List<Book>> {
        return bookDao.getNewestBooks(limit).map{ list ->
            list.map { it.toBookModel() }
        }
    }

    fun getPopularBooks(limit: Int = 10): Flow<List<Book>> {
        return bookDao.getPopularBooks(limit).map { list ->
            list.map { it.toBookModel() }
        }
    }

    fun getRecommendedBooks(limit: Int = 10): Flow<List<Book>> {
        return bookDao.getRecommendedBooks(limit).map { list ->
            list.map { it.toBookModel() }
        }
    }

    private fun BookReviewResponseDto.toBookReview() = BookReview(
        id = id,
        userId = userId,
        userName = userName,
        rating = rating,
        comment = comment,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
