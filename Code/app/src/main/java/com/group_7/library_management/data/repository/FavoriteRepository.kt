package com.group_7.library_management.data.repository

import com.group_7.library_management.data.local.dao.FavoriteBookDao
import com.group_7.library_management.data.local.entity.FavoriteBookEntity
import com.group_7.library_management.data.local.preferences.CheckLogin
import com.group_7.library_management.data.mapper.toBookModel
import com.group_7.library_management.data.remote.api.BookApi
import com.group_7.library_management.models.Book
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class FavoriteRepository(
    private val favoriteBookDao: FavoriteBookDao,
    private val bookApi: BookApi,
    private val checkLogin: CheckLogin
) {
    fun observeCachedFavorites(): Flow<List<Book>> {
        val userId = checkLogin.getSavedUserId() ?: return flowOf(emptyList())
        return favoriteBookDao.observeByUser(userId).map { favorites ->
            favorites.map(FavoriteBookEntity::toBook)
        }
    }

    suspend fun refreshFavorites() {
        val userId = requireUserId()
        val books = bookApi.getFavoriteBooks().map { it.toBookModel() }
        val now = System.currentTimeMillis()
        favoriteBookDao.replaceForUser(
            userId,
            books.mapIndexed { index, book -> book.toFavoriteEntity(userId, now - index) }
        )
    }

    suspend fun isFavorite(bookId: String): Boolean {
        return bookApi.getFavoriteStatus(bookId).favorite
    }

    suspend fun setFavorite(book: Book, favorite: Boolean): Boolean {
        val userId = requireUserId()
        val result = if (favorite) {
            bookApi.addFavorite(book.id)
        } else {
            bookApi.removeFavorite(book.id)
        }
        if (result.favorite) {
            favoriteBookDao.upsert(book.toFavoriteEntity(userId, System.currentTimeMillis()))
        } else {
            favoriteBookDao.delete(userId, book.id)
        }
        return result.favorite
    }

    private fun requireUserId(): String = checkLogin.getSavedUserId()
        ?: error("Không tìm thấy tài khoản đang đăng nhập")
}

private fun Book.toFavoriteEntity(userId: String, favoritedAt: Long) = FavoriteBookEntity(
    userId = userId,
    bookId = id,
    isbn = isbn,
    title = title,
    author = author,
    category = category,
    publisher = publisher,
    publishYear = publishYear,
    totalCopies = totalCopies,
    coverImageUrl = coverImageUrl,
    description = description,
    borrowFee = borrowFee,
    availableCopies = availableCopies,
    rating = rating,
    createdAt = createdAt,
    ratingCount = ratingCount,
    popularityScore = popularityScore,
    favoritedAt = favoritedAt
)

private fun FavoriteBookEntity.toBook() = Book(
    id = bookId,
    isbn = isbn,
    title = title,
    author = author,
    category = category,
    publisher = publisher,
    publishYear = publishYear,
    totalCopies = totalCopies,
    coverImageUrl = coverImageUrl,
    description = description,
    borrowFee = borrowFee,
    availableCopies = availableCopies,
    rating = rating,
    createdAt = createdAt,
    ratingCount = ratingCount,
    popularityScore = popularityScore
)
