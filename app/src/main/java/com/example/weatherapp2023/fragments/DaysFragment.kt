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
import com.example.weatherapp2023.databinding.FragmentDaysBinding


class DaysFragment() : Fragment(), WeatherAdapter.Listener { //6.1//12.2
    private lateinit var binding: FragmentDaysBinding
    private lateinit var adapter: WeatherAdapter //11
    private val model: MainViewModel by activityViewModels() //11.4

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDaysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //11.1
        super.onViewCreated(view, savedInstanceState)
        initRcView() //11.3
        updateDaysFragment() //11.6
    }

    private fun initRcView() = with(binding){ //11.2
        rcViewDays.layoutManager = LinearLayoutManager(activity as AppCompatActivity)
        adapter = WeatherAdapter(this@DaysFragment) //12.4
        rcViewDays.adapter = adapter
    }

    private fun updateDaysFragment() = with(binding){ //11.5
        model.liveDataDaysList.observe(viewLifecycleOwner){
            adapter.submitList(it)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DaysFragment()
    }

    override fun onClick(wM: WeatherModel) { //12.3
        model.liveDataMainCard.value = wM //12.8
    }
}