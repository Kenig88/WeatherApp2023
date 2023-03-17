package com.example.weatherapp2023.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp2023.R
import com.example.weatherapp2023.databinding.ActivityMainBinding
import com.example.weatherapp2023.fragments.MainFragment

class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager //1.1
            .beginTransaction()
            .replace(R.id.placeHolder, MainFragment.newInstance())
            .commit()
    }
}