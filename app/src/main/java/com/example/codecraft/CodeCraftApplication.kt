package com.example.codecraft

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.codecraft.data.AppContainer
import com.example.codecraft.data.DefaultAppContainer
import com.example.codecraft.util.NotificationWorker
import java.util.concurrent.TimeUnit

class CodeCraftApplication : Application() {
    /**
     * The dependency injection container.
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        scheduleNotificationWorker()
    }

    private fun scheduleNotificationWorker() {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            24, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "codecraft_notification_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
