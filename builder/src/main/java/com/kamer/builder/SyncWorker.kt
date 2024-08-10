package com.kamer.builder

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import easydone.core.domain.Synchronizer
import easydone.widget.updateWidget

class SyncWorker(
    private val appContext: Context,
    workerParams: WorkerParameters,
    private val synchronizer: Synchronizer
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        synchronizer.sync()
        updateWidget(appContext)
        return Result.success()
    }
}