package com.example.crystalfiles.view.recyclerview;

import android.widget.CheckBox;

import com.example.crystalfiles.model.imageFolder;

import java.util.ArrayList;

public interface itemPressListener {

    void onPicPressed(ArrayList<imageFolder> folders, String path, String folderName, int position, CheckBox check);
}
