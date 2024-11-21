package com.example.bookshelfapp.ui.login

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookshelfapp.ui.login.data.MainScreenDataObject
import com.example.bookshelfapp.ui.theme.myPurple
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun LoginScreen(
    onNavigateToMainScreen: (MainScreenDataObject) -> Unit
) {
    val auth = remember {
        Firebase.auth
    }

    val errorState = remember {
        mutableStateOf("")
    }

    val emailState = remember {
        mutableStateOf("")
    }

    val passwordState = remember {
        mutableStateOf("")
    }

    if(auth.currentUser != null) {
        onNavigateToMainScreen(
            MainScreenDataObject(
                auth.currentUser!!.uid,
                auth.currentUser!!.email!!
            )
        )
    }

    // #90D96D
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(myPurple)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 50.dp,
                end = 50.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Книжная полка",
            fontWeight = FontWeight.Bold,
            fontSize = 48.sp,
            fontFamily = FontFamily.Serif,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(15.dp))
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
        if (errorState.value.isNotEmpty()) {
            Text(
                text = errorState.value,
                color = Color.Red,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        LoginButton("Войти") {
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
        Spacer(modifier = Modifier.height(10.dp))
        LoginButton("Зарегистрироваться") {
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
        onSignUpFailure("Email или пароль не могут быть пустыми");
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
        onSignInFailure("Email или пароль не могут быть пустыми");
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