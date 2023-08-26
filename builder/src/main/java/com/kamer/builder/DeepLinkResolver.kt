package com.kamer.builder

import android.content.Intent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow


class DeepLinkResolver {

    private val tokenSharedFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)

    fun resolveIntent(intent: Intent) {
        if (intent.data?.host == "auth") {
            val token = intent.data?.fragment?.substringAfter('=') ?: ""
            tokenSharedFlow.tryEmit(token)
        }
    }

    fun observeToken(): Flow<String> = tokenSharedFlow

}
