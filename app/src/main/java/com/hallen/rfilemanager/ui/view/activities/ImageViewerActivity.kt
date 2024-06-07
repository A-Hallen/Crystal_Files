package com.hallen.rfilemanager.ui.view.activities

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.hallen.rfilemanager.R
import com.hallen.rfilemanager.databinding.ActivityImageViewerBinding
import com.hallen.rfilemanager.infraestructure.MediaManipulation
import com.hallen.rfilemanager.ui.view.fragments.ImageViewerFragment
import dagger.hilt.android.AndroidEntryPoint

interface OnPictureClickListener {
    fun onClick(position: Int, name: String, view: ImageView)
}

typealias MediaPhoto = MediaManipulation.MediaFile

@AndroidEntryPoint
class ImageViewerActivity : AppCompatActivity() {


    private lateinit var binding: ActivityImageViewerBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        handleIntentData()
    }

    private fun handleIntentData() {
        val data = intent.data
        val bundle = Bundle()
        bundle.putParcelable("uri", data)

        val fragment = ImageViewerFragment()
        fragment.arguments = bundle
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_container_view, fragment)
        fragmentTransaction.commit()

    }

}