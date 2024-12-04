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
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.bookshelfapp.R
import com.example.bookshelfapp.data.Book
import com.example.bookshelfapp.ui.addbook_screen.AddBookScreen
import com.example.bookshelfapp.ui.addbook_screen.RoundedCornerDropDownMenu
import com.example.bookshelfapp.ui.favouritebooks_screen.FavouriteBooksScreen
import com.example.bookshelfapp.ui.login.data.MainScreenDataObject
import com.example.bookshelfapp.ui.main_screen.bottom_menu.BottomMenu
import com.example.bookshelfapp.ui.mybooks_screen.MyBooksScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class NavItemState(
    val title: String,
    val iconId: Int
)

@Composable
fun MainScreen(
    navData: MainScreenDataObject,
    context: Context,
    onExitClick: () -> Unit
) {
    val db = remember { Firebase.firestore }
    val userId by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser?.uid) }

    val items = listOf(
        NavItemState(title = "Каталог", iconId = R.drawable.ic_main),
        NavItemState(title = "Избранное", iconId = R.drawable.ic_fav),
        NavItemState(title = "Добавить", iconId = R.drawable.ic_add),
        NavItemState(title = "Мои книги", iconId = R.drawable.ic_my)
    )

    var bottomNavState by remember { mutableStateOf(0) }
    var booksListState by remember { mutableStateOf(emptyList<Book>()) }
    var favouriteBooksState by remember { mutableStateOf(emptyList<Book>()) }
    var myBooksState by remember { mutableStateOf(emptyList<Book>()) }

    // Получаем все книги при первом запуске
    LaunchedEffect(Unit) {
        fetchAllBooks(db) { books ->
            booksListState = books
        }
        fetchFavouriteBooksForCurrentUser(db, userId) { books ->
            favouriteBooksState = books
        }
        fetchMyBooksForCurrentUser(db, userId) { books ->
            myBooksState = books
        }
    }

    ModalNavigationDrawer(
        modifier = Modifier.fillMaxSize(),
        drawerContent = {
            Column(Modifier.fillMaxWidth(0.7f)) {
                DrawerHeader(navData.email)
                DrawerBody(FirebaseAuth.getInstance()) {
                    onExitClick()
                }
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = bottomNavState == index,
                            onClick = {
                                bottomNavState = index
                                if (index == 0) { // Главный экран
                                    fetchAllBooks(db) { books ->
                                        booksListState = books
                                    }
                                }
                                if (index == 1) { // Избранное
                                    fetchFavouriteBooksForCurrentUser(db, userId) { books ->
                                        favouriteBooksState = books
                                    }
                                }
                                if(index == 3) { // Мои книги
                                    fetchMyBooksForCurrentUser(db, userId) { books ->
                                        myBooksState = books
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(id = item.iconId),
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(text = item.title)
                            }
                        )
                    }
                }
            }
        ) { paddingValues ->
            when (bottomNavState) {
                0 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        items(booksListState) { book ->
                            BookListItemUI(book) { selectedBook ->
                                addToFavorites(selectedBook, db, userId) {
                                    fetchAllBooks(db) { books ->
                                        booksListState = books
                                    }
                                    fetchFavouriteBooksForCurrentUser(db, userId) { books ->
                                        favouriteBooksState = books
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    FavouriteBooksScreen(
                        favouriteBooks = favouriteBooksState,
                        paddingValues = paddingValues,
                        onDelete = { book ->
                            deleteBook(book, db) {
                                fetchFavouriteBooksForCurrentUser(db, userId) { books ->
                                    favouriteBooksState = books
                                }
                            }
                        },
                        onReturn = { book ->
                            removeFromFavorites(book, db) {
                                fetchFavouriteBooksForCurrentUser(db, userId) { books ->
                                    favouriteBooksState = books
                                }
                            }
                        }
                    )
                }
                2 -> {
                    AddBookScreen(
                        context = context,
                        firestore = db,
                        userId = userId.toString(),
                        onSaved = {
                            fetchAllBooks(db) { books ->
                                booksListState = books
                            }
                        }
                    )
                }
                3 -> {
                    MyBooksScreen(
                        myBooks = myBooksState,
                        paddingValues = paddingValues,
                        onDelete = { book ->
                            deleteBook(book, db) {
                                fetchMyBooksForCurrentUser(db, userId) { books ->
                                    myBooksState = books
                                }
                            }
                        },
                        onChange = {

                        }
                    )
                }
            }
        }
    }
}

private fun fetchAllBooks(
    db: FirebaseFirestore,
    onBooks: (List<Book>) -> Unit
) {
    db.collection("books")
        .whereEqualTo("userFavouriteId", "")
        .get()
        .addOnSuccessListener { task ->
            onBooks(task.toObjects(Book::class.java))
        }
        .addOnFailureListener { e ->
            Log.e("MainScreen", "Ошибка при получении книг", e)
        }
}

private fun fetchFavouriteBooksForCurrentUser(
    db: FirebaseFirestore,
    userId: String?,
    onBooks: (List<Book>) -> Unit
) {
    if (userId == null) return
    db.collection("books")
        .whereEqualTo("userFavouriteId", userId)
        .get()
        .addOnSuccessListener { task ->
            onBooks(task.toObjects(Book::class.java))
        }
        .addOnFailureListener { e ->
            Log.e("MainScreen", "Ошибка при получении избранных книг", e)
        }
}

private fun fetchMyBooksForCurrentUser(
    db: FirebaseFirestore,
    userId: String?,
    onBooks: (List<Book>) -> Unit
) {
    if (userId == null) return
    db.collection("books")
        .whereEqualTo("userAuthorId", userId)
        .get()
        .addOnSuccessListener { task ->
            onBooks(task.toObjects(Book::class.java))
        }
        .addOnFailureListener { e ->
            Log.e("MainScreen", "Ошибка при получении моих книг", e)
        }
}

// Функция для добавления книги в избранное
private fun addToFavorites(
    book: Book,
    db: FirebaseFirestore,
    userId: String?,
    onSuccess: () -> Unit
) {
    if (userId == null) return
    val bookRef = db.collection("books").document(book.key)
    bookRef.update("userFavouriteId", userId)
        .addOnSuccessListener {
            Log.d("MainScreen", "Книга добавлена в избранное: ${book.title}")
            onSuccess()
        }
        .addOnFailureListener { e ->
            Log.e("MainScreen", "Ошибка при добавлении книги в избранное", e)
        }
}

// Функция для удаления книги из избранного
private fun removeFromFavorites(
    book: Book,
    db: FirebaseFirestore,
    onSuccess: () -> Unit
) {
    val bookRef = db.collection("books").document(book.key)
    bookRef.update("userFavouriteId", "")
        .addOnSuccessListener {
            Log.d("MainScreen", "Книга удалена из избранного: ${book.title}")
            onSuccess()
        }
        .addOnFailureListener { e ->
            Log.e("MainScreen", "Ошибка при удалении книги из избранного", e)
        }
}

// Функция для удаления книги по её идентификатору
private fun deleteBook(
    book: Book,
    db: FirebaseFirestore,
    onSuccess: () -> Unit
) {
    val bookRef = db.collection("books").document(book.key)
    bookRef.delete()
        .addOnSuccessListener {
            Log.d("MainScreen", "Книга успешно удалена: ${book.title}")
            onSuccess()
        }
        .addOnFailureListener { e ->
            Log.e("MainScreen", "Ошибка при удалении книги", e)
        }
}