package com.example.crystalfiles.view.leftpanel

internal object  ExpandableListData {
    val data: HashMap<String, List<String>>
    get(){
        val expandableListDetail =  HashMap<String, List<String>>()

        val myLocal:MutableList<String> = ArrayList()
        myLocal.add("SD Card")
        myLocal.add("Memoria Interna")

        val myMedia:MutableList<String> = ArrayList()
        myMedia.add("Imagenes")
        myMedia.add("Musica")
        myMedia.add("Peliculas")
        myMedia.add("Libros")

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