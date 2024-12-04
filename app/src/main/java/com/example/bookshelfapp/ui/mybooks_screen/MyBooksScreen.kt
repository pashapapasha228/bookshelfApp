package com.example.bookshelfapp.ui.mybooks_screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.bookshelfapp.data.Book

@Composable
fun MyBooksScreen(
    myBooks: List<Book>,
    paddingValues: PaddingValues,
    onDelete: (Book) -> Unit,
    onChange: (Book) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        items(myBooks) { book ->
            MyListItemUI(
                book = book,
                onDelete = onDelete,
                onChange = onChange
            )
        }
    }
}