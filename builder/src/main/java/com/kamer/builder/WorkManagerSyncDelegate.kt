package com.kamer.builder

import android.content.Context
import easydone.core.domain.SyncDelegate
import kotlinx.coroutines.flow.Flow

class WorkManagerSyncDelegate(private val context: Context) : SyncDelegate {
    override fun isSyncing(): Flow<Boolean> = WorkScheduler.getIsSyncingStatus(context)

    override fun initiateSync() {
        WorkScheduler.scheduleOneTimeSync(context)
    }
}