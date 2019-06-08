package com.kamer.easydone

import android.content.Context
import androidx.core.content.edit


class LoginHolder(context: Context) {

    private val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit {
            putString(TOKEN, token)
        }
    }

    fun hasToken(): Boolean = prefs.contains(TOKEN)

    companion object {
        private const val TOKEN = "TOKEN"
    }
}