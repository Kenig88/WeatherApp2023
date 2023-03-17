package com.example.weatherapp2023.adapters

data class WeatherModel( //3
    val city: String,
    val time: String,
    val condition: String,
    val icon: String,
    val maxTemp: String,
    val currentTemp: String,
    val minTemp: String,
    val hoursArray: String
)
