package easydone.core.network

import easydone.library.keyvalue.KeyValueStorage


class AuthInfoHolder(private val storage: KeyValueStorage) {

    fun getToken(): String? = storage.getString(TOKEN)

    fun putToken(token: String) = storage.putString(TOKEN, token)

    fun getBoardId(): String? = storage.getString(BOARD_ID)

    fun putBoardId(id: String) = storage.putString(BOARD_ID, id)

    fun getInboxListId(): String? = storage.getString(INBOX_LIST_ID)

    fun putInboxListId(id: String) = storage.putString(INBOX_LIST_ID, id)

    fun getTodoListId(): String? = storage.getString(TO_DO_LIST_ID)

    fun putTodoListId(id: String) = storage.putString(TO_DO_LIST_ID, id)

    fun getWaitingListId(): String? = storage.getString(WAITING_LIST_ID)

    fun putWaitingListId(id: String) = storage.putString(WAITING_LIST_ID, id)

    fun clear() = storage.clear()

    companion object {
        private const val TOKEN = "token"
        private const val BOARD_ID = "board_id"
        private const val INBOX_LIST_ID = "inbox_list_id"
        private const val TO_DO_LIST_ID = "todo_list_id"
        private const val WAITING_LIST_ID = "waiting_list_id"
    }

}