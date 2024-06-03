package com.hallen.rfilemanager.ui.view.leftpanel

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.StatFs
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import androidx.core.content.ContextCompat
import com.hallen.rfilemanager.R
import com.hallen.rfilemanager.databinding.ExpandableListChildsBinding
import com.hallen.rfilemanager.databinding.ExpandableListChildsProgressBinding
import com.hallen.rfilemanager.databinding.ListItemBinding
import com.hallen.rfilemanager.model.Storage
import com.hallen.rfilemanager.ui.utils.ColorManagement
import java.io.File
import java.text.DecimalFormat

typealias ThemeColor = ColorManagement.ThemeColor

class NavListAdapter(private val context: Context) : BaseExpandableListAdapter() {

    private var drives: List<Storage>? = null
    private var titleList: List<String> = emptyList()
    private var detailList: HashMap<String, List<String>> = HashMap()
    private var colorScheme: ThemeColor? = null
    private val driveMap = mutableMapOf<String, Long>()


    override fun getChildId(listPos: Int, expandedPos: Int): Long = expandedPos.toLong()
    override fun getChildrenCount(position: Int): Int = detailList[titleList[position]]?.size ?: 0
    override fun getGroupCount(): Int = titleList.size
    override fun getGroup(position: Int): String = titleList[position]
    override fun getGroupId(listPos: Int): Long = listPos.toLong()
    override fun hasStableIds(): Boolean = false
    override fun isChildSelectable(p0: Int, p1: Int): Boolean = true

    fun hasToUpdateDrives(newDrives: List<Storage>?): Boolean {
        if (newDrives?.size != drives?.size) return true
        return newDrives?.any {
            val newSpace = bytesToHuman(getAvailableSpace(it))
            val oldSize = driveMap[it.description] ?: return true
            val oldSpace = bytesToHuman(oldSize)
            newSpace != oldSpace
        } ?: true
    }

    fun update(
        data: HashMap<String, List<String>>,
        drives: List<Storage>?,
        colorScheme: ThemeColor? = null,
    ) {
        this.drives = drives
        colorScheme?.let { this.colorScheme = it }
        detailList = data
        titleList = detailList.keys.toList()
        notifyDataSetChanged()
    }

    override fun getChild(listPos: Int, expandedPos: Int): String {
        val key: String = titleList[listPos]
        val value: List<String> = detailList[key] ?: emptyList()
        return value.getOrNull(expandedPos) ?: "error"
    }

    override fun getGroupView(
        listPos: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?,
    ): View {
        val listTitle: String = getGroup(listPos)
        val layoutInflater = LayoutInflater.from(context)

        if (listPos <= 2) {
            val binding = ListItemBinding.inflate(layoutInflater)
            binding.listView.text = listTitle
            val imageResource =
                if (isExpanded) R.drawable.access_tab_down else R.drawable.access_tab_up
            binding.indicator.setImageResource(imageResource)
            return binding.root
        }

        return when (listTitle) {
            DrawerData.DOWNLOAD -> {
                val binding = ExpandableListChildsBinding.inflate(layoutInflater)
                binding.imageViewChild.setImageResource(R.drawable.sidebar_download_folder)
                binding.listViewChild.text = listTitle
                binding.listViewChild.setTextColor(ContextCompat.getColor(context, R.color.white))
                binding.root
            }

            else -> getDeviceView(layoutInflater, listPos)
        }
    }

    private fun getDeviceView(layoutInflater: LayoutInflater, listPos: Int): View {
        val binding = ExpandableListChildsProgressBinding.inflate(layoutInflater)
        val drive = drives?.find { it.description == titleList[listPos] } ?: return binding.root
        val color = colorScheme?.darkColor ?: return binding.root

        binding.listViewChildProgress.text = drive.description

        /*        if (drive.state != "mounted") {
                    binding.listViewChildProgressSize.text = "DESMONTADO"
                    binding.expandableProgressBar.progress = 100
                    binding.expandableProgressBar.progressTintList = ColorStateList.valueOf(Color.DKGRAY)
                    driveMap[drive.description] = drive.freeSpace
                    return binding.root
                }*/

        val parseColor = Color.parseColor(color)
        binding.expandableProgressBar.progressTintList = ColorStateList.valueOf(parseColor)

        val storageIcon = getStorageIcon(drive)
        binding.imageViewChildProgress.setImageResource(storageIcon)

        val thread = object : Thread() {
            override fun run() {
                super.run()
                setPercent(drive, binding)
            }
        }
        (thread as Thread).start()
        return binding.root
    }

    private fun getStorageIcon(storage: Storage): Int {
        return when {
            storage.isPrimary -> R.drawable.icon_phone
            storage.isRemovable -> R.drawable.sidebar_sdcard
            else -> R.drawable.sidebar_sdcard
        }
    }

    private fun getAvailableSpace(file: File): Long {
        val statFs = StatFs(file.path)
        return statFs.availableBytes
    }


    private fun setPercent(
        storage: Storage?,
        binding: ExpandableListChildsProgressBinding,
    ) {
        storage ?: return

        val totalSpace = storage.totalSpace
        val freeSpace = getAvailableSpace(storage)
        driveMap[storage.description] = freeSpace
        if (totalSpace == 0L) return
        val percent = if (freeSpace < 0) 100 else 100 * freeSpace / totalSpace
        (context as Activity).runOnUiThread {
            binding.listViewChildProgressSize.text = bytesToHuman(freeSpace) + "/"
            binding.listViewChildProgressTotal.text = bytesToHuman(totalSpace)
            binding.expandableProgressBar.progress = 100 - percent.toInt()
        }
    }

    private fun floatForm(d: Double): String? {
        return DecimalFormat("#.##").format(d)
    }

    private fun bytesToHuman(size: Long): String {
        val Kb = (1 * 1024).toLong()
        val Mb = Kb * 1024
        val Gb = Mb * 1024
        val Tb = Gb * 1024
        val Pb = Tb * 1024
        val Eb = Pb * 1024
        if (size < Kb) return floatForm(size.toDouble()).toString() + " byte"
        if (size in Kb until Mb) return floatForm(size.toDouble() / Kb).toString() + " Kb"
        if (size in Mb until Gb) return floatForm(size.toDouble() / Mb).toString() + " Mb"
        if (size in Gb until Tb) return floatForm(size.toDouble() / Gb).toString() + " Gb"
        if (size in Tb until Pb) return floatForm(size.toDouble() / Tb).toString() + " Tb"
        if (size in Pb until Eb) return floatForm(size.toDouble() / Pb).toString() + " Pb"
        return if (size >= Eb) floatForm(size.toDouble() / Eb).toString() + " Eb" else "???"
    }

    override fun getChildView(
        listPos: Int,
        expandedPos: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?,
    ): View {
        //if (convertView != null) return convertView
        val expandedListText = getChild(listPos, expandedPos)
        val layoutInflater = LayoutInflater.from(context)
        val binding = ExpandableListChildsBinding.inflate(layoutInflater)
        val imageRes = when (expandedListText) {
            DrawerData.IMAGENES -> R.drawable.sidebar_imagenes
            DrawerData.MUSIC -> R.drawable.sidebar_music
            DrawerData.MOVIES -> R.drawable.sidebar_movies
            DrawerData.BOOKS -> R.drawable.sidebar_books
            DrawerData.APPS -> R.drawable.sidebar_apps
            DrawerData.HIDDEN_FILES -> android.R.drawable.ic_menu_view
            DrawerData.SPACE_ANALISIS -> android.R.drawable.stat_notify_sdcard_prepare
            DrawerData.FAVORITES -> R.drawable.sidebar_new_favorites
            else -> R.drawable.folder
        }
        binding.imageViewChild.setImageResource(imageRes)
        binding.listViewChild.text = expandedListText
        binding.listViewChild.setTextColor(ContextCompat.getColor(context, R.color.white))
        if (expandedListText == DrawerData.HIDDEN_FILES) {
            binding.switchChild.visibility = View.VISIBLE
        }
        binding.switchChild.visibility =
            if (expandedListText == DrawerData.HIDDEN_FILES) View.VISIBLE else View.INVISIBLE

        return binding.root
    }
}