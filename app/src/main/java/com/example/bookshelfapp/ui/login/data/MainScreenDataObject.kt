package com.example.bookshelfapp.ui.login.data

import kotlinx.serialization.Serializable

@Serializable
data class MainScreenDataObject(
    val uid: String = "",
    val email: String = ""
)
