package easydone.core.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class SyncScheduler(
    private val localDataSource: LocalDataSource,
    private val syncDelegate: SyncDelegate,
    scope: CoroutineScope
) {

    init {
        localDataSource.observeChangesCount()
            .onEach { if (it > 0L) initiateSync() }
            .launchIn(scope)
    }

    fun isSyncing(): Flow<Boolean> = syncDelegate.isSyncing()

    fun observeChanges(): Flow<Long> = localDataSource.observeChangesCount()

    fun initiateSync() {
        syncDelegate.initiateSync()
    }
}
