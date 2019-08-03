package easydone.core.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import timber.log.Timber
import timber.log.error

fun <T> Flow<T>.logErrors(): Flow<T> = catch { Timber.error(it) { it.localizedMessage } }
