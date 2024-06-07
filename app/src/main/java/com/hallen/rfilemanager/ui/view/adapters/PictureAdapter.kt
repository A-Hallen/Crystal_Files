package com.hallen.rfilemanager.ui.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.hallen.rfilemanager.R
import com.hallen.rfilemanager.ui.view.activities.MediaPhoto
import com.hallen.rfilemanager.ui.view.activities.OnPictureClickListener

class PictureAdapter(
    private val pictures: ArrayList<MediaPhoto>,
    listen: OnPictureClickListener
) :
    RecyclerView.Adapter<PictureAdapter.PictureViewHolder>() {
    private val listenToClick: OnPictureClickListener = listen
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.picture_view_recicler_inflated, parent, false)
        return PictureViewHolder(view)
    }

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        val currentItem = pictures[position]
        holder.imagen.setOnClickListener {
            listenToClick.onClick(position, currentItem.name, holder.imagen)
        }
        Glide.with(holder.imagen.context).load(currentItem)
            .thumbnail(0.5f).placeholder(R.color.black)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(R.drawable.icon_image).into(holder.imagen)
    }

    override fun getItemCount(): Int = pictures.size

    fun updatePictures(pictures: ArrayList<MediaPhoto>) {
        this.pictures.clear()
        this.pictures.addAll(pictures)
        notifyDataSetChanged()
    }

    class PictureViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imagen: ImageView = view.findViewById(R.id.picture_iv)
    }
}