package easydone.library.trelloapi

import easydone.library.trelloapi.model.Board
import easydone.library.trelloapi.model.Card
import easydone.library.trelloapi.model.CardList
import easydone.library.trelloapi.model.User
import retrofit2.http.*


interface TrelloApi {

    companion object {
        const val API_KEY = "98c9ac26156a960889eb42586aa1bcd7"
    }

    @GET("members/me/")
    suspend fun me(@Query("key") apiKey: String, @Query("token") token: String): User

    @GET("members/{id}/boards")
    suspend fun boards(@Path("id") id: String, @Query("key") apiKey: String, @Query("token") token: String): List<Board>

    @GET("boards/{id}/lists")
    suspend fun lists(@Path("id") boardId: String, @Query("key") apiKey: String, @Query("token") token: String): List<CardList>

    @GET("boards/{id}/cards")
    suspend fun cards(@Path("id") boardId: String, @Query("key") apiKey: String, @Query("token") token: String): List<Card>

    @GET("cards/{id}")
    suspend fun card(@Path("id") id: String, @Query("key") apiKey: String, @Query("token") token: String): Card

    @PUT("cards/{id}")
    suspend fun editCard(
        @Path("id") id: String,
        @Query("key") apiKey: String,
        @Query("token") token: String,
        @Query("name") name: String? = null,
        @Query("closed") closed: Boolean? = null,
        @Query("idList") listId: String? = null
    ): Card

    @POST("cards")
    suspend fun postCard(
        @Query("idList") listId: String,
        @Query("name") name: String,
        @Query("pos") pos: String = "bottom",
        @Query("key") apiKey: String,
        @Query("token") token: String
    )

}