package com.example.weatherapp2023.adapters

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() { //2
    val liveDataMainCard = MutableLiveData<String>()
    val liveDataRecyclerList = MutableLiveData<List<String>>()
}