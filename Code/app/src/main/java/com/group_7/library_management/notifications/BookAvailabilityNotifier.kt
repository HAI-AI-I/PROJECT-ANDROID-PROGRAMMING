package com.group_7.library_management.notifications

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.group_7.library_management.BuildConfig
import com.group_7.library_management.data.remote.api.BookApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object BookAvailabilityNotifier {
    private const val PREFS = "book_availability_notifications"
    private const val IDS = "subscribed_book_ids"
    private const val WORK_NAME = "check_book_availability"

    fun subscribe(context: Context, bookId: String, bookTitle: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val ids = prefs.getStringSet(IDS, emptySet()).orEmpty().toMutableSet()
        ids += bookId
        prefs.edit()
            .putStringSet(IDS, ids)
            .putString("title_$bookId", bookTitle)
            .apply()

        val request = PeriodicWorkRequestBuilder<BookAvailabilityWorker>(15, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    internal fun preferences(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    internal const val idsKey = IDS
}

class BookAvailabilityWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val prefs = BookAvailabilityNotifier.preferences(applicationContext)
        val trackedIds = prefs.getStringSet(BookAvailabilityNotifier.idsKey, emptySet())
            .orEmpty().toMutableSet()
        if (trackedIds.isEmpty()) return Result.success()

        return try {
            val api = Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookApi::class.java)
            val books = api.getLatestBooks(50)
            val available = books.filter {
                it.id.toString() in trackedIds && it.availableQuantity > 0
            }
            available.forEach { book ->
                val id = book.id.toString()
                val title = prefs.getString("title_$id", book.title) ?: book.title
                LibraryNotificationManager.showBookAvailable(applicationContext, id, title)
                trackedIds.remove(id)
            }
            prefs.edit().putStringSet(BookAvailabilityNotifier.idsKey, trackedIds).apply()
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}
