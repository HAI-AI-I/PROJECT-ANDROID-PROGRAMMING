package com.group_7.library_management.di

import android.content.Context
import com.group_7.library_management.data.local.preferences.CheckLogin
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideCheckLogin(@ApplicationContext context: Context): CheckLogin {
        return CheckLogin(context)
    }
}
