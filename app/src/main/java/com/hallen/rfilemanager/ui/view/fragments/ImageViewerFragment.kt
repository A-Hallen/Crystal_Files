package com.hallen.rfilemanager.ui.view.fragments

import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.loader.content.CursorLoader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hallen.rfilemanager.R
import com.hallen.rfilemanager.databinding.FragmentImageViewerBinding
import com.hallen.rfilemanager.infraestructure.FileProperties
import com.hallen.rfilemanager.infraestructure.Share
import com.hallen.rfilemanager.infraestructure.persistance.Prefs
import com.hallen.rfilemanager.infraestructure.utils.GetMimeFile
import com.hallen.rfilemanager.ui.view.activities.MediaPhoto
import com.hallen.rfilemanager.ui.view.activities.OnPictureClickListener
import com.hallen.rfilemanager.ui.view.adapters.PictureAdapter
import com.hallen.rfilemanager.ui.view.custom.PopupMenuCustomLayout
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint

class ImageViewerFragment : Fragment(), OnPictureClickListener {
    private lateinit var binding: FragmentImageViewerBinding

    @Inject
    lateinit var fileProperties: FileProperties

    @Inject
    lateinit var share: Share

    @Inject
    lateinit var prefs: Prefs

    private lateinit var getMimeFile: GetMimeFile

    private lateinit var pictures: ArrayList<MediaPhoto>

    private var selectedImageView: ImageView? = null

    private var currentPosition: Int = 0
    private var newPosition: Int = 0
    private var selectedPictureName = ""
    private var longAnimationDuration: Int = 0
    private var shortAnimationDuration: Int = 0
    private var data: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data = arguments?.getParcelable("uri")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImageViewerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getMimeFile = GetMimeFile(requireContext())

        longAnimationDuration = resources.getInteger(android.R.integer.config_longAnimTime)
        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

        setupRecyclerView()
        handleIntentData()
        setupListeners()
        setStyle()
    }

    private fun setStyle() {
        val colorScheme = prefs.getColorScheme()
        val lightColor = Color.parseColor(colorScheme.lightColor)
        binding.pictureNameOfFile.setTextColor(lightColor)
    }

    private fun setupListeners() = binding.settingsBtn.setOnClickListener(::settings)
    private fun setupRecyclerView() {
        binding.pictureRv.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            PagerSnapHelper().attachToRecyclerView(this)
            adapter = PictureAdapter(arrayListOf(), this@ImageViewerFragment)
        }
    }

    private fun handleIntentData() {
        val files = getFilesFromUri(data)
        if (files.isNotEmpty()) handleMultipleImages(files.toList()) else handleSingleImage()
    }

    override fun onClick(position: Int, name: String, view: ImageView) {
        newPosition = position
        selectedImageView = view
        showTopBar(name)
    }

    private fun fadeIn(view: View) {
        view.animate()
            .alpha(1f)
            .setDuration(shortAnimationDuration.toLong())
            .withStartAction { view.visibility = View.VISIBLE } // Set visibility at start
    }

    private fun fadeOut(view: View) {
        view.animate()
            .alpha(0f)
            .setDuration(longAnimationDuration.toLong())
            .withEndAction { view.visibility = View.GONE } // Set visibility at end
    }

    private fun showTopBar(fileName: String) {
        if (binding.pictureTopBar.visibility == View.GONE) {
            binding.pictureNameOfFile.text = fileName
            fadeIn(binding.pictureTopBar)
        } else fadeOut(binding.pictureTopBar)
    }

    private fun rotateImage(view: ImageView) {
        view.drawable
        val matrix = Matrix()
        matrix.postRotate(90.0F)
        val imageBitmap = view.drawable.toBitmap()
        val newBitmap = Bitmap.createBitmap(
            imageBitmap, 0, 0, imageBitmap.width, imageBitmap.height,
            matrix, true
        )
        view.setImageBitmap(newBitmap)
    }

    fun settings(view: View) {
        val menuItems = mapOf(
            "Compartir" to R.drawable.share,
            "Girar" to R.drawable.rotate,
            "Establecer como: " to R.drawable.wallpaper_icon,
            "Detalles" to R.drawable.details
        )
        val customPopupMenu = PopupMenuCustomLayout(requireContext(), menuItems, view,
            object : PopupMenuCustomLayout.PopupMenuCustomOnClickListener {
                override fun onClick(index: Int, view: View) {
                    val file = pictures[newPosition]
                    when (index) {
                        0 -> share.shareIntent(file)
                        3 -> fileProperties.showProperties(file)
                        1 -> selectedImageView?.let { rotateImage(it) }
                        2 -> setWallpaperImage(pictures[newPosition])
                    }
                }
            })
        customPopupMenu.show()
    }

    private fun setWallpaperImage(file: File) {
        val uri: Uri = try {
            FileProvider.getUriForFile(
                requireContext().applicationContext,
                "${requireContext().applicationContext.packageName}.provider",
                file
            ).normalizeScheme()
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
        try {
            val intent =
                WallpaperManager.getInstance(requireContext()).getCropAndSetWallpaperIntent(uri)
            startActivity(intent)
        } catch (e: IllegalArgumentException) {
            // Seems to be an Oreo bug - fall back to using the bitmap instead
            WallpaperManager.getInstance(requireContext())
                .setBitmap(selectedImageView?.drawable?.toBitmap())
        }
    }

    private fun getFilesFromUri(uri: Uri?): Array<File> {
        if (uri == null) return emptyArray()

        return try {
            val filePath = getFilePath(uri)
            if (filePath.isEmpty()) {
                return arrayOf(uri.toFile())
            }
            val file = File(filePath)
            val parent = file.parentFile ?: return arrayOf(file)
            parent.listFiles()?.sortedArray() ?: emptyArray()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyArray()
        }
    }

    private fun getFilePath(uri: Uri): String {
        return when {
            uri.authority == "media" -> getPathFromMediaStore(uri)
            uri.scheme == "file" -> uri.path ?: ""
            else -> ""
        }
    }

    private fun getPathFromMediaStore(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(requireContext(), uri, projection, null, null, null)
        val cursor = loader.loadInBackground() ?: return ""
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(columnIndex)
    }

    private fun handleMultipleImages(files: List<File>) {
        pictures = ArrayList()
        selectedPictureName = File(getFilePath(data!!)).name
        var position = 0

        for (file in files) {
            if (getMimeFile.getmime(file.extension).startsWith("image/")) {
                if (file.name == selectedPictureName) currentPosition = position
                position++
                pictures.add(MediaPhoto(file, file.name))
            }
        }

        (binding.pictureRv.adapter as? PictureAdapter)?.updatePictures(pictures)
        binding.pictureRv.scrollToPosition(currentPosition)

        binding.pictureRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING && binding.pictureTopBar.visibility == View.VISIBLE) {
                    fadeOut(binding.pictureTopBar)
                }
            }
        })
    }

    private fun handleSingleImage() {
        if (data != null) {
            binding.pictureSingleFileIv.visibility = View.VISIBLE
            binding.pictureRv.visibility = View.INVISIBLE
            Glide.with(this).load(data).into(binding.pictureSingleFileIv)
        }
    }
}