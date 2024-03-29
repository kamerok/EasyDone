package easydone.service.trello

import easydone.library.keyvalue.KeyValueStorage


internal class AuthInfoHolder(private val storage: KeyValueStorage) {

    suspend fun getToken(): String? = storage.getString(TOKEN)

    suspend fun putToken(token: String) = storage.putString(TOKEN, token)

    suspend fun getBoardId(): String? = storage.getString(BOARD_ID)

    suspend fun putBoardId(id: String) = storage.putString(BOARD_ID, id)

    suspend fun getInboxListId(): String? = storage.getString(INBOX_LIST_ID)

    suspend fun putInboxListId(id: String) = storage.putString(INBOX_LIST_ID, id)

    suspend fun getTodoListId(): String? = storage.getString(TO_DO_LIST_ID)

    suspend fun putTodoListId(id: String) = storage.putString(TO_DO_LIST_ID, id)

    suspend fun getWaitingListId(): String? = storage.getString(WAITING_LIST_ID)

    suspend fun putWaitingListId(id: String) = storage.putString(WAITING_LIST_ID, id)

    suspend fun getProjectsListId(): String? = storage.getString(PROJECTS_LIST_ID)

    suspend fun putProjectsListId(id: String) = storage.putString(PROJECTS_LIST_ID, id)

    suspend fun getMaybeListId(): String? = storage.getString(MAYBE_LIST_ID)

    suspend fun putMaybeListId(id: String) = storage.putString(MAYBE_LIST_ID, id)

    suspend fun getUrgentLabelId(): String? = storage.getString(URGENT_LABEL_ID)

    suspend fun putUrgentLabelId(id: String) = storage.putString(URGENT_LABEL_ID, id)

    suspend fun getImportantLabelId(): String? = storage.getString(IMPORTANT_LABEL_ID)

    suspend fun putImportantLabelId(id: String) = storage.putString(IMPORTANT_LABEL_ID, id)

    suspend fun clear() = storage.clear()

    companion object {
        private const val TOKEN = "token"
        private const val BOARD_ID = "board_id"
        private const val INBOX_LIST_ID = "inbox_list_id"
        private const val TO_DO_LIST_ID = "todo_list_id"
        private const val WAITING_LIST_ID = "waiting_list_id"
        private const val MAYBE_LIST_ID = "maybe_list_id"
        private const val PROJECTS_LIST_ID = "projects_list_id"
        private const val URGENT_LABEL_ID = "urgent_label_id"
        private const val IMPORTANT_LABEL_ID = "important_label_id"
    }

}
