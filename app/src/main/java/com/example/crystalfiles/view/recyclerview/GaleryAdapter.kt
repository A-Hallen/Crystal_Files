package com.example.crystalfiles.view.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.crystalfiles.R
import com.example.crystalfiles.model.GetMimeFile
import com.example.crystalfiles.model.imageFolder
import com.example.crystalfiles.model.list_files.LoadThumb
import java.io.File
import java.lang.NullPointerException


class PictureFolderAdapter(
    private val folders: ArrayList<imageFolder>,
    private val folderContx: Context,
    listen: itemClickListener,
    listenPressed:itemPressListener,
    private val select:Boolean = false
) :
    RecyclerView.Adapter<PictureFolderAdapter.FolderHolder>() {
    private val listenToClick: itemClickListener = listen
    private val listenToPress: itemPressListener = listenPressed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderHolder {
        val cell = LayoutInflater.from(parent.context).inflate(R.layout.galery_reciclerview,
            parent, false)
        return FolderHolder(cell)
    }

    override fun onBindViewHolder(holder: FolderHolder, position: Int) {
        val folder = folders[position]
        if (select){
            holder.check.visibility = View.VISIBLE
            holder.check.isChecked = folder.state
        }
        val mime = GetMimeFile(File(folder.path)).getmime().split("/")[0]
        if (mime == "image" || File(folder.path).isDirectory) Glide.with(folderContx).load(folder.firstPic).apply(RequestOptions().centerCrop()).into(holder.folderPic)
        else
        if (mime == "video") Glide.with(folderContx).asBitmap().load(folder.path).into(holder.folderPic)
        else if (folder.path.split(".").last() == "apk"){
            try {
                holder.folderPic.setImageDrawable(LoadThumb().getApkIcon(folderContx, folder.path))
            } catch (e: NullPointerException) {
                holder.folderPic.setImageDrawable(AppCompatResources.getDrawable(folderContx, android.R.drawable.sym_def_app_icon))
            }
        } else {
            val image = GetMimeFile(File(folder.path)).getImageFromExtension(holder.folderPic.context)
            holder.folderPic.setImageDrawable(image)
        }
        //Sets the name of the item
        val text:String = if (folder.numberOfPics != 0){
            "(" + folder.numberOfPics + ") " + folder.folderName
        } else {
            folder.folderName
        }
        holder.folderName.text = text

        //the Listeners
        holder.folderPic.setOnLongClickListener {
            listenToPress.onPicPressed(folders,folder.path,
                folder.folderName, position, holder.check)
            true
        }
        if (select){
            holder.folderPic.setOnClickListener {
                listenToClick.onPicClicked(folders, folder.path, folder.folderName, position, holder.check)
            }
        } else {
            holder.folderPic.setOnClickListener {
                listenToClick.onPicClicked(folders,folder.path,
                    folder.folderName, position)
            }
        }

    }

    override fun getItemCount(): Int {
        return folders.size
    }

    inner class FolderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var folderPic: ImageView = itemView.findViewById(R.id.image_galery)
        var folderName: TextView = itemView.findViewById(R.id.galeryText)
        var check: CheckBox      = itemView.findViewById(R.id.galeryCheck)

    }
}
