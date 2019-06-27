package easydone.core.domain

import easydone.core.auth.AuthInfoHolder
import easydone.core.domain.model.Task
import easydone.library.trelloapi.TrelloApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class DomainRepository(
    private val authInfoHolder: AuthInfoHolder,
    private val trelloApi: TrelloApi
) {

    fun getTasks(): Flow<List<Task>> = flow {
        val boardId = authInfoHolder.getBoardId()!!
        val token = authInfoHolder.getToken()!!
        val lists = trelloApi.lists(boardId, TrelloApi.API_KEY, token)
        val cards = trelloApi.cards(boardId, TrelloApi.API_KEY, token).filter { it.idList == lists.first().id }
        emit(cards.map { Task(it.id, it.name) })
    }

}