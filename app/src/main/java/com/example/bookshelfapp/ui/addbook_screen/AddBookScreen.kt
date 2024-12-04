package com.example.bookshelfapp.ui.addbook_screen

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
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

@SuppressLint("SuspiciousIndentation")
@Composable
fun AddBookScreen(
    context: Context,
    onSaved: () -> Unit,
    firestore: FirebaseFirestore,
    userId: String
) {
    val title = remember { mutableStateOf("") }
    val author = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    var selectedCategory = "Бестселлеры"
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri.value = uri
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(myPurple)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 50.dp, end = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Добавление новой книги",
            fontWeight = FontWeight.Bold,
            fontSize = 48.sp,
            fontFamily = FontFamily.Serif,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(15.dp))
        if (selectedImageUri.value != null)
            Image(
                painter = rememberAsyncImagePainter(model = selectedImageUri.value),
                contentDescription = "BG",
                modifier = Modifier.size(150.dp),
                contentScale = ContentScale.Crop
            )
        Spacer(modifier = Modifier.height(15.dp))
        RoundedCornerTextField(
            text = title.value,
            label = "Название"
        ) {
            title.value = it
        }
        Spacer(modifier = Modifier.height(15.dp))
        RoundedCornerTextField(
            text = author.value,
            label = "Автор"
        ) {
            author.value = it
        }
        Spacer(modifier = Modifier.height(15.dp))
        RoundedCornerDropDownMenu { selectedItem ->
            selectedCategory = selectedItem
        }
        Spacer(modifier = Modifier.height(15.dp))
        RoundedCornerTextField(
            maxLines = 5,
            singleLine = false,
            text = description.value,
            label = "Описание"
        ) {
            description.value = it
        }
        Spacer(modifier = Modifier.height(10.dp))
        LoginButton("Выбрать картинку") {
            imageLauncher.launch("image/*")
        }
        Spacer(modifier = Modifier.height(10.dp))
        LoginButton("Сохранить") {
            saveBookImage(
                selectedImageUri.value!!,
                context,
                firestore,
                Book(
                    title = title.value,
                    author = author.value,
                    description = description.value,
                    category = selectedCategory,
                    userAuthorId = userId
                ),
                onSaved = {
                    Toast.makeText(context, "Книга успешно добавлена!", Toast.LENGTH_SHORT).show()
                    onSaved() // Вызов коллбэка после сохранения
                },
                onError = {
                    Toast.makeText(context, "Ошибка при добавлении книги", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

private fun saveBookImage(
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

                if (url.isNotEmpty()) {
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