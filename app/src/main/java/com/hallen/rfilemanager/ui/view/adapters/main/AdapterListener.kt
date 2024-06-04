package com.hallen.rfilemanager.ui.view.adapters.main

interface AdapterListener {
    fun onClick(adapterPosition: Int)
    fun onLongClick(adapterPosition: Int): Boolean
    fun onCheck(adapterPosition: Int)
}