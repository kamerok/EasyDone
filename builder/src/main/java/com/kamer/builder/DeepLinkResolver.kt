package com.kamer.builder

import android.content.Intent
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow


class DeepLinkResolver {

    private val tokenChannel = BroadcastChannel<String>(1)

    fun resolveIntent(intent: Intent) {
        if (intent.data?.host == "auth") {
            val token = intent.data?.fragment?.substringAfter('=') ?: ""
            tokenChannel.trySend(token)
        }
    }

    fun observeToken(): Flow<String> = tokenChannel.asFlow()

}
