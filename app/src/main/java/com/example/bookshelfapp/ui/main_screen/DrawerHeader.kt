package com.example.bookshelfapp.ui.main_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookshelfapp.ui.theme.myPurple

@Composable
fun DrawerHeader(
    email: String
) {
    Column (
        Modifier.fillMaxWidth()
            .height(170.dp)
            .background(myPurple),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Книжная полка",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = email,
            color = Color.White,
            fontSize = 16.sp,
        )
    }
}