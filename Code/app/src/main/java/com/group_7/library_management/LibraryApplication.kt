package com.group_7.library_management

import android.app.Application
import com.group_7.library_management.notifications.LibraryNotificationManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LibraryApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        LibraryNotificationManager.createNotificationChannels(this)
    }
}
