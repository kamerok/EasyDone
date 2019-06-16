package com.kamer.trelloapi

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface TrelloApi {

    @GET("members/me/")
    suspend fun me(@Query("key") apiKey: String, @Query("token") token: String): Any

    @GET("members/{id}/boards")
    suspend fun boards(@Path("id") id: String, @Query("key") apiKey: String, @Query("token") token: String): Any

}