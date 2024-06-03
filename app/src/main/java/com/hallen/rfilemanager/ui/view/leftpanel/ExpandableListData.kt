package com.hallen.rfilemanager.ui.view.leftpanel

internal object ExpandableListData {

    val newData = LinkedHashMap<String, List<String>>()

    val data: LinkedHashMap<String, List<String>>
        get() {
            val expandableListDetail: LinkedHashMap<String, List<String>> = LinkedHashMap()

            val myMedia: MutableList<String> = ArrayList()
            myMedia.add(DrawerData.IMAGENES)
            myMedia.add(DrawerData.MUSIC)
            myMedia.add(DrawerData.MOVIES)
            myMedia.add(DrawerData.BOOKS)
            myMedia.add(DrawerData.APPS)

            val myHerramientas: MutableList<String> = ArrayList()
            myHerramientas.add(DrawerData.HIDDEN_FILES)
            myHerramientas.add(DrawerData.SPACE_ANALISIS)

            val myFav: MutableList<String> = ArrayList()
            myFav.add(DrawerData.FAVORITES)
            /*        for (favs in prefs.getFavLocation()){
                        myFav.add(favs)
                    }*/

            expandableListDetail["Favoritos"] = myFav
            expandableListDetail["Media"] = myMedia
            expandableListDetail["Heramientas"] = myHerramientas

            /*                    for (drive in drives){
                                    val file = File(drive!!)
                                    expandableListDetail[file.name] = listOf(file.absolutePath)
                                }*/
            expandableListDetail[DrawerData.DOWNLOAD] = ArrayList()

            return expandableListDetail
        }
}