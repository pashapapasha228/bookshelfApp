package com.example.bookshelfapp.data

data class Book(
    val key: String = "",
    val title: String = "",
    val author: String = "",
    val description: String = "",
    var imageURL: String = "",
    val category: String = "",
    val userAuthorId: String = "",
    val userFavouriteId: String = ""
)
