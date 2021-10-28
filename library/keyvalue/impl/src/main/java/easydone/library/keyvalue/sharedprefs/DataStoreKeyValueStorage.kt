package easydone.library.keyvalue.sharedprefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import easydone.library.keyvalue.KeyValueStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


class DataStoreKeyValueStorage(private val dataStore: DataStore<Preferences>) : KeyValueStorage {

    override suspend fun putString(key: String, value: String) {
        dataStore.edit { it[stringPreferencesKey(key)] = value }
    }

    override suspend fun getString(key: String): String? =
        dataStore.data.map { it[stringPreferencesKey(key)] }.first()

    override suspend fun getString(key: String, default: String): String =
        dataStore.data.map { it[stringPreferencesKey(key)] ?: default }.first()

    override suspend fun contains(key: String): Boolean =
        dataStore.data.map { it.contains(stringPreferencesKey(key)) }.first()

    override suspend fun clear() {
        dataStore.edit { it.clear() }
    }

}
