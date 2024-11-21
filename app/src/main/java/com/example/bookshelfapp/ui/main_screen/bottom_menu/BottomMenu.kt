package com.example.bookshelfapp.ui.main_screen.bottom_menu

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource

@Composable
fun BottomMenu(
    onButtonClick: (String) -> Unit
) {
    val items = listOf(
        BottomMenuItem.Main,
        BottomMenuItem.Favs,
        BottomMenuItem.Add,
        BottomMenuItem.My
    )

    val selectedItem = remember {
        mutableStateOf("main")
    }

    NavigationBar {
        items.forEach{ item ->
            NavigationBarItem(
                selected = selectedItem.value == item.route,
                onClick = {
                    selectedItem.value = item.route
                    onButtonClick(selectedItem.value)
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