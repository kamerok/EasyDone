package easydone.core.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.error


class SyncScheduler(
    private val synchronizer: Synchronizer,
    private val localDataSource: LocalDataSource,
    private val scope: CoroutineScope = GlobalScope
) {

    private val syncProgressState = MutableStateFlow(false)
    private var syncJob: Job? = null

    init {
        localDataSource.observeChangesCount()
            .onEach { if (it > 0L) initiateSync() }
            .launchIn(scope)
    }

    fun isSyncing(): Flow<Boolean> = syncProgressState

    fun observeChanges(): Flow<Long> = localDataSource.observeChangesCount()

    fun initiateSync() {
        val currentJob = syncJob
        if (currentJob == null || !currentJob.isActive) {
            syncJob = syncWithProgress().also {
                it.invokeOnCompletion { syncJob = null }
            }
        }
    }

    private fun syncWithProgress(): Job = scope.launch {
        syncProgressState.value = true
        try {
            synchronizer.sync()
        } catch (e: Exception) {
            Timber.error(e) { "sync error" }
        } finally {
            syncProgressState.value = false
        }
    }
}
