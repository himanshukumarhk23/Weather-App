package com.example.wheather

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("weather")
    fun getWeatherData(
        @Query("q") city: String,
        @Query("appid") appKey: String,
        @Query("units") units: String
    ): Call<wheatherapp>
}
