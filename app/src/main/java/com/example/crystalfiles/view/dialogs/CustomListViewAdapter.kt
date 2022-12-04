package com.example.crystalfiles.view.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.crystalfiles.R
import com.example.crystalfiles.view.recyclerview.Lis

class CustomListViewAdapter(private val activity: Activity, private val arrayIdList: ArrayList<Lis>):
    BaseAdapter() {
    override fun getCount(): Int {
       return arrayIdList.size
    }

    override fun getItem(position: Int): Any {
        return arrayIdList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.list_on_click_dialog, parent, false)
        val imageView:ImageView = rowView.findViewById(R.id.iv_dialog)
        val textView:TextView   = rowView.findViewById(R.id.tv_dialog)
        textView.text = arrayIdList[position].textIdList
        textView.setTextColor(ContextCompat.getColor(textView.context, R.color.white))
        imageView.setImageDrawable(arrayIdList[position].imageIdList)

        return rowView
    }

}