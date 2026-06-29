package com.example.finalproject.data.network

import com.example.finalproject.data.network.dto.OpenLibrarySearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): OpenLibrarySearchResponse
}