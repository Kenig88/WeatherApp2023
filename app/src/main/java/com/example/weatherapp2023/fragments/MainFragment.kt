package com.example.weatherapp2023.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp2023.adapters.DialogManager
import com.example.weatherapp2023.adapters.MainViewModel
import com.example.weatherapp2023.adapters.ViewPagerAdapter
import com.example.weatherapp2023.adapters.WeatherModel
import com.example.weatherapp2023.databinding.FragmentMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.json.JSONObject

const val API_KEY = "7bef3a523d2a4e8abaa00717231503" //8.1
class MainFragment : Fragment() { //1
    private lateinit var fusedLocationClient: FusedLocationProviderClient //13
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
        checkPermissionLocation() //4.5
        initViewPager() //5.3
        updateMainCard() //9.3 (эта функция может запускаться в каком угодно порядке)
    }

    override fun onResume() { //14.3
        super.onResume()
        checkLocation()
    }

    private fun initViewPager() = with(binding){ //5.2
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext()) //13.1

        val adapter = ViewPagerAdapter(activity as FragmentActivity, fragmentList)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager){
                tab, position -> tab.text = tabList[position]
        }.attach()

        ibSync.setOnClickListener{//13.3
            getLocation()
            tabLayout.selectTab(tabLayout.getTabAt(0))
            checkLocation() //14.2
        }
        ibSearch.setOnClickListener{ //15.2
            DialogManager.searchByCityName(requireContext(), object : DialogManager.Listener{
                override fun onClick(cityName: String?) {
                    if (cityName != null) {
                        requestWeatherData(cityName)
                    }
                }
            })
        }
    }

    private fun permissionListenerLocation(){ //4.3
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            Toast.makeText(activity, "Permission is $it", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPermissionLocation(){ //4.4
        if(!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)){
            permissionListenerLocation()
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun isLocationEnabled(): Boolean{ //13.1
        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun checkLocation(){ //14.1
        if(isLocationEnabled()){
            getLocation()
        } else {
            DialogManager.locationSettingsDialog(requireContext(), object : DialogManager.Listener {
                override fun onClick(cityName: String?) {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
        }
    }

    private fun getLocation(){ //13.2
        val canToken = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, canToken.token)
            .addOnCompleteListener{
                requestWeatherData("${it.result.latitude}, ${it.result.longitude}")
            }
    }

    private fun requestWeatherData(city: String){ //8
        val url = "https://api.weatherapi.com/v1/forecast.json?key=" +
                API_KEY +
                "&q=" +
                city +
                "&days=" +
                "10" +
                "&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.GET,
            url,
            {
                    response -> parseWeatherData(response) //8.4
            },
            {
                    error -> Log.d("MyLog", "Error: $error")
            },
        )
        queue.add(request)
    }

    private fun parseWeatherData(result: String){ //8.3
        val mainObject = JSONObject(result)
        val parseListDays = parseDays(mainObject) //8.6
        parseMainCard(mainObject, parseListDays[0])
    }

    private fun parseMainCard(mainObject: JSONObject, weatherItem: WeatherModel){ //8.2
        val weather = WeatherModel(
            mainObject.getJSONObject("location").getString("name"),
            mainObject.getJSONObject("current").getString("last_updated"),
            mainObject.getJSONObject("current")
                .getJSONObject("condition").getString("text"),
            mainObject.getJSONObject("current")
                .getJSONObject("condition").getString("icon"),
            weatherItem.maxTemp,
            mainObject.getJSONObject("current").getString("temp_c") + "°C",
            weatherItem.minTemp,
            weatherItem.hoursArray
        )
        model.liveDataMainCard.value = weather //9.1
    }

    private fun updateMainCard() = with(binding){ //9.2
        model.liveDataMainCard.observe(viewLifecycleOwner){
            val maxMinTemp = "${it.maxTemp}°C / ${it.minTemp}°C"
            tvDate.text = it.time
            tvCity.text = it.city
            tvCurrentTemp.text = it.currentTemp.ifEmpty { maxMinTemp }
            tvCondition.text = it.condition
            tvMaxMin.text = if(it.currentTemp.isEmpty()) "" else maxMinTemp
            Picasso.get().load("https:" + it.icon).into(imWeather)
        }
    }

    private fun parseDays(mainObject: JSONObject): List<WeatherModel>{ //8.5
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
                day.getJSONObject("day").getString("maxtemp_c").toFloat().toInt().toString(),
                "",
                day.getJSONObject("day").getString("mintemp_c").toFloat().toInt().toString(),
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