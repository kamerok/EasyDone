package com.kamer.builder

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object WorkScheduler {

    private const val SYNC_WORK = "sync"
    private const val PERIODIC_SYNC_WORK = "periodic_sync"

    fun scheduleOneTimeSync(context: Context) {
        val syncRequest: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<SyncWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        WorkManager
            .getInstance(context)
            .enqueueUniqueWork(
                SYNC_WORK,
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                syncRequest
            )
    }

    fun schedulePeriodicSync(context: Context) {
        val currentTime = LocalDateTime.now()
        var targetTime = LocalDateTime.now().withHour(1).withMinute(0).withSecond(0)
        if (targetTime.isBefore(currentTime)) {
            targetTime = targetTime.plusDays(1)
        }

        val delay = Duration.between(currentTime, targetTime)
        val delayInSeconds = delay.seconds

        val syncRequest: PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<SyncWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(delayInSeconds, TimeUnit.SECONDS)
                .build()
        WorkManager
            .getInstance(context)
            .enqueueUniquePeriodicWork(
                PERIODIC_SYNC_WORK,
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                syncRequest
            )
    }

    fun getIsSyncingStatus(context: Context): Flow<Boolean> {
        return combine(
            observeOneTimeSyncIsRunning(context),
            observePeriodicSyncIsRunning(context)
        ) { oneTimeSyncIsRunning, periodicSyncIsRunning ->
            oneTimeSyncIsRunning || periodicSyncIsRunning
        }
    }

    private fun observeOneTimeSyncIsRunning(context: Context) = WorkManager.getInstance(context)
        .getWorkInfosForUniqueWorkFlow(SYNC_WORK)
        .map { workInfos ->
            workInfos.any { it.state == WorkInfo.State.RUNNING }
        }

    private fun observePeriodicSyncIsRunning(context: Context) = WorkManager.getInstance(context)
        .getWorkInfosForUniqueWorkFlow(PERIODIC_SYNC_WORK)
        .map { workInfos ->
            workInfos.any { it.state == WorkInfo.State.RUNNING }
        }

}