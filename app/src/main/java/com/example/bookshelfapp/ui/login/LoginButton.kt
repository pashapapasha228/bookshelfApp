package com.example.bookshelfapp.ui.login

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.bookshelfapp.ui.theme.mySalad

@Composable
fun LoginButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = {
            onClick()
        },
            modifier = Modifier.fillMaxWidth(0.75f),
            colors = ButtonDefaults.buttonColors(
                containerColor = mySalad
            )
        ) {
        Text(text = text)
    }
}