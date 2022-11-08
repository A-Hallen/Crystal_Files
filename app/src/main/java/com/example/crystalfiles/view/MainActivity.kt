package com.example.crystalfiles.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.crystalfiles.databinding.ActivityMainBinding
import com.example.crystalfiles.model.Permissions
import com.example.crystalfiles.testing.Test

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Permissions(this)
        Test(this).test()


    }
}