package com.example.crystalfiles.view.recyclerview

    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.*
    import androidx.constraintlayout.widget.ConstraintLayout
    import androidx.core.content.ContextCompat
    import androidx.recyclerview.widget.RecyclerView
    import com.bumptech.glide.Glide
    import com.example.crystalfiles.R
    import com.example.crystalfiles.model.GetMimeFile
    import com.example.crystalfiles.model.Global.Companion.scale
    import com.google.android.material.imageview.ShapeableImageView

class MyAdapter(private val newsList: ArrayList<News>):RecyclerView.Adapter<MyAdapter.MyViewHolder>(){
        private lateinit var mListener:OnItemClickListener
        private lateinit var lListener:OnItemLongClickListener
        private lateinit var cbListener:OnItemCheckListener
        interface OnItemCheckListener{     fun onItemCheck(position: Int, checkBox: CheckBox, layoutBackground:ConstraintLayout)}
        interface OnItemClickListener{     fun onItemClick(position: Int)     }
        interface OnItemLongClickListener{ fun onItemLongClick(position: Int, view: View) }

        fun setOnItemCheckListener(cblistener: OnItemCheckListener){ cbListener = cblistener}
        fun setOnLongItemClickListener(longlistener: OnItemLongClickListener) { lListener = longlistener }
        fun setOnItemClicKListener(listener:OnItemClickListener){  mListener = listener  }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_grid,
            parent, false)
            return MyViewHolder(itemView, mListener, lListener, cbListener)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val currentItem = newsList[position]
            val params = holder.listItemRelativeLayout.layoutParams
            when(scale){
                2 -> {params.width = 350; params.height = 350}
                3 -> {params.width = 300; params.height = 300}
                4 -> {params.width = 200; params.height = 200}
                5 -> {params.width = 175; params.height = 175}
                6 -> {params.width = 150; params.height = 150}
            }
            holder.listItemRelativeLayout.layoutParams = params
            if (currentItem.isdirectory){
                holder.titleImage.setImageResource(R.drawable.folder)
            } else {
                //Glide.with(holder.titleImage.context).asDrawable().load(currentItem.titleImageResource).into(holder.titleImage)

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


            holder.cbSelect.isChecked = currentItem.state
            if (currentItem.state) holder.layoutBackground.setBackgroundColor(ContextCompat.getColor(holder.layoutBackground.context, R.color.glass))
            else holder.layoutBackground.setBackgroundColor(ContextCompat.getColor(holder.layoutBackground.context, R.color.transparente))

            holder.tvHeading.text = currentItem.heading

            if (newsList[position].cbVisibility){
                holder.llContainer.visibility = View.VISIBLE
            }

        }

        override fun getItemCount(): Int {    return newsList.size     }

        inner class MyViewHolder(itemView: View, listener: OnItemClickListener, longlistener: OnItemLongClickListener, cblistener: OnItemCheckListener):RecyclerView.ViewHolder(itemView){
            val titleImage: ShapeableImageView = itemView.findViewById(R.id.title_image_grid)
            val tvHeading:TextView = itemView.findViewById(R.id.tvHeading_grid)
            val llContainer:LinearLayout = itemView.findViewById(R.id.llContainer_grid)
            val cbSelect:CheckBox = itemView.findViewById(R.id.cbSelect_grid)
            val layoutBackground:ConstraintLayout = itemView.findViewById(R.id.layout_recicler_grid)
            val supperpositionImage: ImageView = itemView.findViewById(R.id.supperposition_image)
            val listItemRelativeLayout: RelativeLayout = itemView.findViewById(R.id.list_item_relative_layout)

            init{
                llContainer.setOnClickListener {
                    cblistener.onItemCheck(adapterPosition, cbSelect, layoutBackground)
                }
                itemView.setOnLongClickListener{
                    longlistener.onItemLongClick(adapterPosition, itemView)
                    return@setOnLongClickListener true
                }
                itemView.setOnClickListener {
                    listener.onItemClick(adapterPosition)
                }
            }
        }

}