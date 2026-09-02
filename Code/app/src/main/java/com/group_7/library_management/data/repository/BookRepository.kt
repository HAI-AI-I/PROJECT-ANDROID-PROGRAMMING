package com.group_7.library_management.data.repository

import com.group_7.library_management.data.local.dao.BookDAO
import com.group_7.library_management.data.mapper.toBookModel
import com.group_7.library_management.models.Book
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookRepository(private val bookDao: BookDAO) {
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
