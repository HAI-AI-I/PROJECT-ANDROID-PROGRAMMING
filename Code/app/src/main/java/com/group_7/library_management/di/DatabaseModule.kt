package com.group_7.library_management.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.group_7.library_management.data.local.AppDatabase
import com.group_7.library_management.data.local.dao.BookDAO
import com.group_7.library_management.data.local.dao.FavoriteBookDao
import com.group_7.library_management.data.local.dao.NotificationDAO
import com.group_7.library_management.data.local.dao.HomeSummaryDao
import com.group_7.library_management.data.local.dao.UserDAO
import com.group_7.library_management.data.local.dao.SupportRequestDao
import com.group_7.library_management.data.local.preferences.CheckLogin
import com.group_7.library_management.data.remote.api.AuthApi
import com.group_7.library_management.data.remote.api.BookApi
import com.group_7.library_management.data.remote.api.NotificationApi
import com.group_7.library_management.data.remote.api.SupportApi
import com.group_7.library_management.data.repository.BookRepository
import com.group_7.library_management.data.repository.FavoriteRepository
import com.group_7.library_management.data.repository.NotificationRepository
import com.group_7.library_management.data.repository.UserRepository
import com.group_7.library_management.data.repository.SupportRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object  DatabaseModule {
    private val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE books RENAME COLUMN viewCount TO ratingCount")
            db.execSQL("ALTER TABLE books ADD COLUMN coverImageUrl TEXT")
        }
    }

    private val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE books ADD COLUMN popularityScore INTEGER NOT NULL DEFAULT 0")
        }
    }

    private val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE notifications ADD COLUMN userId INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE notifications ADD COLUMN bookId INTEGER")
        }
    }

    private val MIGRATION_7_8 = object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE books ADD COLUMN isbn TEXT")
            db.execSQL("ALTER TABLE books ADD COLUMN publisher TEXT")
            db.execSQL("ALTER TABLE books ADD COLUMN publishYear INTEGER")
            db.execSQL("ALTER TABLE books ADD COLUMN totalCopies INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE books ADD COLUMN description TEXT")
        }
    }

    private val MIGRATION_8_9 = object : Migration(8, 9) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
                UPDATE books
                SET isbn = NULL,
                    publisher = NULL,
                    publishYear = NULL,
                    totalCopies = 0,
                    description = NULL
            """.trimIndent())
        }
    }

    private val MIGRATION_9_10 = object : Migration(9, 10) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS favorite_books (
                    userId TEXT NOT NULL,
                    bookId TEXT NOT NULL,
                    isbn TEXT,
                    title TEXT NOT NULL,
                    author TEXT NOT NULL,
                    category TEXT NOT NULL,
                    publisher TEXT,
                    publishYear INTEGER,
                    totalCopies INTEGER NOT NULL,
                    coverImageUrl TEXT,
                    description TEXT,
                    borrowFee INTEGER NOT NULL,
                    availableCopies INTEGER NOT NULL,
                    rating REAL NOT NULL,
                    createdAt INTEGER NOT NULL,
                    ratingCount INTEGER NOT NULL,
                    popularityScore INTEGER NOT NULL,
                    favoritedAt INTEGER NOT NULL,
                    PRIMARY KEY(userId, bookId)
                )
                """.trimIndent()
            )
        }
    }

    private val MIGRATION_10_11 = object : Migration(10, 11) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS home_summaries (
                    userId INTEGER NOT NULL,
                    pendingPickupCount INTEGER NOT NULL,
                    borrowingCount INTEGER NOT NULL,
                    dueSoonCount INTEGER NOT NULL,
                    overdueCount INTEGER NOT NULL,
                    favoriteCount INTEGER NOT NULL,
                    returnedCount INTEGER NOT NULL,
                    updatedAt INTEGER NOT NULL,
                    PRIMARY KEY(userId)
                )
                """.trimIndent()
            )
        }
    }

    private val MIGRATION_11_12 = object : Migration(11, 12) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE home_summaries RENAME COLUMN returnedCount TO allBorrowCount")
        }
    }

    private val MIGRATION_12_13 = object : Migration(12, 13) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE home_summaries ADD COLUMN returnedCount INTEGER NOT NULL DEFAULT 0"
            )
        }
    }

    private val MIGRATION_13_14 = object : Migration(13, 14) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE notifications ADD COLUMN actionType TEXT NOT NULL DEFAULT 'NONE'"
            )
            db.execSQL("ALTER TABLE notifications ADD COLUMN targetId INTEGER")
            db.execSQL(
                """
                UPDATE notifications
                SET actionType = 'BOOK_DETAIL',
                    targetId = bookId
                WHERE bookId IS NOT NULL
                """.trimIndent()
            )
        }
    }

    private val MIGRATION_14_15 = object : Migration(14, 15) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("DROP TABLE IF EXISTS support_requests")
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS support_requests (
                    id INTEGER NOT NULL,
                    userId INTEGER NOT NULL,
                    bookId INTEGER,
                    bookTitle TEXT,
                    subject TEXT NOT NULL,
                    message TEXT NOT NULL,
                    status TEXT NOT NULL,
                    adminReply TEXT,
                    repliedAt TEXT,
                    createdAt TEXT NOT NULL,
                    updatedAt TEXT NOT NULL,
                    PRIMARY KEY(id)
                )
                """.trimIndent()
            )
        }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "library_management_db"
        )
        .addMigrations(
            MIGRATION_4_5,
            MIGRATION_5_6,
            MIGRATION_6_7,
            MIGRATION_7_8,
            MIGRATION_8_9,
            MIGRATION_9_10,
            MIGRATION_10_11,
            MIGRATION_11_12,
            MIGRATION_12_13,
            MIGRATION_13_14,
            MIGRATION_14_15
        )
        .fallbackToDestructiveMigration(true)
        .build()
    }

    @Provides
    fun provideBookDao(database: AppDatabase): BookDAO {
        return database.getBookDao()
    }

    @Provides
    fun provideFavoriteBookDao(database: AppDatabase): FavoriteBookDao {
        return database.getFavoriteBookDao()
    }

    @Provides
    fun provideHomeSummaryDao(database: AppDatabase): HomeSummaryDao {
        return database.getHomeSummaryDao()
    }

    @Provides
    @Singleton
    fun provideBookRepository(bookDao: BookDAO, bookApi: BookApi): BookRepository {
        return BookRepository(bookDao, bookApi)
    }

    @Provides
    @Singleton
    fun provideFavoriteRepository(
        favoriteBookDao: FavoriteBookDao,
        bookApi: BookApi,
        checkLogin: CheckLogin
    ): FavoriteRepository {
        return FavoriteRepository(favoriteBookDao, bookApi, checkLogin)
    }

    @Provides
    fun provideNotificationDao(database: AppDatabase): NotificationDAO {
        return database.getNotificationDao()
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(
        notificationDao: NotificationDAO,
        notificationApi: NotificationApi,
        checkLogin: CheckLogin
    ): NotificationRepository {
        return NotificationRepository(notificationDao, notificationApi, checkLogin)
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDAO {
        return database.getUserDao()
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        userDao: UserDAO,
        authApi: AuthApi,
        checkLogin: CheckLogin
    ): UserRepository {
        return UserRepository(userDao, authApi, checkLogin)
    }

    @Provides
    fun provideSupportRequestDao(database: AppDatabase): SupportRequestDao {
        return database.getSupportRequestDao()
    }

    @Provides
    @Singleton
    fun provideSupportRepository(
        supportRequestDao: SupportRequestDao,
        supportApi: SupportApi,
        checkLogin: CheckLogin
    ): SupportRepository {
        return SupportRepository(supportRequestDao, supportApi, checkLogin)
    }
}
