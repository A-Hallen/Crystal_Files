package com.hallen.rfilemanager.ui.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import com.hallen.rfilemanager.R
import com.hallen.rfilemanager.databinding.ListOnClickDialogBinding
import com.hallen.rfilemanager.model.LaunchActivity

class ActivitiesAdapter(
    private val context: Context,
    private val arrayIdList: ArrayList<LaunchActivity>,
) :
    BaseAdapter() {
    override fun getCount(): Int = arrayIdList.size
    override fun getItem(position: Int): Any = arrayIdList[position]
    override fun getItemId(position: Int): Long = position.toLong()

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        val binding = ListOnClickDialogBinding.inflate(inflater)
        val item = arrayIdList[position]
        binding.tvDialog.text = item.textIdList
        binding.tvDialog.setTextColor(ContextCompat.getColor(context, R.color.white))
        binding.ivDialog.setImageDrawable(item.image)
        return binding.root
    }
}