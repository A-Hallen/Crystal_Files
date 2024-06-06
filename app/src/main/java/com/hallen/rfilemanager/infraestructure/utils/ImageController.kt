package com.hallen.rfilemanager.infraestructure.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.caverock.androidsvg.SVG
import com.devs.vectorchildfinder.VectorChildFinder
import com.google.android.material.imageview.ShapeableImageView
import com.hallen.rfilemanager.R
import com.hallen.rfilemanager.infraestructure.persistance.Prefs
import com.hallen.rfilemanager.ui.view.leftpanel.ThemeColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageController @Inject constructor(context: Context) {

    private val iconsManager = IconsManager(context, Prefs(context))

    private val failImages: ArrayList<String> = arrayListOf()


    private fun loadSvg(scope: CoroutineScope, imageView: ShapeableImageView, file: File) {
        scope.launch {
            withContext(Dispatchers.IO) {
                val fileInputStream = FileInputStream(file)
                val svg = SVG.getFromInputStream(fileInputStream)
                val picture = svg.renderToPicture()
                val pictureDrawable = PictureDrawable(picture)
                val load = Glide.with(imageView.context).load(pictureDrawable)
                loadGlide(load, imageView)
                fileInputStream.close()
            }
        }
    }

    private fun loadApkIcon(imageView: ShapeableImageView, file: File) {
        val context = imageView.context
        val packageManager = context.packageManager

        val packageArchiveInfo = packageManager.getPackageArchiveInfo(file.absolutePath, 0)
        val packageInfo = packageArchiveInfo?.applicationInfo

        val icon = packageInfo?.loadIcon(packageManager)
        if (icon != null) {
            val load = Glide.with(context).load(icon)
            loadGlide(load, imageView)
            setGlideImageFromDrawable(imageView, icon)
            return
        }

        val usedIconPack = iconsManager.getUsedIconPack().name
        val mime = "application/vnd.android.package-archive"
        if (usedIconPack.isNotBlank()) {
            val imageIconResult = setImageIcon(imageView, mime)
            if (imageIconResult) return
        }

        val drawable = ContextCompat.getDrawable(context, R.drawable.file)!!
        setGlideImageFromDrawable(imageView, drawable)
    }

    fun setImage(imageView: ShapeableImageView, file: File) {
        CoroutineScope(Dispatchers.IO).launch {
            val getMimeFile = GetMimeFile(imageView.context)
            val extension = file.extension.lowercase(Locale.getDefault())

            val mime = getMimeFile.getmime(extension)

            when {
                file.extension.equals("apk", ignoreCase = true) -> loadApkIcon(imageView, file)

                file.extension.equals("svg", ignoreCase = true) ->
                    loadSvg(this, imageView, file)

                mime.split("/").firstOrNull() == "image" -> {
                    setGlideImageFromFile(imageView, file)
                }

                mime.split("/").firstOrNull() == "video" -> {
                    setGlideImageFromVideoFile(imageView, file)
                }

                else -> {
                    val usedIconPack = iconsManager.getUsedIconPack().name
                    if (usedIconPack.isNotBlank()) {
                        val imageIconResult = setImageIcon(imageView, mime)
                        if (imageIconResult) return@launch
                    }
                    val drawable = getDrawableFromFile(file, imageView.context)
                    setGlideImageFromDrawable(imageView, drawable)
                }
            }
        }
    }

    private fun setImageIcon(
        imageView: ShapeableImageView,
        mime: String,
    ): Boolean {
        val replace = mime.replace("/", "-")
        val modifiedMime = replace.ifBlank { "empty" }
        val iconPacks = iconsManager.getUsedIconPack()
        val path = iconPacks.drawables[modifiedMime]
        if (path != null) {
            val file = File(path)
            val drawable = getDrawableFromFile(file, imageView.context)
            val load = Glide.with(imageView.context).load(drawable)
            loadGlide(load, imageView)
            return true
        }
        return false
    }

    private fun setGlideImageFromFile(imageView: ShapeableImageView, file: File) {
        loadGlide(Glide.with(imageView.context).load(file).error(R.drawable.icon_image), imageView)
    }

    private fun setGlideImageFromDrawable(imageView: ShapeableImageView, drawable: Drawable) {
        CoroutineScope(Dispatchers.Main).launch {
            imageView.setImageDrawable(drawable)
        }
    }

    private val listener = object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean,
        ): Boolean {
            target ?: return false
            if (model !is File) return false
            failImages.add(model.absolutePath)
            return true
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean,
        ): Boolean {
            return false
        }
    }

    private fun setGlideImageFromVideoFile(imageView: ShapeableImageView, file: File) {
        val context = imageView.context
        if (file.absolutePath in failImages) {
            imageView.setImageResource(R.drawable.icon_video)
            return
        }

        val builder = Glide.with(context).load(file).thumbnail(
            Glide.with(context)
                .load(R.drawable.animated_vector_loading)
                .apply(RequestOptions().override(24, 24))
        )
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .listener(listener)
            .error(R.drawable.icon_video)
        loadGlide(builder, imageView)
    }

    private fun loadGlide(builder: RequestBuilder<Drawable>, imageView: ShapeableImageView) {
        CoroutineScope(Dispatchers.Main).launch {
            builder.transition(DrawableTransitionOptions.withCrossFade()).into(imageView)
        }
    }

    fun setFolderImage(
        imageView: ShapeableImageView,
        imageView2: ShapeableImageView,
        colorTheme: ThemeColor?,
    ) {
        val vector = VectorChildFinder(
            imageView2.context,
            R.drawable.folder1,
            imageView2
        )
        val path1: com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath =
            vector.findPathByName("path1")
        val normalColor = colorTheme?.normalColor
        path1.fillColor = Color.parseColor(normalColor)
        imageView2.invalidate()
        imageView.setImageResource(R.drawable.folder)
    }

    companion object {
        fun getDrawableFromFile(file: File, context: Context): Drawable {
            val drawable = try {
                if (file.extension == "svg") {
                    val fileInputStream = FileInputStream(file)
                    val svg = SVG.getFromInputStream(fileInputStream)
                    val pictureDrawable = PictureDrawable(svg.renderToPicture())
                    fileInputStream.close()
                    pictureDrawable
                } else Glide.with(context).asDrawable().load(file).submit().get()
            } catch (e: Exception) {
                e.printStackTrace()
                ContextCompat.getDrawable(context, R.drawable.file)!!
            }
            return drawable
        }
    }
}