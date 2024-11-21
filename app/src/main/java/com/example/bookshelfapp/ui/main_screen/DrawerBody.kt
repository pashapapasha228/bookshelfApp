package com.example.bookshelfapp.ui.main_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bookshelfapp.ui.theme.mySalad
import com.google.firebase.auth.FirebaseAuth

@Composable
fun DrawerBody(
    da: FirebaseAuth,
    onExitClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = {
                da.signOut()
                onExitClick()
            },
            modifier = Modifier.fillMaxWidth()
                .padding(5.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = mySalad
            )
        ) {
            Text(text = "Выйти из аккаунта")
        }
    }
}