package com.basha.weather.api

import WeatherModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    // API request for current weather
    @GET("v1/current.json")
    suspend fun getCurrentWeather(
        @Query("key") apikey: String, // API key for authentication
        @Query("q") city: String      // Query parameter for city name
    ): Response<WeatherModel>        // Response wrapping the WeatherModel

    // API request for weather forecast
    @GET("v1/forecast.json")
    suspend fun getWeatherForecast(
        @Query("key") apikey: String, // API key for authentication
        @Query("q") city: String,     // Query parameter for city name
        @Query("days") days: Int = 7  // Number of forecast days (default is 7)
    ): Response<WeatherModel>        // Response wrapping the WeatherModel
}
