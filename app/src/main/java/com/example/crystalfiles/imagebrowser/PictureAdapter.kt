package com.example.crystalfiles.imagebrowser

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.crystalfiles.R
import com.example.crystalfiles.view.recyclerview.Fotos
import com.example.crystalfiles.view.recyclerview.PictureClickListener

class PictureAdapter(private val fotos:ArrayList<Fotos>, listen: PictureClickListener):RecyclerView.Adapter<PictureAdapter.PictureViewHolder>() {
    private val listenToClick: PictureClickListener = listen
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.picture_view_recicler_inflated, parent, false)
        return PictureViewHolder(view)
    }

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        val currentItem = fotos[position]
        holder.imagen.setOnClickListener {
            listenToClick.onClick(position, currentItem.file.name, holder.imagen)
        }
        Glide.with(holder.imagen.context).asDrawable().load(currentItem.file).into(holder.imagen)
    }

    override fun getItemCount(): Int {
        return fotos.size
    }
    class PictureViewHolder(view:View):RecyclerView.ViewHolder(view) {
        val imagen:ImageView = view.findViewById(R.id.picture_iv)
    }
}