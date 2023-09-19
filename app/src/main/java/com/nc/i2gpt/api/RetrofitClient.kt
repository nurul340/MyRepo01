package com.nc.i2gpt.api

import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    const val BASE_URL = "https://api.openai.com/v1/chat/"

    private lateinit var instance: Retrofit;

    fun getClient(): Retrofit{
        if(!this::instance.isInitialized){
            instance = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }
        return instance;
    }


}