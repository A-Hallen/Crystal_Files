package com.example.crystalfiles.view.recyclerview

import android.widget.CheckBox
import com.example.crystalfiles.model.imageFolder
import java.util.ArrayList

interface itemClickListener {
    //void onPicClicked(PicHolder holder, int position, ArrayList<pictureFacer> pics);
    fun onPicClicked(
        folders: ArrayList<imageFolder>,
        pictureFolderPath: String?,
        folderName: String?,
        position: Int,
        check: CheckBox? = null
    )
}