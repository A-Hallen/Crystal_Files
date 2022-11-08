package com.example.crystalfiles.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.crystalfiles.databinding.ActivityMainBinding
import com.example.crystalfiles.model.Permissions
import com.example.crystalfiles.testing.Test
import com.example.crystalfiles.view.leftpanel.NavDrawer
import com.example.crystalfiles.view.recyclerview.Recycler

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawer: DrawerLayout // This is the left navigation drawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Permissions(this)
        Test(this).test()

        NavDrawer(this)
        Recycler(this)

    }
}