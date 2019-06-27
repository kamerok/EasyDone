package easydone.core.domain

import easydone.core.auth.AuthInfoHolder
import easydone.core.domain.model.Task
import easydone.library.trelloapi.TrelloApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class DomainRepository(
    private val authInfoHolder: AuthInfoHolder,
    private val trelloApi: TrelloApi
) {

    fun getTasks(isInbox: Boolean): Flow<List<Task>> = flow {
        val filteredCards = coroutineScope {
            val boardId = authInfoHolder.getBoardId()!!
            val token = authInfoHolder.getToken()!!
            val lists = async { trelloApi.lists(boardId, TrelloApi.API_KEY, token) }
            val cards = async { trelloApi.cards(boardId, TrelloApi.API_KEY, token) }
            val listId = lists.await()[if (isInbox) 0 else 1].id
            cards.await().filter { it.idList == listId }
        }
        emit(filteredCards.map { Task(it.id, it.name) })
    }

}