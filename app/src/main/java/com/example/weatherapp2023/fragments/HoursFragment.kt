package com.example.weatherapp2023.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp2023.adapters.MainViewModel
import com.example.weatherapp2023.adapters.WeatherAdapter
import com.example.weatherapp2023.adapters.WeatherModel
import com.example.weatherapp2023.databinding.FragmentHoursBinding
import org.json.JSONArray
import org.json.JSONObject

class HoursFragment : Fragment() { //6
    private lateinit var binding: FragmentHoursBinding
    private lateinit var adapter: WeatherAdapter //7.2
    private val model: MainViewModel by activityViewModels() //10

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHoursBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //7.1
        super.onViewCreated(view, savedInstanceState)
        initRcView() //7.4
    }

    private fun initRcView() = with(binding){ //7.3
        rcViewHours.layoutManager = LinearLayoutManager(activity as AppCompatActivity)
        adapter = WeatherAdapter(null) //12.5 (Listener)
        rcViewHours.adapter = adapter
        updateHoursFragment() //10.3
    }

    private fun parseHours(wM: WeatherModel): List<WeatherModel> { //10.1
        val hoursArray = JSONArray(wM.hoursArray)
        val hours = ArrayList<WeatherModel>()
        for (i in 0 until hoursArray.length()) {
            val json = hoursArray[i] as JSONObject
            val item = WeatherModel(
                wM.city,
                json.getString("time"),
                json.getJSONObject("condition").getString("text"),
                json.getJSONObject("condition").getString("icon"),
                "",
                json.getString("temp_c") + "°",
                "",
                ""
            )
            hours.add(item)
        }
        return hours
    }

    private fun updateHoursFragment() = with(binding){ //10.2
        model.liveDataMainCard.observe(viewLifecycleOwner){ //(нужен слушатель с MainCard)
            adapter.submitList(parseHours(it))
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = HoursFragment()
    }
}