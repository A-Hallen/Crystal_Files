package com.example.crystalfiles.view.leftpanel

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.example.crystalfiles.R
import com.example.crystalfiles.model.Global.Companion.drives
import com.example.crystalfiles.model.Global.Companion.globalDarkModeSwitchState
import com.example.crystalfiles.model.Global.Companion.globalSwitchState
import java.io.File
import java.text.DecimalFormat


class CustomExpandableListAdapter internal constructor(
    private val resource:HashMap<Int, Array<Drawable?>>,
    private val context: Context,
    private val titleList: List<String>,
    private val dataList: HashMap<String, List<String>>
    ) : BaseExpandableListAdapter(){
        override fun getChild(listPosition: Int, expandedListPosition: Int):Any{
            return this.dataList[this.titleList[listPosition]]!![expandedListPosition]
        }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    override fun getChildView(
        listPosition: Int,
        expandedListPosition: Int,
        isLastChild: Boolean,
        convertView_: View?,
        parent: ViewGroup?
    ): View {
        var convertView = convertView_
        val expandedListText = getChild(listPosition, expandedListPosition) as String
        if (convertView == null){
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.expandable_list_childs, parent, false)
        }
        val expandedListTextView = convertView!!.findViewById<TextView>(R.id.listView_child)
        val expandedImageView = convertView.findViewById<ImageView>(R.id.image_view_child)
        val switchChild = convertView.findViewById<SwitchCompat>(R.id.switch_child)
        if (listPosition == 2) {
            switchChild.visibility = View.VISIBLE
            when(expandedListPosition){
                0 -> {
                    switchChild.isChecked = globalSwitchState
                }
                1 -> {
                    switchChild.isChecked = globalDarkModeSwitchState
                }
            }
        } else {
            switchChild.visibility = View.INVISIBLE
        }
        if (listPosition == 0 && expandedListPosition != 0){
            expandedImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.folder))



        } else {
            expandedImageView.setImageDrawable(resource[listPosition]!![expandedListPosition])
        }
        expandedListTextView.text = expandedListText
        expandedListTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
        return convertView
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return this.dataList[this.titleList[listPosition]]!!.size
    }

    override fun getGroup(listPosition: Int): Any {
        return this.titleList[listPosition]
    }

    override fun getGroupCount(): Int {
        return this.titleList.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }
    override fun getGroupView(
        listPosition: Int,
        isExpanded: Boolean,
        convertView_: View?,
        parent: ViewGroup?
    ): View {
        val convertView: View

        if(listPosition > 2){
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            if (listPosition >= drives.size + 3){
                convertView = layoutInflater.inflate(R.layout.expandable_list_childs, parent, false)
                val expandedListTextView = convertView.findViewById<TextView>(R.id.listView_child)
                val expandedImageView = convertView.findViewById<ImageView>(R.id.image_view_child)
                val switchChild = convertView.findViewById<SwitchCompat>(R.id.switch_child)
                expandedListTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
                switchChild.visibility = View.INVISIBLE
                if (listPosition == drives.size + 3){
                    expandedImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.folder))
                    expandedListTextView.text = context.getString(R.string.downloads)
                } else {
                    expandedImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_drive_icon))
                    expandedListTextView.text = context.getString(R.string.drive)
                }
            } else {
                convertView = layoutInflater.inflate(R.layout.expandable_list_childs_progress, parent, false)
                val expandedListTextView = convertView.findViewById<TextView>(R.id.listView_child_progress)
                val expandedImageView = convertView.findViewById<ImageView>(R.id.image_view_child_progress)
                val expandableProgressBar = convertView.findViewById<ProgressBar>(R.id.expandable_progress_bar)
                expandedImageView.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.sidebar_sdcard))
                val progresSizeView = convertView.findViewById<TextView>(R.id.listView_child_progress_size)
                val progresTotalView = convertView.findViewById<TextView>(R.id.listView_child_progress_total)


                expandedListTextView.text = titleList[listPosition]
                val thread = object: Thread() {
                    override fun run() {
                        super.run()
                        setPercent(listPosition, expandableProgressBar, progresSizeView, progresTotalView)
                    }
                }
                (thread as Thread).start()

            }
            return convertView
        }

        val listTitle = getGroup(listPosition) as String


        val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        convertView = layoutInflater.inflate(R.layout.list_item, parent, false)
        val listTitleTextView = convertView.findViewById<TextView>(R.id.listView)


        listTitleTextView.text = listTitle
        listTitleTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
        val indicator:ImageView = convertView.findViewById(R.id.indicator)

        if (isExpanded){
            indicator.setImageResource(R.drawable.access_tab_down)
        } else {
            indicator.setImageResource(R.drawable.access_tab_up)
        }
        return convertView
    }

    private fun setPercent(
        listPosition: Int,
        expandableProgressBar: ProgressBar,
        progresSizeView: TextView,
        progresTotalView: TextView
    ) {
        val path = drives[listPosition - 3]
        val file = File(path!!)
        val totalSpace = file.totalSpace
        val freeSpace = file.freeSpace
        val percent = 100 * freeSpace / totalSpace
        progresSizeView.text = bytesToHuman(freeSpace) + "/"
        progresTotalView.text = bytesToHuman(totalSpace)
        (context as Activity).runOnUiThread {
            expandableProgressBar.progress = 100 - percent.toInt()
        }
    }

    private fun floatForm(d: Double): String? {
        return DecimalFormat("#.##").format(d)
    }
    private fun bytesToHuman(size: Long): String? {
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

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }
}