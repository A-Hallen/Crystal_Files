package com.example.crystalfiles.view

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.crystalfiles.R
import com.example.crystalfiles.databinding.ActivityMainBinding
import com.example.crystalfiles.model.Global
import com.example.crystalfiles.model.Global.Companion.actualPath
import com.example.crystalfiles.model.Global.Companion.cut
import com.example.crystalfiles.model.Global.Companion.drives
import com.example.crystalfiles.model.Global.Companion.galeryScrollPosition
import com.example.crystalfiles.model.Global.Companion.imageFolderArray
import com.example.crystalfiles.model.Global.Companion.modeTipe
import com.example.crystalfiles.model.Global.Companion.portapapeles
import com.example.crystalfiles.model.Global.Companion.scale
import com.example.crystalfiles.model.Global.Companion.scrollPositionsArray
import com.example.crystalfiles.model.Global.Companion.totals
import com.example.crystalfiles.model.MediaManipulation
import com.example.crystalfiles.model.Permissions
import com.example.crystalfiles.model.copymove.CopyMove
import com.example.crystalfiles.model.filemanipulation.Create
import com.example.crystalfiles.model.imageFolder
import com.example.crystalfiles.model.list_files.ListDrives
import com.example.crystalfiles.model.list_files.ReadStorage
import com.example.crystalfiles.model.list_files.Selection
import com.example.crystalfiles.model.list_files.startReading
import com.example.crystalfiles.model.prefs.SharedPrefs.Companion.prefs
import com.example.crystalfiles.search.hideSearch
import com.example.crystalfiles.testing.Test
import com.example.crystalfiles.view.leftpanel.Listeners
import com.example.crystalfiles.view.leftpanel.NavDrawer
import com.example.crystalfiles.view.recyclerview.PictureFolderAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File


@Suppress("UNUSED_PARAMETER")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var selectAllTopBar: CoordinatorLayout
    private lateinit var selectNavBar: BottomNavigationView
    private lateinit var newRecyclerView: RecyclerView
    private lateinit var appBarMain: CoordinatorLayout
    private lateinit var cbSelectAll: CheckBox
    private lateinit var readStorage: ReadStorage
    private lateinit var selection: Selection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)


        setContentView(binding.root)


        Permissions(this)
        scale = prefs.getScala()
        drives = ListDrives(this).getStorageDirectories()
        startReading(this)
        Test(this).test()

        selectAllTopBar = binding.selectAllTopBarLayout.selectAllTopBar
        selectNavBar = binding.selectBottomNavLayout.selectNavBar
        newRecyclerView = binding.reciclerViewLayout.recyclerView
        appBarMain = binding.appBarMain.appBarMain2
        cbSelectAll = binding.selectAllTopBarLayout.cbSelectAll
        setBackground() // Set the background
        readStorage = ReadStorage(this)

        val navDrawer = NavDrawer(this)
        binding.appBarMain.hamburgerIcon.setOnClickListener {   navDrawer.hamburgetFun()    }
        binding.appBarMain.back1.setOnClickListener { backward() }
        cbSelectAll.setOnClickListener { selectAll() }
        binding.selectAllTopBarLayout.closeSelectAll.setOnClickListener { closeSelectAll() }



        selection = Selection(this)
        val listeners =  Listeners(this, newRecyclerView, selection, binding.expandableList)
        listeners.onItemLongClickListener();    listeners.onChildClickListener(readStorage)

        val normalAppBarMain = binding.appBarMain.normalAppBarMain;     val searchL = binding.appBarMain.searchL
        binding.appBarMain.settingsIcon.setOnClickListener {MenuSettings(this, newRecyclerView, readStorage, it, normalAppBarMain, searchL)}
        binding.appBarMain.cancelSearch.setOnClickListener { hideSearch(normalAppBarMain, searchL); readStorage.readStorage(actualPath) }
    }

    private fun setBackground(){
        if(!prefs.getDefaultBackground()){
            val path = prefs.getBgLocation()
            val bg = File(path)
            if (bg.exists() && bg.canRead()){
                val background = binding.background
                Glide.with(this).load(bg).override(background.width, background.height).centerCrop().into(binding.background)
            }
        } else {
            Glide.with(this).load(R.drawable.wallpaper_default).into(binding.background)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 918){
            setBackground()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        prefs.saveLastLocation(actualPath.absolutePath)
    }

    private fun closeSelectAll() {
        backward()
    }

    override fun onBackPressed() {
        backward()
    }//Sobreescribir la accion de ir hacia atras

    private fun selectAll() {
        if (cbSelectAll.isChecked){
            Global.checkeds = totals
            cbSelectAll.isChecked = true
            selection.selection(true)
        } else {
            Global.checkeds = 0
            cbSelectAll.isChecked = false
            selection.selection(false)
        }
        binding.selectAllTopBarLayout.numberItems.text = String.format(resources.getString(R.string.selected), Global.checkeds, totals)
    }
    private fun ifSelectBackward(){
        Global.checkBoxListArray.clear()
        selectAllTopBar.visibility = View.INVISIBLE
        selectNavBar.visibility = View.INVISIBLE
        val param = newRecyclerView.layoutParams as ViewGroup.MarginLayoutParams
        param.bottomMargin = Global.newRecyclerViewBottomM
        newRecyclerView.layoutParams = param

        if (appBarMain.visibility == View.INVISIBLE){
            appBarMain.visibility = View.VISIBLE
            cbSelectAll.isChecked = false
            Global.checkeds = 0
            totals = 0
        }
        if (Global.mode){
            newRecyclerView.adapter = PictureFolderAdapter(imageFolderArray, this, selection, selection)
            newRecyclerView.scrollToPosition(0)
            (newRecyclerView.layoutManager as GridLayoutManager).onRestoreInstanceState(galeryScrollPosition)
            readStorage.changeBacksText("Local", modeTipe)
        } else {
            galeryScrollPosition = newRecyclerView.layoutManager!!.onSaveInstanceState()!!
            readStorage.readStorage(actualPath)
            newRecyclerView.layoutManager!!.onRestoreInstanceState(galeryScrollPosition)
        }
    }
    private fun backward(){
        if (selectAllTopBar.visibility == View.VISIBLE){
            ifSelectBackward(); return
        }
        if (Global.mode && modeTipe == "Imagenes"){
            val folds: ArrayList<imageFolder> = MediaManipulation(this).getPicturePaths()
            newRecyclerView.adapter = PictureFolderAdapter(folds, this, selection, selection)
            (newRecyclerView.layoutManager as GridLayoutManager).onRestoreInstanceState(galeryScrollPosition)
            readStorage.changeBacksText("Local", modeTipe)
            return
        }

        Global.checkBoxListArray.clear()

        try {
            val parent = actualPath.parentFile!!
            if(parent.name == "emulated" || parent.name == "storage"){
                return
            }
            readStorage.readStorage(parent, true)
            if (scrollPositionsArray.isNotEmpty()) {
                newRecyclerView.layoutManager!!.onRestoreInstanceState(scrollPositionsArray.last())
            }
        } catch (e: java.lang.NullPointerException){
            return
        }
    }

    fun showPasteBar(item: MenuItem) {
        selection.showPasteBar()
        ifSelectBackward()
    }
    fun showPasteBarCut(item: MenuItem) {
        selection.showPasteBarCut()
        ifSelectBackward()
    }
    fun selectEliminar(item: MenuItem) {selection.selectEliminar(
        binding.progressBarLayout.progressView, readStorage
    )}
    fun selectRename(item: MenuItem) {selection.rename(readStorage)}
    fun popupMenus(item: MenuItem) {selection.popupMenus(readStorage); ifSelectBackward()}
    fun cancel(item: MenuItem) {
        val pasteBar = binding.pasteBarLayout.navigationBar
        if (pasteBar.visibility == View.VISIBLE){pasteBar.visibility = View.INVISIBLE}
        val param = newRecyclerView.layoutParams as ViewGroup.MarginLayoutParams
        param.bottomMargin = Global.newRecyclerViewBottomM
        newRecyclerView.layoutParams = param
        ifSelectBackward()
    }

    fun paste(item: MenuItem) {
        val copyMove = CopyMove(this, readStorage)
        if (cut){
            copyMove.move(portapapeles, actualPath.absolutePath)
        } else {
            copyMove.copy(portapapeles, actualPath.absolutePath)
        }
        cancel(item)
    }

    fun createFolder(item: MenuItem) {
        Create().createFolderDialog(this, readStorage, newRecyclerView.layoutManager!!, true)
    }
}