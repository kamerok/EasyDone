package easydone.core.auth

import easydone.library.keyvalue.KeyValueStorage


class AuthInfoHolder(private val storage: KeyValueStorage) {

    fun getToken(): String? = storage.getString(TOKEN)

    fun putToken(token: String) = storage.putString(TOKEN, token)

    fun getBoardId(): String? = storage.getString(BOARD_ID)

    fun putBoardId(id: String) = storage.putString(BOARD_ID, id)

    fun clear() = storage.clear()

    companion object {
        private const val TOKEN = "token"
        private const val BOARD_ID = "board_id"
    }

}