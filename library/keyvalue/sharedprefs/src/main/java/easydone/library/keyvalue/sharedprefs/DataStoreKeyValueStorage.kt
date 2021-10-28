package easydone.library.keyvalue.sharedprefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import easydone.library.keyvalue.KeyValueStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking


class DataStoreKeyValueStorage(private val dataStore: DataStore<Preferences>) : KeyValueStorage {

    override fun putString(key: String, value: String) {
        runBlocking {
            dataStore.edit { it[stringPreferencesKey(key)] = value }
        }
    }

    override fun getString(key: String): String? = runBlocking {
        dataStore.data.map { it[stringPreferencesKey(key)] }.first()
    }

    override fun getString(key: String, default: String): String = runBlocking {
        dataStore.data.map { it[stringPreferencesKey(key)] ?: default }.first()
    }

    override fun contains(key: String): Boolean = runBlocking {
        dataStore.data.map { it.contains(stringPreferencesKey(key)) }.first()
    }

    override fun clear() {
        runBlocking {
            dataStore.edit { it.clear() }
        }
    }

}
