package com.fiserv.commercehub.androidttp.security

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Manages background periodic work tasks.
 *
 * This object invokes background tasks to run with specified delay.
 *
 * @function startPeriodicWorkManager: schedules a periodic work request.
 * @param context: the application context used to initialize the WorkManager.
 */
object BackgroundWorkManager {

    fun startPeriodicWorkManager(context: Context) {
        val periodicWorkRequest = PeriodicWorkRequestBuilder<PatchWorker>(1, TimeUnit.HOURS)
            .setInitialDelay(30, TimeUnit.SECONDS)
            .addTag("PatchWorkerManager")
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "PatchWorkerManager",
                ExistingPeriodicWorkPolicy.UPDATE,
                periodicWorkRequest
            )
    }
}