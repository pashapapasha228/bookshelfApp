package com.example.bookshelfapp.ui.main_screen

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.bookshelfapp.data.Book
import com.example.bookshelfapp.ui.addbook_screen.RoundedCornerDropDownMenu
import com.example.bookshelfapp.ui.login.data.MainScreenDataObject
import com.example.bookshelfapp.ui.main_screen.bottom_menu.BottomMenu
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

//@Preview(showBackground = true)
@Composable
fun MainScreen(
    navData: MainScreenDataObject,
    context: Context,
    onButtonClick: (String) -> Unit,
    onExitClick: () -> Unit
) {
    val email = remember {
        mutableStateOf(navData.email)
    }

    val booksListState = remember {
        mutableStateOf(emptyList<Book>())
    }

    LaunchedEffect(Unit) {
        val db = Firebase.firestore
        getAllBooks(db) { books ->
            booksListState.value = books
        }
    }

    ModalNavigationDrawer(
        modifier = Modifier.fillMaxSize(),
        drawerContent = {
            Column(Modifier.fillMaxWidth(0.7f)) {
                DrawerHeader(email.value)
                DrawerBody(Firebase.auth) {
                    onExitClick()
                }
            }
        }
    ) {
        Scaffold (
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                BottomMenu { selectedItem ->
                    onButtonClick(selectedItem)
                }
            }
        ) { paddingValues ->
            LazyColumn (
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            ) {
                items(booksListState.value) { book ->
                    BookListItemUI(book)
                }
            }
        }
    }
}

private fun getAllBooks(
    db: FirebaseFirestore,
    onBooks: (List<Book>) -> Unit
) {
    db.collection("books")
        .get()
        .addOnSuccessListener { task ->
            onBooks(task.toObjects(Book::class.java))
        }
}