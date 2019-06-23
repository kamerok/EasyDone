package easydone.library.keyvalue


interface KeyValueStorage {

    fun putString(key: String, value: String)

    fun getString(key: String): String?

    fun getString(key: String, default: String): String

    fun contains(key: String): Boolean

}