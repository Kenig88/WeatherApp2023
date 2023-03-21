package com.example.weatherapp2023.adapters

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() { //2
    val liveDataMainCard = MutableLiveData<WeatherModel>()
    val liveDataDaysList = MutableLiveData<List<WeatherModel>>()
}