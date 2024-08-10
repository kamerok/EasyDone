package easydone.core.domain

import kotlinx.coroutines.flow.Flow

interface SyncDelegate {

    fun isSyncing(): Flow<Boolean>

    fun initiateSync()

}