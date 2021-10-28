package easydone.library.keyvalue


interface KeyValueStorage {

    suspend fun putString(key: String, value: String)

    suspend fun getString(key: String): String?

    suspend fun getString(key: String, default: String): String

    suspend fun contains(key: String): Boolean

    suspend fun clear()

}
