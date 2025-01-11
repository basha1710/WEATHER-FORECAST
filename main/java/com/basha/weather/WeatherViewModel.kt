package com.basha.weather

import WeatherModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.basha.weather.api.*

class WeatherViewModel : ViewModel() {

    private val weatherApi: WeatherApi = RetrofitInstance.weatherApi

    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponse<WeatherModel>> = _weatherResult

    fun getData(city: String, latitude: String, longitude: String) {
        _weatherResult.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val currentWeatherResponse = weatherApi.getCurrentWeather(Constant.apiKey, city)
                val weatherForecastResponse = weatherApi.getWeatherForecast(Constant.apiKey, city)

                if (currentWeatherResponse.isSuccessful && weatherForecastResponse.isSuccessful) {
                    val currentWeather = currentWeatherResponse.body()
                    val weatherForecast = weatherForecastResponse.body()

                    if (currentWeather != null && weatherForecast != null) {
                        // Combine current weather with forecast
                        val combinedData = weatherForecast.copy(current = currentWeather.current)
                        _weatherResult.value = NetworkResponse.Success(combinedData)
                    } else {
                        _weatherResult.value = NetworkResponse.Error("No data available")
                    }
                } else {
                    _weatherResult.value = NetworkResponse.Error(
                        "API error: ${currentWeatherResponse.code()} - ${currentWeatherResponse.message()}"
                    )
                }
            } catch (e: Exception) {
                _weatherResult.value = NetworkResponse.Error("Exception: ${e.localizedMessage}")
            }
        }
    }
}
