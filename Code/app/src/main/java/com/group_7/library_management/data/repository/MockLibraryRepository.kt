package com.group_7.library_management.data.repository

import com.group_7.library_management.models.Book
import com.group_7.library_management.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockLibraryRepository : LibraryRepository {
    
    private val mockUser = User(
        id = "1",
        name = "Nguyễn Văn An",
        studentId = "UTH123456",
        qrCodeData = "STUDENT_UTH123456"
    )

    private val mockBooks = listOf(
        Book(
            id = "b1",
            title = "Lập trình Android với Kotlin",
            author = "Google Developers",
            isAvailable = true
        ),
        Book(
            id = "b2",
            title = "Clean Architecture",
            author = "Robert C. Martin",
            isAvailable = false,
            remainingDays = 3
        ),
        Book(
            id = "b3",
            title = "Design Patterns",
            author = "Gang of Four",
            isAvailable = true
        ),
        Book(
            id = "b4",
            title = "Kotlin in Action",
            author = "Dmitry Jemerov",
            isAvailable = true
        ),
        Book(
            id = "b5",
            title = "Effective Java",
            author = "Joshua Bloch",
            isAvailable = false,
            remainingDays = 0,
            isOverdue = true
        )
    )

    override fun getCurrentUser(): Flow<User?> = flowOf(mockUser)

    override fun getBorrowedBooks(): Flow<List<Book>> = flowOf(
        mockBooks.filter { !it.isAvailable }
    )

    override fun getPopularBooks(): Flow<List<Book>> = flowOf(
        mockBooks.take(3)
    )

    override fun getAllBooks(): Flow<List<Book>> = flowOf(mockBooks)

    override fun getBookById(id: String): Flow<Book?> = flowOf(
        mockBooks.find { it.id == id }
    )

    override suspend fun borrowBook(bookId: String): Result<String> {
        return Result.success("TX-${System.currentTimeMillis()}")
    }
}
