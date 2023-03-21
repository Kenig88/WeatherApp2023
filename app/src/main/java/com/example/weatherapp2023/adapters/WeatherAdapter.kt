package com.example.weatherapp2023.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp2023.R
import com.example.weatherapp2023.databinding.WeatherModelItemBinding
import com.squareup.picasso.Picasso


class WeatherAdapter : ListAdapter<WeatherModel, WeatherAdapter.Holder>(Comparator()) { //7
    class Holder(view: View) : RecyclerView.ViewHolder(view){
        private val binding = WeatherModelItemBinding.bind(view)

        fun bind(wM: WeatherModel) = with(binding){
            tvDate.text = wM.time
            tvCondition.text = wM.condition
            tvTemp.text = wM.currentTemp.ifEmpty { "${wM.maxTemp}°/${wM.minTemp}°" }
            Picasso.get().load("https:" + wM.icon).into(im)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.weather_model_item, parent, false)
        return Holder(view)
    }
    override fun onBindViewHolder(holder: Holder, position: Int) {
       holder.bind(getItem(position))
    }

    class Comparator : DiffUtil.ItemCallback<WeatherModel>(){
        override fun areItemsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }
    }
}