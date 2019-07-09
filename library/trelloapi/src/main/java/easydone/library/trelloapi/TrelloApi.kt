package easydone.library.trelloapi

import easydone.library.trelloapi.model.*
import retrofit2.http.*


interface TrelloApi {

    companion object {
        const val API_KEY = "98c9ac26156a960889eb42586aa1bcd7"
    }

    @GET("members/me?fields=none&boards=open&board_fields=id,name")
    suspend fun boards(@Query("key") apiKey: String, @Query("token") token: String): NestedBoards

    @GET("boards/{id}?fields=none&lists=open&cards=visible&list_fields=id&card_fields=id,idList,name,desc,pos")
    suspend fun boardData(
        @Path("id") boardId: String,
        @Query("key") apiKey: String,
        @Query("token") token: String
    ): NestedBoard

    @PUT("cards/{id}")
    suspend fun editCard(
        @Path("id") id: String,
        @Query("key") apiKey: String,
        @Query("token") token: String,
        @Query("name") name: String?,
        @Query("desc") desc: String?,
        @Query("closed") closed: Boolean?,
        @Query("idList") listId: String?
    ): Card

    @POST("cards")
    suspend fun postCard(
        @Query("idList") listId: String,
        @Query("name") name: String,
        @Query("desc") desc: String?,
        @Query("pos") pos: String = "bottom",
        @Query("key") apiKey: String,
        @Query("token") token: String
    ): Card

}