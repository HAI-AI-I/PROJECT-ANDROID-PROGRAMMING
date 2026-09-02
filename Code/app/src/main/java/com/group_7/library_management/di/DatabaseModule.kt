package com.group_7.library_management.di


import android.content.Context
import androidx.room.Room
import com.group_7.library_management.data.local.AppDatabase
import com.group_7.library_management.data.local.dao.BookDAO
import com.group_7.library_management.data.repository.BookRepository
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
        ).build()
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

}