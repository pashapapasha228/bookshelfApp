package com.example.bookshelfapp.ui.favouritebooks_screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.bookshelfapp.data.Book

@Composable
fun FavouriteBooksScreen(
    favouriteBooks: List<Book>,
    paddingValues: PaddingValues,
    onDelete: (Book) -> Unit,
    onReturn: (Book) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        items(favouriteBooks) { book ->
            FavouriteListItemUI(
                book = book,
                onDelete = onDelete,
                onReturn = onReturn
            )
        }
    }
}