package com.example.bookshelfapp.ui.login

import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookshelfapp.R
import com.example.bookshelfapp.ui.login.data.MainScreenDataObject
import com.example.bookshelfapp.ui.theme.myPurple
import com.example.bookshelfapp.ui.theme.mySalad
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun LoginScreen(
    onNavigateToMainScreen: (MainScreenDataObject) -> Unit
) {
    val auth = remember { Firebase.auth }

    val errorState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }

    if (auth.currentUser != null) {
        onNavigateToMainScreen(
            MainScreenDataObject(
                auth.currentUser!!.uid,
                auth.currentUser!!.email!!
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(myPurple)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Книжная полка",
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center,
                color = mySalad
            )
            Spacer(modifier = Modifier.height(15.dp))

            Image(
                painter = painterResource(id = R.drawable.latindictionary), // Укажите правильный путь к изображению
                contentDescription = "default book cover",
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(2.dp, mySalad, RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(30.dp))

            RoundedCornerTextField(
                text = emailState.value,
                label = "Email"
            ) {
                emailState.value = it
            }
            Spacer(modifier = Modifier.height(15.dp))

            RoundedCornerTextField(
                text = passwordState.value,
                label = "Пароль"
            ) {
                passwordState.value = it
            }
            Spacer(modifier = Modifier.height(10.dp))

            if (errorState.value.isNotEmpty()) {
                Text(
                    text = errorState.value,
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if (validateInput(emailState.value, passwordState.value, errorState)) {
                        signIn(
                            auth,
                            emailState.value,
                            passwordState.value,
                            onSignInSuccess = { navData ->
                                onNavigateToMainScreen(navData)
                            },
                            onSignInFailure = { error ->
                                errorState.value = error
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .background(myPurple),
                content = { Text("Войти", color = Color.White) }
            )

            Button(
                onClick = {
                    if (validateInput(emailState.value, passwordState.value, errorState)) {
                        signUp(
                            auth,
                            emailState.value,
                            passwordState.value,
                            onSignUpSuccess = { navData ->
                                onNavigateToMainScreen(navData)
                            },
                            onSignUpFailure = { error ->
                                errorState.value = error
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .background(myPurple),
                content = { Text("Зарегистрироваться", color = Color.White) }
            )
        }
    }
}

private fun validateInput(email: String, password: String, errorState: MutableState<String>): Boolean {
    return when {
        email.isBlank() -> {
            errorState.value = "Email не может быть пустым"
            false
        }
        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
            errorState.value = "Введите корректный Email"
            false
        }
        password.isBlank() -> {
            errorState.value = "Пароль не может быть пустым"
            false
        }
        password.length < 6 -> {
            errorState.value = "Пароль должен содержать не менее 6 символов"
            false
        }
        else -> {
            errorState.value = "" // Сброс ошибки
            true
        }
    }
}

private fun signUp(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSignUpSuccess: (MainScreenDataObject) -> Unit,
    onSignUpFailure: (String) -> Unit
) {
    if (email.isBlank() || password.isBlank()) {
        onSignUpFailure("Email или пароль не могут быть пустыми")
        return
    }
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSignUpSuccess(
                    MainScreenDataObject(
                        task.result.user?.uid!!,
                        task.result.user?.email!!
                    )
                )
            }
        }
        .addOnFailureListener {
            onSignUpFailure(it.message ?: "Ошибка регистрации")
        }

}

private fun signIn(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSignInSuccess: (MainScreenDataObject) -> Unit,
    onSignInFailure: (String) -> Unit
) {
    if (email.isBlank() || password.isBlank()) {
        onSignInFailure("Email или пароль не могут быть пустыми")
        return
    }
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSignInSuccess(
                    MainScreenDataObject(
                        task.result.user?.uid!!,
                        task.result.user?.email!!
                    )
                )
            }
        }
        .addOnFailureListener {
            onSignInFailure(it.message ?: "Ошибка входа")
        }

}

private fun signOut(auth: FirebaseAuth) {
    auth.signOut()
}