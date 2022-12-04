package com.example.crystalfiles.texteditor
import java.util.*

class WordIndex {
    fun findWord(textString: String, word: String): List<Int> {
        val indexes: MutableList<Int> = ArrayList()
        val lowerCaseTextString = textString.lowercase(Locale.getDefault())
        val lowerCaseWord = word.lowercase(Locale.getDefault())
        var index = 0
        while (index != -1) {
            index = lowerCaseTextString.indexOf(lowerCaseWord, index)
            if (index != -1) {
                indexes.add(index)
                index++
            }
        }
        return indexes
    }
}