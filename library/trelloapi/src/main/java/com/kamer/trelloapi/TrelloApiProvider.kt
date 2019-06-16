package com.kamer.trelloapi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object TrelloApiProvider {

    val api: TrelloApi = Retrofit.Builder()
        .baseUrl("https://trello.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(com.kamer.trelloapi.TrelloApi::class.java)

}