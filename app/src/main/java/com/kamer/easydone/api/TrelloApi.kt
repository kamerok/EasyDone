package com.kamer.easydone.api

import retrofit2.http.GET
import retrofit2.http.Query


interface TrelloApi {

    @GET("1/members/me/")
    suspend fun me(@Query("key") apiKey: String, @Query("token") token: String): Any

}