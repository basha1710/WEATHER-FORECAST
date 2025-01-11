package com.basha.weather

import ForecastDay
import WeatherModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.basha.weather.api.NetworkResponse
import com.google.android.gms.location.LocationServices
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun WeatherPage(viewModel: WeatherViewModel = viewModel()) {
    var city by remember { mutableStateOf("Vijayawada") }
    var errorMessage by remember { mutableStateOf("") }
    var showMoreHourly by remember { mutableStateOf(false) }
    var showMoreWeekly by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf<ForecastDay?>(null) }

    val weatherResult by viewModel.weatherResult.observeAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7B68EE)) // Violet background
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Basha Weather",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = city,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        SearchBar(city = city, onCityChange = { city = it }, onSearch = {
            if (city.isNotEmpty()) {
                errorMessage = ""
                viewModel.getData(city, "", "")
            } else {
                errorMessage = "Please enter a city."
            }
        })

        Spacer(modifier = Modifier.height(16.dp))

        // Display error message if any
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(8.dp)
            )
        }

        when (weatherResult) {
            is NetworkResponse.Loading -> CircularProgressIndicator(color = Color.White)
            is NetworkResponse.Error -> {
                errorMessage = "Unable to fetch weather data. Try again later."
            }
            is NetworkResponse.Success<*> -> {
                val data = (weatherResult as NetworkResponse.Success<*>).data as? WeatherModel
                data?.let {
                    CurrentWeatherSection(data)
                    AdditionalWeatherDetails(data)
                    HourlyForecastSection(data.forecast?.forecastday, showMoreHourly) { showMoreHourly = !showMoreHourly }
                    WeeklyForecastSection(data.forecast?.forecastday, showMoreWeekly) { showMoreWeekly = !showMoreWeekly }
                    selectedDay?.let {
                        DailyForecastDetails(it)
                    }
                }
            }
            null -> Text(text = "Enter a city to see the weather", color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(city: String, onCityChange: (String) -> Unit, onSearch: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = city,
            onValueChange = onCityChange,
            label = { Text("Enter city") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),  // Set text color here
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.Gray
            )
        )
        IconButton(onClick = onSearch) {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Color.White)
        }
    }
}

@Composable
fun CurrentWeatherSection(data: WeatherModel) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "${data.current?.temp_c}°C",
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Feels like ${data.current?.feelslike_c}°C",
            fontSize = 18.sp,
            color = Color.White
        )
        Text(
            text = data.current?.condition?.text ?: "Condition not available",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
        AsyncImage(
            model = "https:${data.current?.condition?.icon}",
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
    }
}

@Composable
fun AdditionalWeatherDetails(data: WeatherModel) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text("Additional Weather Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)

        Text("Temperature: ${data.current?.temp_c}°C", color = Color.White)
        Text("Feels Like: ${data.current?.feelslike_c}°C", color = Color.White)
        Text("Condition: ${data.current?.condition?.text}", color = Color.White)
        Text("Humidity: ${data.current?.humidity}%", color = Color.White)
        Text("Pressure: ${data.current?.pressure_mb} hPa", color = Color.White)
        Text("Visibility: ${data.current?.vis_km} km", color = Color.White)
        Text("UV Index: ${data.current?.uv}", color = Color.White)
        Text("Wind Speed: ${data.current?.wind_kph} km/h", color = Color.White)
        Text("Wind Direction: ${data.current?.wind_dir}", color = Color.White)
        Text("Precipitation: ${data.current?.precip_mm} mm", color = Color.White)
        Text("Rain: ${data.current?.rain}", color = Color.White)
        Text("Snow: ${data.current?.snow}", color = Color.White)
        Text(text = "Sunrise: ${data.forecast?.forecastday?.firstOrNull()?.astro?.sunrise}", color = Color.White)
        Text("Sunset: ${data.forecast?.forecastday?.firstOrNull()?.astro?.sunset}", color = Color.White)
        Text("Moon Phase: ${data.forecast?.forecastday?.firstOrNull()?.astro?.moon_phase}", color = Color.White)
    }
}

@Composable
fun HourlyForecastSection(forecastDays: List<ForecastDay>?, showMore: Boolean, onToggle: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Hourly Forecast",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(8.dp)
        )
        forecastDays?.firstOrNull()?.hour?.take(if (showMore) 24 else 12)?.forEach { hour ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = hour.time ?: "N/A", color = Color.White)
                Text(text = "${hour.temp_c}°C", color = Color.White)
                Text(text = hour.condition?.text ?: "N/A", color = Color.White)
            }
        }

        Button(
            onClick = onToggle,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = if (showMore) "Show Less" else "View More")
        }
    }
}

@Composable
fun WeeklyForecastSection(forecastDays: List<ForecastDay>?, showMore: Boolean, onToggle: () -> Unit) {
    var selectedDay by remember { mutableStateOf<ForecastDay?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Weekly Forecast",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(8.dp)
        )
        forecastDays?.take(if (showMore) 7 else 3)?.forEach { day ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { selectedDay = day },
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = day.date ?: "N/A", color = Color.White)
                Text(text = "${day.day?.avgtemp_c}°C", color = Color.White)
            }
        }

        Button(
            onClick = onToggle,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = if (showMore) "Show Less" else "View More")
        }
    }

    selectedDay?.let {
        DailyForecastDetails(it)
    }
}

@Composable
fun DailyForecastDetails(day: ForecastDay) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = "Details for ${day.date}",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text("Max Temp: ${day.day?.maxtemp_c}°C", color = Color.White)
        Text("Min Temp: ${day.day?.mintemp_c}°C", color = Color.White)
        Text("Humidity: ${day.day?.avghumidity}%", color = Color.White)
        Text("Condition: ${day.day?.condition?.text}", color = Color.White)

        // Show different time-based conditions (morning, afternoon, etc.)
        val timeOfDay = getTimeOfDay(day.date)
        Text("Time of Day: $timeOfDay", color = Color.White)
    }
}

// Helper function to categorize times of the day
fun getTimeOfDay(date: String?): String {
    // Here, a simple example for categorizing by time ranges
    return when (date) {
        "Morning" -> "Sunny"
        "Afternoon" -> "Partly Cloudy"
        "Evening" -> "Light Sun"
        "Night" -> "Moonlit"
        else -> "Unknown"
    }
}
