package com.group_7.library_management.data.repository

import com.group_7.library_management.data.local.dao.BookDAO
import com.group_7.library_management.data.mapper.toBookModel
import com.group_7.library_management.data.mapper.toEntity
import com.group_7.library_management.data.remote.api.BookApi
import com.group_7.library_management.models.Book
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookRepository(
    private val bookDao: BookDAO,
    private val bookApi: BookApi
) {
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
}
