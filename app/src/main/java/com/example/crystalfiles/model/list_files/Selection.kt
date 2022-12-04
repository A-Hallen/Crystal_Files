package com.example.crystalfiles.model.list_files

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.PopupMenu
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crystalfiles.R
import com.example.crystalfiles.model.Global.Companion.actualPath
import com.example.crystalfiles.model.Global.Companion.checkBoxListArray
import com.example.crystalfiles.model.Global.Companion.checkeds
import com.example.crystalfiles.model.Global.Companion.cut
import com.example.crystalfiles.model.Global.Companion.galeryScrollPosition
import com.example.crystalfiles.model.Global.Companion.imageFolderArray
import com.example.crystalfiles.model.Global.Companion.mode
import com.example.crystalfiles.model.Global.Companion.modeTipe
import com.example.crystalfiles.model.Global.Companion.newArrayList
import com.example.crystalfiles.model.Global.Companion.newRecyclerViewBottomM
import com.example.crystalfiles.model.Global.Companion.portapapeles
import com.example.crystalfiles.model.Global.Companion.totals
import com.example.crystalfiles.model.MediaManipulation
import com.example.crystalfiles.model.Share
import com.example.crystalfiles.model.compress.CompressDialog
import com.example.crystalfiles.model.compress.Decompress
import com.example.crystalfiles.model.filemanipulation.Delete
import com.example.crystalfiles.model.filemanipulation.FilesManipulation
import com.example.crystalfiles.model.filemanipulation.Rename
import com.example.crystalfiles.model.imageFolder
import com.example.crystalfiles.view.recyclerview.MyAdapter
import com.example.crystalfiles.view.recyclerview.PictureFolderAdapter
import com.example.crystalfiles.view.recyclerview.itemClickListener
import com.example.crystalfiles.view.recyclerview.itemPressListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.skydoves.progressview.ProgressView
import java.io.File

class Selection(private val context: Context): itemClickListener, itemPressListener {

    private val ctx = (context as Activity)
    private val selectAllTopBar: CoordinatorLayout = ctx.findViewById(R.id.selectAllTopBarLayout)
    private val selectNavBar = ctx.findViewById<BottomNavigationView>(R.id.select_nav_bar)
    private val newRecyclerView: RecyclerView = ctx.findViewById(R.id.recyclerView)
    private val appBarMain = ctx.findViewById<CoordinatorLayout>(R.id.app_bar_main)
    private val numberItems: TextView = ctx.findViewById(R.id.numberItems)
    private val cbSelectAll: CheckBox = ctx.findViewById(R.id.cbSelectAll)
    private val back1: TextView = ctx.findViewById(R.id.back1)
    private val back2: TextView = ctx.findViewById(R.id.back2)

    fun selection(selectAll:Boolean = false, position: Int? = null) {

        if (selectAllTopBar.visibility == View.INVISIBLE){
            selectAllTopBar.visibility = View.VISIBLE
            selectNavBar.visibility = View.VISIBLE
            selectNavBar.menu.clear()
            selectNavBar.inflateMenu(R.menu.select_nav_items_menu)
            val param = newRecyclerView.layoutParams as ViewGroup.MarginLayoutParams
            val paramNav = selectNavBar.layoutParams.height
            param.bottomMargin = paramNav
            newRecyclerView.layoutParams = param
            appBarMain.visibility = View.INVISIBLE
        }
        ////temporal
        if (selectAll){
            if(mode){
                val folds = MediaManipulation(context).getPicturePaths()
                galeryScrollPosition = newRecyclerView.layoutManager!!.onSaveInstanceState()!!
                imageFolderArray = folds
                newRecyclerView.adapter = PictureFolderAdapter(folds, context, this, this,
                    select = true)
                newRecyclerView.layoutManager!!.onRestoreInstanceState(galeryScrollPosition)
                return
            } else{
                for ((e, i) in newArrayList.withIndex()){
                    checkBoxListArray.put(e, true)
                    i.cbVisibility = true
                    i.state = true
                    checkeds = totals
                }
            }

        } else {
            if (mode){
                val folds = MediaManipulation(context).getPicturePaths()
                galeryScrollPosition = newRecyclerView.layoutManager!!.onSaveInstanceState()!!
                imageFolderArray = folds
                newRecyclerView.adapter = PictureFolderAdapter(folds, context, this, this,
                    select = true)
                newRecyclerView.layoutManager!!.onRestoreInstanceState(galeryScrollPosition)
                return
            } else {

                for (i in newArrayList){
                    i.cbVisibility = true
                    i.state = false
                    checkeds = 0
                    checkBoxListArray.clear()
                }
                if (position != null){
                    newArrayList[position].state = true
                    checkBoxListArray.put(position, true)
                    checkeds++
                    numberItems.text = String.format(context.resources.getString(R.string.selected), checkeds, totals)
                }
            }

        }
        ////temporal
        totals = newArrayList.size
        numberItems.text = String.format(context.resources.getString(R.string.selected), checkeds, totals)
        val adapter = MyAdapter(newArrayList)
        galeryScrollPosition = newRecyclerView.layoutManager!!.onSaveInstanceState()!!
        newRecyclerView.adapter = adapter
        newRecyclerView.layoutManager!!.onRestoreInstanceState(galeryScrollPosition)
        adapter.setOnItemCheckListener(object : MyAdapter.OnItemCheckListener{
            override fun onItemCheck(position: Int, checkBox: CheckBox, layoutBackground: ConstraintLayout) {
                if(!checkBoxListArray.get(position,false))
                {//checkbox checked.
                    checkBox.isChecked = true
                    newArrayList[position].state = true
                    newRecyclerView.adapter?.notifyItemChanged(position)
                    checkeds++
                    numberItems.text = String.format(context.resources.getString(R.string.selected), checkeds, totals)
                    //stores checkbox states and position
                    checkBoxListArray.put(position, true)
                }
                else
                {//checkbox unchecked
                    checkBox.isChecked = false
                    newArrayList[position].state = false
                    newRecyclerView.adapter?.notifyItemChanged(position)
                    checkeds--
                    numberItems.text = String.format(context.resources.getString(R.string.selected), checkeds, totals)
                    cbSelectAll.isChecked = false
                    checkBoxListArray.put(position, false)
                }
            }
        })
        adapter.setOnLongItemClickListener(object :MyAdapter.OnItemLongClickListener{
            override fun onItemLongClick(position: Int, view: View) {  }
        })
        adapter.setOnItemClicKListener(object :MyAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
            }
        })

    }

    override fun onPicClicked(
        folders: ArrayList<imageFolder>,
        pictureFolderPath: String?,
        folderName: String?,
        position: Int,
        check: CheckBox?
    ) {
        mode=true
        if (check != null){
            if (check.isChecked){
                //false
                check.isChecked = false
                checkeds -= 1
                numberItems.text = String.format(context.resources.getString(R.string.selected), checkeds, totals)
                folders[position].state = false
                newRecyclerView.adapter?.notifyItemChanged(position)
                checkBoxListArray.put(position, false)

            } else {
                //true
                checkeds += 1
                check.isChecked = true
                numberItems.text = String.format(context.resources.getString(R.string.selected), checkeds, totals)
                folders[position].state = true
                checkBoxListArray.put(position, true)
                newRecyclerView.adapter?.notifyItemChanged(position)
            }
            return
        }
        val folder = File(pictureFolderPath.toString())
        if (folder.isDirectory){
            galeryScrollPosition = newRecyclerView.layoutManager?.onSaveInstanceState()!!
            Log.i("POSITION", galeryScrollPosition.toString())
            back2.text = folder.name
            back1.text = modeTipe
            val foldsArray:ArrayList<imageFolder> = MediaManipulation(context).getPicturesOnPath(File(pictureFolderPath!!))
            imageFolderArray = foldsArray
            newRecyclerView.adapter = PictureFolderAdapter(foldsArray, context, this, this)
            actualPath = File(pictureFolderPath)
        } else {
            fileFun(File(pictureFolderPath.toString()), context)
        }
    }

    override fun onPicPressed(
        folders: ArrayList<imageFolder>?,
        path: String?,
        folderName: String?,
        position: Int,
        check: CheckBox?
    ) {
        if (selectAllTopBar.visibility == View.INVISIBLE){
            selectAllTopBar.visibility = View.VISIBLE
            selectNavBar.visibility = View.VISIBLE
            appBarMain.visibility = View.INVISIBLE
            checkBoxListArray.clear()
            selectNavBar.menu.clear()
            selectNavBar.inflateMenu(R.menu.galery_nav_bar)
            totals = folders!!.size
            numberItems.text = String.format(context.resources.getString(R.string.selected), checkeds, totals)
            galeryScrollPosition = newRecyclerView.layoutManager!!.onSaveInstanceState()!!
            for(i in folders){
                i.state = false
            }
            imageFolderArray = folders
            newRecyclerView.adapter = PictureFolderAdapter(folders, context, this, this, true)
            newRecyclerView.layoutManager?.onRestoreInstanceState(galeryScrollPosition)
            check?.isChecked = true
            checkeds = 1
            numberItems.text = String.format(context.resources.getString(R.string.selected), checkeds, totals)
            folders[position].state = true
            checkBoxListArray.put(position, true)
            newRecyclerView.adapter?.notifyItemChanged(position)
        }

    }

    private fun getSelectedItems(visibility:Boolean = true):Array<String?>{
        if (mode){
            if (selectAllTopBar.visibility == View.VISIBLE && visibility){
                selectAllTopBar.visibility = View.INVISIBLE
                selectNavBar.visibility = View.INVISIBLE
                galeryScrollPosition = newRecyclerView.layoutManager?.onSaveInstanceState()!!
                newRecyclerView.adapter = PictureFolderAdapter(imageFolderArray, context, this, this)
                val layout: GridLayoutManager = newRecyclerView.layoutManager as GridLayoutManager
                (newRecyclerView.layoutManager as GridLayoutManager).onRestoreInstanceState(galeryScrollPosition)
                newRecyclerView.layoutManager = layout
                back1.text = context.getString(R.string.local); back2.text = modeTipe
                val param = newRecyclerView.layoutParams as ViewGroup.MarginLayoutParams
                param.bottomMargin = newRecyclerViewBottomM
                newRecyclerView.layoutParams = param
                if (appBarMain.visibility == View.INVISIBLE){
                    appBarMain.visibility = View.VISIBLE
                    cbSelectAll.isChecked = false

                }

            }
            val arrayOfSelectedItems:MutableList<Int> = arrayListOf()
            for (i in 0..totals){
                if (checkBoxListArray.get(i, false)){
                    arrayOfSelectedItems.add(i)
                }
            }

            val list = arrayOfNulls<String>(arrayOfSelectedItems.size)

            if (checkeds == totals){
                for (i in 0 until imageFolderArray.size){
                    list[i] = imageFolderArray[i].path
                }
            } else {
                for (i in 0 until arrayOfSelectedItems.size){
                    list[i] = imageFolderArray[arrayOfSelectedItems[i]].path
                }
            }
            checkeds = 0
            totals = 0

            return list
        } else {
            if (selectAllTopBar.visibility == View.VISIBLE && visibility){
                selectAllTopBar.visibility = View.INVISIBLE
                selectNavBar.visibility = View.INVISIBLE
                val param = newRecyclerView.layoutParams as ViewGroup.MarginLayoutParams
                param.bottomMargin = newRecyclerViewBottomM
                newRecyclerView.layoutParams = param
                if (appBarMain.visibility == View.INVISIBLE){
                    appBarMain.visibility = View.VISIBLE
                    cbSelectAll.isChecked = false

                }

            }
            val arrayOfSelectedItems:MutableList<Int> = arrayListOf()
            for (i in 0..totals){
                if (checkBoxListArray.get(i, false)){
                    arrayOfSelectedItems.add(i)
                }
            }

            val list = arrayOfNulls<String>(arrayOfSelectedItems.size)

            if (checkeds == totals){
                for (i in 0 until newArrayList.size){
                    list[i] = newArrayList[i].path.absolutePath
                }
            } else {
                for (i in 0 until arrayOfSelectedItems.size){
                    list[i] = newArrayList[arrayOfSelectedItems[i]].path.absolutePath
                }
            }
            if(visibility){
                checkeds = 0
                totals = 0
            }

            return list
        }
    }

    fun showPasteBar() {
        val selected = getSelectedItems()
        portapapeles = selected

        val pasteBar: BottomNavigationView = (context as Activity).findViewById(R.id.navigation_bar) //the bar at bottom of screen in wich we show cut paste, new folder, etc
        if (pasteBar.visibility != View.VISIBLE){
            pasteBar.visibility = View.VISIBLE
            val param = newRecyclerView.layoutParams as ViewGroup.MarginLayoutParams
            val paramNav = selectNavBar.layoutParams.height
            param.bottomMargin = paramNav
            newRecyclerView.layoutParams = param
        }
        cut = false
    }
    fun showPasteBarCut() {
        val selected = getSelectedItems()
        val pasteBar: BottomNavigationView = (context as Activity).findViewById(R.id.navigation_bar) //the bar at bottom of screen in wich we show cut paste, new folder, etc
        portapapeles = selected
        if (pasteBar.visibility != View.VISIBLE){
            pasteBar.visibility = View.VISIBLE
        }
        cut = true
    }

    fun selectEliminar(progressView: ProgressView, readStorage: ReadStorage) {
        Delete(context).deleteFun(getSelectedItems(), progressView, newRecyclerView, readStorage, this@Selection)
    }

    fun rename(readStorage: ReadStorage) {
        val list = getSelectedItems()
        val listFiles = arrayOfNulls<File>(list.size)
        for(i in list.indices){
            listFiles[i] = File(list[i]!!)
        }
        Rename(context, this@Selection, readStorage, newRecyclerView).showRenameDialog(listFiles)

    }
    private fun share(file:File){
        Share(context).shareIntent(file)
    }
    fun popupMenus(readStorage: ReadStorage) {
        val list = getSelectedItems(false)
        if (list.isEmpty()) return
        val popupMenus = PopupMenu(context, selectNavBar, Gravity.END)
        popupMenus.inflate(R.menu.context_menu)
        if (File(list[0]!!).extension == "zip"){
            popupMenus.menu.findItem(R.id.comprimir).isVisible = false
            popupMenus.menu.findItem(R.id.descomprimir).isVisible = true
        }
        popupMenus.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.openWidth -> {
                    fileFun(File(list[0]!!), context, true)
                    true
                }
                R.id.compartir -> {
                    share(File(list[0]!!))
                    true
                }
                R.id.comprimir -> {
                    CompressDialog(readStorage).show(context, list)
                    true
                }
                R.id.propiedades -> {
                    FilesManipulation(context).propiedades(File(list[0]!!))
                    true
                }
                R.id.descomprimir -> {
                    Decompress(context, readStorage, list[0]!!)
                    true
                }
                else -> true
            }
        }
        popupMenus.show()
        val popup = PopupMenu::class.java.getDeclaredField("mPopup")
        popup.isAccessible = true
        val menu = popup.get(popupMenus)
        menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java).invoke(menu, true)

    }


}