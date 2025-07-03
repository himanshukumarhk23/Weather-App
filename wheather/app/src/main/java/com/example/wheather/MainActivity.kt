package com.example.wheather

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.wheather.databinding.ActivityMainBinding
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("London")
        setupSearchView()
    }

    private fun setupSearchView() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    fetchWeatherData(query.trim())
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun fetchWeatherData(location: String) {
        val call = retrofit.getWeatherData(
            location,
            "7288276f3f751756aaf554f13aebe511", // ✅ Replace with your actual API key
            "metric"
        )

        call.enqueue(object : Callback<wheatherapp> {
            override fun onResponse(call: Call<wheatherapp>, response: Response<wheatherapp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    updateUIWithWeatherData(location, responseBody)
                } else {
                    Toast.makeText(this@MainActivity, "City not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<wheatherapp>, t: Throwable) {
                Log.e("TAG", "API call failed: ${t.message}")
                Toast.makeText(this@MainActivity, "Failed to fetch data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUIWithWeatherData(location: String, data: wheatherapp) {
        val temperature = data.main.temp.toString()
        val humidity = data.main.humidity
        val windSpeed = data.wind.speed
        val sunRise = formatUnixTime(data.sys.sunrise.toLong())
        val sunSet = formatUnixTime(data.sys.sunset.toLong())
        val pressure = data.main.pressure
        val condition = data.weather.firstOrNull()?.main?.trim() ?: "Unknown"
        val maxTemp = data.main.temp_max
        val minTemp = data.main.temp_min

        binding.temp.text = "${temperature}°C"
        binding.wheather.text = condition
        binding.maxtemp.text = "Max Temp: ${maxTemp}°C"
        binding.mintemp.text = "Min Temp: ${minTemp}°C"
        binding.humidity00.text = "${humidity}%"
        binding.wind00.text = "${windSpeed} m/s"
        binding.sunrise00.text = sunRise
        binding.sunset00.text = sunSet
        binding.sea00.text = "${pressure} hPa"
        binding.sunnycondition.text = condition
        binding.day.text = SimpleDateFormat("EEEE", Locale.ENGLISH).format(Date())
        binding.date.text = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH).format(Date())
        binding.location.text = location

        changeImageAccordingToWeatherCondition(condition)
    }

    private fun formatUnixTime(time: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        return sdf.format(Date(time * 1000))  // ✅ Convert seconds to milliseconds
    }

    private fun changeImageAccordingToWeatherCondition(condition: String) {
        when (condition) {
            "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny)
                binding.lottieanimation.setAnimation(R.raw.sunnyanimation)
            }
            "Clouds" -> {
                binding.root.setBackgroundResource(R.drawable.cloudy)
                binding.lottieanimation.setAnimation(R.raw.cloudy)
            }
            "Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rainy)
                binding.lottieanimation.setAnimation(R.raw.rainy)
            }
            "Snow" -> {
                binding.root.setBackgroundResource(R.drawable.snow)
                binding.lottieanimation.setAnimation(R.raw.snow)
            }
            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny)
                binding.lottieanimation.setAnimation(R.raw.sunnyanimation)
            }
        }
        binding.lottieanimation.playAnimation()
    }
}
