package com.rohan.fablefit.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomRoute(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomRoute("home", "Home", Icons.Default.Home)
    object Search: BottomRoute(route = "search","Search",Icons.Default.Search)
    object Cart : BottomRoute("cart", "Cart", Icons.Default.ShoppingCart)
    object Profile : BottomRoute("profile", "Profile", Icons.Default.Person)
}