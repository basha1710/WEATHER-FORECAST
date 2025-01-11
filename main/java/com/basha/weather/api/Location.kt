package com.basha.weather.api

import com.google.gson.annotations.SerializedName


data class Location(
    val country: String,
    val lat: String,
    val localtime: String,
    val localtime_epoch: String,
    val lon: String,
    val name: String,
    val region: String,
    val city: String,
    val tz_id: String,
    val latitude: String,
    val longitude: String,
    val country_code: String,
    val population: String,


)
