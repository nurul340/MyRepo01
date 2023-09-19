package com.nc.i2gpt.api

import com.nc.i2gpt.models.GeneratedAnswer
import com.nc.i2gpt.models.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("completions")
    suspend fun getPrompt(@Body body: RequestBody) : GeneratedAnswer
}