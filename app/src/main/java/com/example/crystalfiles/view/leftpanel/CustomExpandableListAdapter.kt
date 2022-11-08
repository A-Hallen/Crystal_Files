package com.example.crystalfiles.view.leftpanel

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.example.crystalfiles.R
import com.example.crystalfiles.model.Global.Global.Companion.globalDarkModeSwitchState
import com.example.crystalfiles.model.Global.Global.Companion.globalSwitchState


class CustomExpandableListAdapter internal constructor(
    private val resource:HashMap<Int, Array<Drawable?>>,
    private val context: Context,
    private val titleList: List<String>,
    private val dataList: HashMap<String, List<String>>
    ) : BaseExpandableListAdapter(){
        override fun getChild(listPosition: Int, expandedListPosition: Int):Any{
            return this.dataList[this.titleList[listPosition]]!![expandedListPosition]
        }
    //set The toggle switch state of the child header so we can access it in other layouts
    private fun setChildToggleCheckedState(state: Boolean){
        this.setChildToggleCheckedState(state)
    }
    //get the toggle
    private fun getChildToggleCheckedState(){
        return getChildToggleCheckedState()
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }
    override fun getChildView(
        listPosition: Int,
        expandedListPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView = convertView
        val expandedListText = getChild(listPosition, expandedListPosition) as String
        if (convertView == null){
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.expandable_list_childs, null)
        }
        val expandedListTextView = convertView!!.findViewById<TextView>(R.id.listView_child)
        val expandedImageView = convertView.findViewById<ImageView>(R.id.image_view_child)
        val switchChild = convertView.findViewById<SwitchCompat>(R.id.switch_child)
        if (listPosition == 3) {
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
        expandedImageView.setImageDrawable(resource[listPosition]!![expandedListPosition])
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
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView = convertView
        val listTitle = getGroup(listPosition) as String
        if (convertView == null){
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.list_item, null)
        }
        val listTitleTextView = convertView!!.findViewById<TextView>(R.id.listView)
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

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }
}