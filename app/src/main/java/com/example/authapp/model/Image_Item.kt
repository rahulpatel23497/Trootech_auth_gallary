package com.example.authapp.model

data class Image_Item(
    var id: String? = null,
    var author: String? = null,
    var width: Int? = null,
    var height: Int? = null,
    var url: String? = null,
    var download_url: String? = null,
    var date: String? = null,
    var selected: Boolean = false
)
