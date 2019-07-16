package easydone.core.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import timber.log.Timber
import timber.log.error

fun <T> Flow<T>.logErrors(): Flow<T> = catch { Timber.error(it) { it.localizedMessage } }

inline fun <T> Flow<T>.onEachMain(crossinline action: suspend (T) -> Unit): Flow<T> =
    onEach { withContext(Dispatchers.Main) { action(it) } }