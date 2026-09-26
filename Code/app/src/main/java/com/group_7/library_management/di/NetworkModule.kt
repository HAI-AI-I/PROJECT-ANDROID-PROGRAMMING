package com.group_7.library_management.di

import android.content.Context
import com.group_7.library_management.BuildConfig
import com.group_7.library_management.data.local.preferences.CheckLogin
import com.group_7.library_management.data.remote.api.AuthApi
import com.group_7.library_management.data.remote.api.BookApi
import com.group_7.library_management.data.remote.api.BorrowApi
import com.group_7.library_management.data.remote.api.NotificationApi
import com.group_7.library_management.data.remote.api.ScanApi
import com.group_7.library_management.data.remote.api.SupportApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object  NetworkModule {
    @Provides
    @Singleton
    fun provideCheckLogin(@ApplicationContext context: Context): CheckLogin = CheckLogin(context)

    @Provides
    @Singleton
    fun provideOkHttpClient(checkLogin: CheckLogin): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val accessToken = checkLogin.getAccessToken()
            val request = if (accessToken.isNullOrBlank()) {
                chain.request()
            } else {
                chain.request().newBuilder()
                    .header("Authorization", "Bearer $accessToken")
                    .build()
            }
            chain.proceed(request)
        }
        .connectTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .callTimeout(60, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideBookApi(retrofit: Retrofit): BookApi = retrofit.create(BookApi::class.java)

    @Provides
    @Singleton
    fun provideBorrowApi(retrofit: Retrofit): BorrowApi = retrofit.create(BorrowApi::class.java)

    @Provides
    @Singleton
    fun provideNotificationApi(retrofit: Retrofit): NotificationApi =
        retrofit.create(NotificationApi::class.java)

    @Provides
    @Singleton
    fun provideScanApi(retrofit: Retrofit): ScanApi = retrofit.create(ScanApi::class.java)

    @Provides
    @Singleton
    fun provideSupportApi(retrofit: Retrofit): SupportApi = retrofit.create(SupportApi::class.java)
}
