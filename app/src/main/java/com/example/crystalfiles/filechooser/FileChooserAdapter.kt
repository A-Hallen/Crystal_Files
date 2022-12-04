package com.example.crystalfiles.filechooser

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.crystalfiles.R
import com.example.crystalfiles.model.GetMimeFile
import com.example.crystalfiles.model.Global
import com.google.android.material.imageview.ShapeableImageView


class FileChooserAdapter: RecyclerView.Adapter<FileChooserAdapter.FileChooserViewHolder>() {
    private lateinit var mListener: OnItemClickListener
    interface OnItemClickListener{     fun onItemClick(position: Int)     }
    fun setOnItemClicKListener(listener: OnItemClickListener){  mListener = listener  }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FileChooserAdapter.FileChooserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.file_chooser_list_item, parent, false)
        val windowManager = parent.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width: Int = windowManager.defaultDisplay.width
        itemView.layoutParams = RecyclerView.LayoutParams(width, RecyclerView.LayoutParams.WRAP_CONTENT)
        return FileChooserViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: FileChooserAdapter.FileChooserViewHolder, position: Int) {
        val currentItem = Global.fcArrayList[position]
        holder.tvHeading.text = currentItem.heading
        if (currentItem.isdirectory){
            if (currentItem.storage)holder.titleImage.setImageResource(R.drawable.sidebar_sdcard) else{
                holder.titleImage.setImageResource(R.drawable.folder)}
        } else {
            if (currentItem.hasMimeType){
                try {
                    Glide.with(holder.titleImage.context).asBitmap().load(currentItem.path).into(holder.titleImage)
                    if (GetMimeFile(currentItem.path).getmime().split("/")[0] == "video"){
                        holder.supperpositionImage.visibility = View.VISIBLE
                    }
                } catch (e:Exception){
                    if (GetMimeFile(currentItem.path).getmime().split("/")[0] == "video"){
                        holder.titleImage
                            .setImageDrawable(ContextCompat.getDrawable(holder.layoutBackground.context, R.drawable.icon_video))
                        holder.supperpositionImage.visibility = View.VISIBLE
                    } else {
                        holder.titleImage
                            .setImageDrawable(ContextCompat.getDrawable(holder.layoutBackground.context, R.drawable.icon_image))
                    }
                }
            } else{
                holder.titleImage.setImageDrawable(currentItem.titleImageResource)
            }
        }
    }

    override fun getItemCount(): Int {
        return Global.fcArrayList.size
    }
    inner class FileChooserViewHolder(itemView: View, listener: OnItemClickListener): RecyclerView.ViewHolder(itemView){
        val titleImage: ShapeableImageView = itemView.findViewById(R.id.file_chooser_title_image)
        val tvHeading: TextView  = itemView.findViewById(R.id.file_chooser_tvHeading)
        val supperpositionImage: ImageView = itemView.findViewById(R.id.file_chooser_supperposition_image)
        val layoutBackground:LinearLayout = itemView.findViewById(R.id.file_chooser_layout_recicler)

        init {
            itemView.setOnClickListener {
            listener.onItemClick(adapterPosition)
        }
        }
    }
}