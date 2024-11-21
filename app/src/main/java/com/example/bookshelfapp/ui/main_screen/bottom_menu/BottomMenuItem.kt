package com.example.bookshelfapp.ui.main_screen.bottom_menu

import com.example.bookshelfapp.R

sealed class BottomMenuItem (
    val route: String,
    val title: String,
    val iconId: Int
) {
    object Main : BottomMenuItem(
        route = "main",
        title = "Каталог",
        iconId = R.drawable.ic_main
    )
    object Favs : BottomMenuItem(
        route = "favs",
        title = "Избранное",
        iconId = R.drawable.ic_fav
    )
    object Add : BottomMenuItem(
        route = "add",
        title = "Добавить",
        iconId = R.drawable.ic_add
    )
    object My: BottomMenuItem(
        route = "my",
        title = "Мои книги",
        iconId = R.drawable.ic_my
    )
}