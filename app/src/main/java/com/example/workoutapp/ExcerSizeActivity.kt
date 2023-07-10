package com.example.workoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.workoutapp.databinding.ActivityExcerSizeBinding

class ExcerSizeActivity : AppCompatActivity() {
    private  var binding: ActivityExcerSizeBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityExcerSizeBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarExcerSize)
        if(supportActionBar!=null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarExcerSize?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()        }
    }
}