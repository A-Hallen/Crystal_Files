package com.example.crystalfiles.imagebrowser

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toFile
import androidx.loader.content.CursorLoader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.crystalfiles.R
import com.example.crystalfiles.model.GetMimeFile
import com.example.crystalfiles.model.Share
import com.example.crystalfiles.model.filemanipulation.FilesManipulation
import com.example.crystalfiles.model.list_files.TreeUriConstruct
import com.example.crystalfiles.view.recyclerview.Fotos
import com.example.crystalfiles.view.recyclerview.PictureClickListener
import java.io.File


class PictureView : AppCompatActivity(), PictureClickListener {
    private lateinit var topBar:LinearLayout
    private lateinit var recicler:RecyclerView
    private lateinit var fotos:ArrayList<Fotos>
    private lateinit var singleView:ImageView
    private lateinit var tvpictureName:TextView
    private lateinit var newImageView: ImageView
    private var actualPosition:Int = 0
    private var newPosition: Int = 0
    private var nameofPicture = ""
    private var longAnimationDuration:Int = 0
    private var shortAnimationDuration:Int = 0
    private var data:Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_view)

        //Aqui capturamos las vistas
        tvpictureName = findViewById(R.id.picture_name_of_file)
        singleView= findViewById(R.id.picture_single_file_iv)
        recicler  = findViewById(R.id.picture_rv)
        topBar    = findViewById(R.id.picture_top_bar)

        //Retrieve and cache the system's default "short" animation time.
        longAnimationDuration = resources.getInteger(android.R.integer.config_longAnimTime)
        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recicler.layoutManager = layoutManager

        PagerSnapHelper().attachToRecyclerView(recicler)
        val intent = intent
        data = intent.data

        val list = getArray(data)
        if (list.isNotEmpty()){
            fotos = ArrayList()
            nameofPicture = File(getFilePath(data!!)).name
            var position = 0
            for (i in list){
                if (GetMimeFile(i).getmime().split("/")[0] == "image"){
                    val nombre = i.name
                    if (nombre == nameofPicture) actualPosition = position
                    position++
                    fotos.add(Fotos(i, nombre))
                }
            }
            recicler.adapter = PictureAdapter(fotos, this@PictureView)
            Log.i("absolutePath", actualPosition.toString())

            recicler.scrollToPosition(actualPosition)
            recicler.addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING){
                        if (topBar.visibility == View.VISIBLE){
                            fadeOut(topBar)
                        }
                    }
                }
            })

        } else {
            if (data != null){
                singleView.visibility = View.VISIBLE
                recicler.visibility = View.INVISIBLE
                Glide.with(this).asDrawable().load(data).into(singleView)
            }
        }
    }

    private fun getFilePath(uri: Uri):String{
        var string = ""

            when{
                uri.authority == "media" -> {
                    string = getPath(uri)
                }
                uri.scheme == "file" ->{
                    string = uri.path ?: ""
                }
                else ->{
                }
            }
        return string
    }
    private fun getArray(uri: Uri?): Array<File> {
         if (uri != null){
            try {

                val string = getFilePath(uri)
                if (string.isEmpty()){
                    return arrayOf(uri.toFile())
                }
                val file = File(string)

                //Si el archivo no tiene un padre, nos hechamos a llorar T_T... No, mandamos el array con el solo
                val parentString = file.parent ?: return arrayOf(file)

                val parent = File(parentString)
                val list = parent.listFiles()
                list!!.sort()
                return list
            } catch (e:Exception){
                e.printStackTrace()
                return arrayOf()
            }
        } else {
             return arrayOf()
        }
    }

    private fun getPath(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(this, uri, projection, null, null, null)
        val cursor = loader.loadInBackground() ?: return ""
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(columnIndex)
    }

    private fun showTopBar(name:String) {
        if (topBar.visibility == View.GONE){
            tvpictureName.text = name
            fadeIn(topBar)
        } else {
            fadeOut(topBar)
        }
    }

    private fun fadeIn(view: View) {
        view.apply {
            alpha = 0f
            animate().alpha(1f).setDuration(shortAnimationDuration.toLong()).setListener(null)
        }
        view.visibility = View.VISIBLE
    }

    private fun fadeOut(view: View) {
        view.apply {
            alpha = 1f
            animate().alpha(0f).setDuration(longAnimationDuration.toLong()).setListener(object: AnimatorListenerAdapter(){

                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.GONE
                }
            })
        }
    }

    override fun onClick(position: Int, name:String, view: ImageView) {
        newPosition = position
        newImageView = view
        showTopBar(name)
    }

    private fun rotarImagen(view: ImageView) {
        view.drawable
        val matrix = Matrix()
        matrix.postRotate(90.0F)
        val imageBitmap = view.drawable.toBitmap()
        val newBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.width, imageBitmap.height,
        matrix, true)
        view.setImageBitmap(newBitmap)
    }

    fun settings(view: View) {
        val popupMenu = PopupMenu(this, view, Gravity.END)
        popupMenu.inflate(R.menu.picture_settings)
        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.picture_compartir ->{
                    val file = fotos[newPosition].file
                    Share(this).shareIntent(file)
                    true
                }
                R.id.picture_detalles ->{
                    val file = fotos[newPosition].file
                    FilesManipulation(this).propiedades(file)
                    true
                }
                R.id.picture_girar -> {
                    rotarImagen(newImageView)
                    true
                }
                R.id.picture_fondo -> {
                    val file = fotos[newPosition].file
                    setWallpaperImage(file)
                    true
                }
                else -> true
            }
        }
        popupMenu.show()
        val popup = PopupMenu::class.java.getDeclaredField("mPopup")
        popup.isAccessible = true
        val menu = popup.get(popupMenu)
        menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java).invoke(menu, true)
    }

    private fun setWallpaperImage(file: File) {
        var uri: Uri
        try {
            uri = FileProvider.getUriForFile(this.applicationContext, "${this.applicationContext.packageName}.provider", file).normalizeScheme()
        } catch (e: Exception){
            val tree = TreeUriConstruct()
            uri = tree.construct(file.absolutePath, this).uri
            e.printStackTrace()
        }
        try {
            val intent =
                WallpaperManager.getInstance(this).getCropAndSetWallpaperIntent(uri)
            //startActivityForResult to stop the progress bar
            startActivity(intent)
        } catch (e: IllegalArgumentException) {
            // Seems to be an Oreo bug - fall back to using the bitmap instead
            WallpaperManager.getInstance(this).setBitmap(newImageView.drawable.toBitmap())
        }
    }
}