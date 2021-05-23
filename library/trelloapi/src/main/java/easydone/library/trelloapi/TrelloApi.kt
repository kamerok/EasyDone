package easydone.library.trelloapi

import easydone.library.trelloapi.model.Card
import easydone.library.trelloapi.model.NestedBoard
import easydone.library.trelloapi.model.NestedBoards
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface TrelloApi {

    @GET("members/me?fields=none&boards=open&board_fields=id,name")
    suspend fun boards(@Query("key") apiKey: String, @Query("token") token: String): NestedBoards

    @GET("boards/{id}?fields=none&lists=open&cards=visible&labels=all&list_fields=id&card_fields=id,idList,name,desc,due,pos,idLabels&label_fields=id,name")
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
        @Query("due") due: String?,
        @Query("idList") listId: String?,
        @Query("idLabels") idLabels: String?
    ): Card

    @POST("cards")
    suspend fun postCard(
        @Query("idList") listId: String,
        @Query("name") name: String,
        @Query("desc") desc: String?,
        @Query("pos") pos: String = "bottom",
        @Query("key") apiKey: String,
        @Query("token") token: String,
        @Query("idLabels") idLabels: String?
    ): Card

    companion object {
        const val API_KEY = "98c9ac26156a960889eb42586aa1bcd7"

        fun build(debugInterceptor: Interceptor?): TrelloApi = Retrofit.Builder()
            .baseUrl("https://trello.com/1/")
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        }
                    )
                    .apply {
                        if (debugInterceptor != null) addInterceptor(debugInterceptor)
                    }
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrelloApi::class.java)
    }
}
