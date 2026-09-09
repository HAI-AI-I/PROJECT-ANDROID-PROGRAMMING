package com.group_7.library_management.di

import android.content.Context
import androidx.room.Room
import com.group_7.library_management.data.local.AppDatabase
import com.group_7.library_management.data.local.dao.BookDAO
import com.group_7.library_management.data.local.dao.NotificationDAO
import com.group_7.library_management.data.local.dao.UserDAO
import com.group_7.library_management.data.local.dao.SupportRequestDao
import com.group_7.library_management.data.repository.BookRepository
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
object DatabaseModule {
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
        .fallbackToDestructiveMigration(true)
        .build()
    }

    @Provides
    fun provideBookDao(database: AppDatabase): BookDAO {
        return database.getBookDao()
    }

    @Provides
    @Singleton
    fun provideBookRepository(bookDao: BookDAO): BookRepository {
        return BookRepository(bookDao)
    }

    @Provides
    fun provideNotificationDao(database: AppDatabase): NotificationDAO {
        return database.getNotificationDao()
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(notificationDao: NotificationDAO): NotificationRepository {
        return NotificationRepository(notificationDao)
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDAO {
        return database.getUserDao()
    }

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDAO): UserRepository {
        return UserRepository(userDao)
    }

    @Provides
    fun provideSupportRequestDao(database: AppDatabase): SupportRequestDao {
        return database.getSupportRequestDao()
    }

    @Provides
    @Singleton
    fun provideSupportRepository(supportRequestDao: SupportRequestDao): SupportRepository {
        return SupportRepository(supportRequestDao)
    }
}
