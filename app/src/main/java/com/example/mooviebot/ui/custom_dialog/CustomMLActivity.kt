package com.example.mooviebot.ui.custom_dialog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mooviebot.R
import com.example.mooviebot.databinding.ActivityCustomMlBinding




class CustomMLActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCustomMlBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        binding = ActivityCustomMlBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)

        val result = intent.getStringExtra("ML RESULT")

        binding.tvResults.text = result

    }

}