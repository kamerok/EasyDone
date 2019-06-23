package easydone.library.keyvalue.sharedprefs

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import easydone.library.keyvalue.KeyValueStorage


class SharedPrefsKeyValueStorage(application: Application, name: String) : KeyValueStorage {

    private val prefs = application.getSharedPreferences(name, Context.MODE_PRIVATE)

    override fun putString(key: String, value: String) = prefs.edit { putString(key, value) }

    override fun getString(key: String): String? = prefs.getString(key, null)

    override fun getString(key: String, default: String): String = prefs.getString(key, default)!!

    override fun contains(key: String): Boolean = prefs.contains(key)

}