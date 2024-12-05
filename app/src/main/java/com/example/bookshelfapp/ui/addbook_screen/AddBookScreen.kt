package com.example.bookshelfapp.ui.addbook_screen

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.example.bookshelfapp.R
import com.example.bookshelfapp.data.Book
import com.example.bookshelfapp.ui.login.LoginButton
import com.example.bookshelfapp.ui.login.RoundedCornerTextField
import com.example.bookshelfapp.ui.theme.myPurple
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.Error

@Composable
fun AddBookScreen(
    context: Context,
    firestore: FirebaseFirestore,
    userId: String,
    onSaved: () -> Unit,
    book: Book? = null // Новый параметр для редактирования
) {
    val title = remember { mutableStateOf(book?.title ?: "") }
    val author = remember { mutableStateOf(book?.author ?: "") }
    val description = remember { mutableStateOf(book?.description ?: "") }
    val selectedCategory = remember { mutableStateOf(book?.category ?: "Бестселлеры") }
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri.value = uri
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(myPurple, Color.LightGray))) // Градиентный фон
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Text(
                text = if (book == null) "Добавление книги" else "Редактирование",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        item {
            if (selectedImageUri.value != null) {
                Image(
                    painter = rememberAsyncImagePainter(model = selectedImageUri.value),
                    contentDescription = "book cover",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(8.dp)) // Округленные углы
                        .border(2.dp, Color.Gray, RoundedCornerShape(8.dp)), // Граница
                    contentScale = ContentScale.Crop
                )
            }
            else if (book != null) {
                AsyncImage(
                    model = book.imageURL,
                    contentDescription = "book cover",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(2.dp, Color.Gray, RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop // Изменяем на Crop для лучшего отображения
                )
            }
            else {
                // Используйте изображение по умолчанию
                Image(
                    painter = painterResource(id = R.drawable.img), // Укажите правильный путь к изображению
                    contentDescription = "default book cover",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(2.dp, Color.Gray, RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }

        //Spacer(modifier = Modifier.height(16.dp))

        item{
            InputField(label = "Название", value = title.value) { title.value = it }
        }

//        Spacer(modifier = Modifier.height(16.dp))
        item {
            InputField(label = "Автор", value = author.value) { author.value = it }
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Категория",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            RoundedCornerDropDownMenu(
                initialOption = selectedCategory.value,
                onOptionSelected = { selectedItem ->
                    selectedCategory.value = selectedItem
                }
            )
        }
//        Spacer(modifier = Modifier.height(16.dp))
        item {
            InputField(label = "Описание", value = description.value, maxLines = 5) { description.value = it }
        }

//        Spacer(modifier = Modifier.height(16.dp))

        item {
            LoginButton("Выбрать картинку") {
                imageLauncher.launch("image/*")
            }
        }
//        Spacer(modifier = Modifier.height(16.dp))

        item {
            LoginButton(if (book == null) "Добавить книгу" else "Сохранить изменения") {
                if (book == null) {
                    if(selectedImageUri.value != null)
                    // Логика добавления новой книги
                    saveBookImage(
                        true,
                        selectedImageUri.value!!,
                        context,
                        firestore,
                        Book(
                            title = title.value,
                            author = author.value,
                            description = description.value,
                            category = selectedCategory.value,
                            userAuthorId = userId
                        ),
                        onSaved = {
                            Toast.makeText(context, "Книга успешно добавлена!", Toast.LENGTH_SHORT)
                                .show()
                            onSaved()
                        },
                        onError = {
                            Toast.makeText(
                                context,
                                "Ошибка при добавлении книги",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                    else
                        saveBookToFirestore(
                            firestore = firestore,
                            url = "https://ibb.co/PMv3rMn",
                            book = Book(
                                title = title.value,
                                author = author.value,
                                description = description.value,
                                category = selectedCategory.value,
                                userAuthorId = userId
                            ),
                            onSaved = {
                                Toast.makeText(context, "Книга успешно добавлена!", Toast.LENGTH_SHORT)
                                    .show()
                                onSaved()
                            },
                            onError = {
                                Toast.makeText(
                                    context,
                                    "Ошибка при добавлении книги",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        )
                } else {
                    // Логика обновления существующей книги
                    if (selectedImageUri.value != null)
                        saveBookImage(
                            false,
                            selectedImageUri.value!!,
                            context,
                            firestore,
                            Book(
                                key = book.key,
                                title = title.value,
                                author = author.value,
                                description = description.value,
                                category = selectedCategory.value,
                                userAuthorId = userId
                            ),
                            onSaved = {
                                Toast.makeText(
                                    context,
                                    "Книга успешно обновлена!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onSaved()
                            },
                            onError = {
                                Toast.makeText(
                                    context,
                                    "Ошибка при обновлении книги",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    else
                        updateBookInFirestore(
                            book.imageURL,
                            firestore,
                            Book(
                                key = book.key,
                                title = title.value,
                                author = author.value,
                                description = description.value,
                                category = selectedCategory.value,
                                userAuthorId = userId
                            ),
                            onSuccess = {
                                Toast.makeText(
                                    context,
                                    "Книга успешно обновлена!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onSaved()
                            }
                        )
                }
            }
        }
    }
}

@Composable
fun InputField(label: String, value: String, maxLines: Int = 1, onValueChange: (String) -> Unit) {
    Column {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        RoundedCornerTextField(
            text = value,
            label = label,
            maxLines = maxLines,
            singleLine = maxLines == 1,
            onValueChange = onValueChange
        )
    }
}

private fun updateBookInFirestore(
    url: String,
    db: FirebaseFirestore,
    book: Book,
    onSuccess: () -> Unit
) {
    val bookRef = db.collection("books").document(book.key)
    book.imageURL = url
    bookRef.set(book) // Используем set для обновления документа
        .addOnSuccessListener {
            Log.d("EditBookScreen", "Книга успешно обновлена: ${book.title}")
            onSuccess()
        }
        .addOnFailureListener { e ->
            Log.e("EditBookScreen", "Ошибка при обновлении книги", e)
        }
}

private fun saveBookImage(
    isCreateBook: Boolean,
    uri: Uri,
    context: Context,
    firestore: FirebaseFirestore,
    book: Book,
    onSaved: () -> Unit,
    onError: () -> Unit
) {
    val inputStream = context.contentResolver.openInputStream(uri)
    val bitmap = BitmapFactory.decodeStream(inputStream)
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos)

    val client = OkHttpClient()

    val base64Image = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)

    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("image", base64Image)
        .addFormDataPart("key", "ea27d0b9a26f3cb9f7baf3f50aae5bcd") // Замените на ваш API-ключ
        .build()

    val request = Request.Builder()
        .url("https://api.imgbb.com/1/upload")
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            println("Ошибка загрузки: ${e.message}")
            onError() // Вызов коллбэка при ошибке
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                val responseData = response.body?.string()
                val gson = Gson()
                val jsonObject = gson.fromJson(responseData, JsonObject::class.java)
                val dataObject = jsonObject.getAsJsonObject("data")
                val url = dataObject.get("url").asString

                if (url.isNotEmpty() && isCreateBook) {
                    saveBookToFirestore(
                        firestore,
                        url,
                        book,
                        onSaved = {
                            onSaved()
                        },
                        onError = {
                            onError()
                        }
                    )
                } else if(url.isNotEmpty() && !isCreateBook) {
                    updateBookInFirestore(
                        url,
                        firestore,
                        book,
                        onSuccess = onSaved
                    )
                }
            } else {
                println("Ошибка: ${response.code}")
                onError() // Вызов коллбэка при ошибке
            }
        }
    })
}

private fun saveBookToFirestore(
    firestore: FirebaseFirestore,
    url: String,
    book: Book,
    onSaved: () -> Unit,
    onError: () -> Unit
) {
    val db = firestore.collection("books")
    val key = db.document().id // Создаем уникальный ключ для книги
    db.document(key)
        .set(
            book.copy(
                key = key, // Добавляем ключ к объекту книги
                imageURL = url
            )
        )
        .addOnSuccessListener {
            onSaved()
        }
        .addOnFailureListener {
            onError()
        }
}