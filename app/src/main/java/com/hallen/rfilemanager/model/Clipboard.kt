package com.hallen.rfilemanager.model


class Clipboard(
    var source: List<String>? = null,
    var destiny: String? = null,
    var action: Action? = null,
) {
    enum class Action { MOVE, COPY }

    val size: Int get() = source?.size ?: 0

    fun clear() {
        source = emptyList()
    }

    fun none(): Boolean = source?.none() ?: true
    fun any(): Boolean = source?.any() ?: false

}
