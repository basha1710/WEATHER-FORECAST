data class WeatherModel(
    val current: CurrentWeather?,
    val forecast: Forecast?
)

data class CurrentWeather(
    val temp_c: Float?,
    val feelslike_c: Float?,
    val condition: WeatherCondition?,
    val uv: String?,
    val vis_km: String?,
    val pressure_mb: String?,
    val humidity: String?,
    val wind_kph: String?,
    val wind_dir: String?,
    val precip_mm: String?,
    val rain: Any?,
    val snow: Any?,
    val last_updated: Any?
)



data class WeatherCondition(
    val text: String?,
    val icon: String?
)

data class Forecast(
    val forecastday: List<ForecastDay>?
)

data class ForecastDay(
    val date: String?,
    val day: ForecastDayDetails?,
    val hour: List<HourlyWeather>?,
    val astro: Astro?
)

data class Astro(
    val sunrise: String?,
    val sunset: String?,
    val moon_phase: String?
)


data class ForecastDayDetails(
    val avgtemp_c: Float?,
    val condition: WeatherCondition?,
    val maxtemp_c: String?,
    val mintemp_c: String?,
    val avgvis_km: String?,
    val avghumidity: String?,
    val maxwind_kph: String?,
    val totalprecip_mm: String?,
    val uv: String?

)

data class HourlyWeather(
    val time: String?,
    val temp_c: Float?,
    val condition: WeatherCondition?
)
