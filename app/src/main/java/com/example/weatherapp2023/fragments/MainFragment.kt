package com.example.weatherapp2023.fragments

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp2023.adapters.MainViewModel
import com.example.weatherapp2023.adapters.ViewPagerAdapter
import com.example.weatherapp2023.adapters.WeatherModel
import com.example.weatherapp2023.databinding.FragmentMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.json.JSONObject

const val API_KEY = "7bef3a523d2a4e8abaa00717231503" //8.1
class MainFragment : Fragment() { //1
    private var fragmentList = listOf(HoursFragment.newInstance(), DaysFragment.newInstance()) //5.1
    private val tabList = listOf("Hours", "Days") //5.4
    private lateinit var binding: FragmentMainBinding
    private val model: MainViewModel by activityViewModels() //9
    private lateinit var permissionLauncher: ActivityResultLauncher<String> //4.2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //4.1
        super.onViewCreated(view, savedInstanceState)
        checkPermission() //4.5
        initViewPager() //5.3
        requestWeatherData("Moscow") //8.2
        updateMainCard() //9.3 (эта функция может запускаться в каком угодно порядке)
    }

    private fun initViewPager() = with(binding){ //5.2
        val adapter = ViewPagerAdapter(activity as FragmentActivity, fragmentList)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager){
                tab, position -> tab.text = tabList[position]
        }.attach()
    }

    private fun permissionListener(){ //4.3
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            Toast.makeText(activity, "Permission is $it", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPermission(){ //4.4
        if(!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)){
            permissionListener()
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun requestWeatherData(city: String){ //8
        val url = "https://api.weatherapi.com/v1/forecast.json?key=" +
                API_KEY +
                "&q=" +
                city +
                "&days=" +
                "3" +
                "&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.GET,
            url,
            {
                response -> parseWeatherData(response) //8.5
            },
            {
                error -> Log.d("MyLog", "Error: $error")
            },
        )
        queue.add(request)
    }

    private fun parseWeatherData(result: String){ //8.4
        val mainObject = JSONObject(result)
        val parseListDays = parseDays(mainObject) //8.7
        parseMainCard(mainObject, parseListDays[0])
    }

    private fun parseMainCard(mainObject: JSONObject, weatherItem: WeatherModel){ //8.3
        val weather = WeatherModel(
            mainObject.getJSONObject("location").getString("name"),
            mainObject.getJSONObject("current").getString("last_updated"),
            mainObject.getJSONObject("current")
                .getJSONObject("condition").getString("text"),
            mainObject.getJSONObject("current")
                .getJSONObject("condition").getString("icon"),
            weatherItem.maxTemp,
            mainObject.getJSONObject("current").getString("temp_c"),
            weatherItem.minTemp,
            weatherItem.hoursArray
        )
        model.liveDataMainCard.value = weather //9.1
    }

    private fun updateMainCard() = with(binding){ //9.2
        model.liveDataMainCard.observe(viewLifecycleOwner){
            val maxMinTemp = "${it.maxTemp}° / ${it.minTemp}°"
            tvDate.text = it.time
            tvCity.text = it.city
            tvCurrentTemp.text = it.currentTemp + "°C"
            tvCondition.text = it.condition
            tvMaxMin.text = maxMinTemp
            Picasso.get().load("https:" + it.icon).into(imWeather)
        }
    }

    private fun parseDays(mainObject: JSONObject): List<WeatherModel>{ //8.6
        val list = ArrayList<WeatherModel>()
        val cityName = mainObject.getJSONObject("location").getString("name")
        val daysArray = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
        for(i in 0 until daysArray.length()){
            val day = daysArray[i] as JSONObject
            val weatherItem = WeatherModel(
                cityName,
                day.getString("date"),
                day.getJSONObject("day").getJSONObject("condition").getString("text"),
                day.getJSONObject("day").getJSONObject("condition").getString("icon"),
                day.getJSONObject("day").getString("maxtemp_c"),
                "",
                day.getJSONObject("day").getString("mintemp_c"),
                day.getJSONArray("hour").toString()
            )
            list.add(weatherItem)
        }
        model.liveDataDaysList.value = list
        return list
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}