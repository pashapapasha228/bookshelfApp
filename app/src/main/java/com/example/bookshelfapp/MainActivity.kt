package com.example.bookshelfapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil3.compose.AsyncImage
import com.example.bookshelfapp.data.Book
import com.example.bookshelfapp.ui.addbook_screen.AddBookScreen
import com.example.bookshelfapp.ui.addbook_screen.data.AddScreenObject
import com.example.bookshelfapp.ui.login.LoginScreen
import com.example.bookshelfapp.ui.login.data.LoginScreenObject
import com.example.bookshelfapp.ui.login.data.MainScreenDataObject
import com.example.bookshelfapp.ui.main_screen.MainScreen
import com.example.bookshelfapp.ui.main_screen.bottom_menu.BottomMenuItem
import com.example.bookshelfapp.ui.theme.BookShelfAppTheme
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
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

class MainActivity : ComponentActivity() {
    init {
        System.loadLibrary("native-lib")
    }

    external fun getApiKey(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current

            NavHost(
                navController = navController,
                startDestination = LoginScreenObject
            ) {
                composable<LoginScreenObject> {
                    LoginScreen { navData ->
                        navController.navigate(navData)
                    }
                }

                composable<MainScreenDataObject> { navEntry ->
                    val navData = navEntry.toRoute<MainScreenDataObject>()
                    MainScreen(navData, context,
                        apiKey = getApiKey(),
//                        apiKey = "ea27d0b9a26f3cb9f7baf3f50aae5bcd",
                        onExitClick = {
                            navController.navigate(LoginScreenObject)
                        })
//                    { bottomRoute ->
//                        navController.navigate(AddScreenObject)
//                    }
                }

//                composable<AddScreenObject> {
//                    AddBookScreen(context)
//                }
            }
        }
    }
}

//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        //enableEdgeToEdge()
//        setContent {
//            val fs = Firebase.firestore
//
//            val launcher = rememberLauncherForActivityResult(
//                contract = ActivityResultContracts.PickVisualMedia()
//            ) { uri ->
//                if(uri == null) return@rememberLauncherForActivityResult
//                val imageBytes = bitmapToByteArray(this, uri)
//                uploadImage(imageBytes) { url ->
//                    saveBook(fs, url)
//                }
//            }
//
//            MainScreen {
//                launcher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
//            }
//        }
//    }
//}
//
//@Composable
//fun MainScreen(onClick: () -> Unit) {
//    val context = LocalContext.current
//    val fs = Firebase.firestore
//
//    val list = remember {
//        mutableStateOf(emptyList<Book>())
//    }
//
//    fs.collection("books").addSnapshotListener { snapShot, exception ->
//        list.value = snapShot?.toObjects(Book::class.java) ?: emptyList()
//    }
//
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.SpaceBetween
//    ) {
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxWidth()
//                .fillMaxHeight(0.8f),
//
//            ) {
//            items(list.value) { book ->
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(10.dp)
//                ) {
//                    Row(modifier = Modifier.fillMaxWidth()) {
//                        AsyncImage(
//                            model = book.imageURL,
//                            contentDescription = "",
//                            modifier = Modifier.height(100.dp)
//                        )
//                        Text(
//                            text = book.name, modifier = Modifier
//                                .fillMaxWidth()
//                                .wrapContentWidth()
//                                .padding(15.dp)
//                        )
//                    }
//
//                }
//            }
//        }
//        Spacer(modifier = Modifier.height(10.dp))
//        Button(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(10.dp),
//            onClick = {
//                onClick()
//            }) {
//            Text(
//                text = "Добавить книгу"
//            )
//        }
//
//    }
//}
//
//private fun bitmapToByteArray(context: Context, uri: Uri): ByteArray {
//    val inputStream = context.contentResolver.openInputStream(uri)
//    val bitmap = BitmapFactory.decodeStream(inputStream)
//    //val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ioba)
//    val baos = ByteArrayOutputStream()
//    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos)
//    return baos.toByteArray()
//}
//
//private fun saveBook(fs: FirebaseFirestore, url: String) {
//    fs.collection("books").document().set(
//        Book(
//            "Книга книг",
//            "Цуба",
//            "Шота",
//            "fiction",
//            url
//        )
//    )
//}
//
//private fun uploadImage(imageBytes: ByteArray, onSuccess: (String) -> Unit) {
//    val client = OkHttpClient();
//
//    val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
//
//    val requestBody = MultipartBody.Builder()
//        .setType(MultipartBody.FORM)
//        .addFormDataPart("image", base64Image)
//        .addFormDataPart("key", "ea27d0b9a26f3cb9f7baf3f50aae5bcd") // Замените на ваш API-ключ
//        .build()
//
//    val request = Request.Builder()
//        .url("https://api.imgbb.com/1/upload")
//        .post(requestBody)
//        .build()
//
//    client.n ewCall(request).enqueue(object : Callback {
//        override fun onFailure(call: Call, e: IOException) {
//            println("Ошибка загрузки: ${e.message}")
//        }
//
//        override fun onResponse(call: Call, response: Response) {
//            if (response.isSuccessful) {
//                val responseData = response.body?.string()
//                // Извлечение URL из ответа
//                val url = extractUrlFromResponse(responseData)
//                onSuccess(url)
//            } else {
//                println("Ошибка: ${response.code}")
//            }
//        }
//    })
//}
//
//private fun extractUrlFromResponse(responseData: String?): String {
//    if (responseData.isNullOrEmpty()) return ""
//
//    val gson = Gson()
//    val jsonObject = gson.fromJson(responseData, JsonObject::class.java)
//    val dataObject = jsonObject.getAsJsonObject("data")
//
//    return dataObject.get("url").asString
//}
