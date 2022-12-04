package com.example.crystalfiles.search

import android.view.View
import android.widget.LinearLayout

fun hideSearch(normalAppBarMain: LinearLayout, searchLayout: LinearLayout){
    searchLayout.visibility = View.GONE
    normalAppBarMain.visibility = View.VISIBLE

}