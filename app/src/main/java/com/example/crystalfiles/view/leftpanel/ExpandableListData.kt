package com.example.crystalfiles.view.leftpanel

import com.example.crystalfiles.model.Global.Global.Companion.drives
import java.io.File

internal object  ExpandableListData {
    val data: HashMap<String, List<String>>
    get(){
        val expandableListDetail =  HashMap<String, List<String>>()

        val myLocal:MutableList<String> = ArrayList()
        for (drive in drives){
            myLocal.add(File(drive!!).name)
        }

        val myMedia:MutableList<String> = ArrayList()
        myMedia.add("Imagenes")
        myMedia.add("Musica")
        myMedia.add("Peliculas")
        myMedia.add("Libros")
        myMedia.add("App")

        val myHerramientas:MutableList<String> = ArrayList()
        myHerramientas.add("archivos ocultos")
        myHerramientas.add("Dark mode")

        val myFav: MutableList<String> = ArrayList()
        myFav.add("Add")

        expandableListDetail["Local"]  = myLocal
        expandableListDetail["Media"]  = myMedia
        expandableListDetail["Heramientas"]  = myHerramientas
        expandableListDetail["Favoritos"] = myFav
        return expandableListDetail


    }
}