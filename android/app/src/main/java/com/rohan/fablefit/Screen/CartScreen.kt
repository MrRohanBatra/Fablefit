package com.rohan.fablefit.Screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.rohan.fablefit.Interface.ScreenInterFace

object CartScreen: ScreenInterFace {
    override val icon: ImageVector=Icons.Default.ShoppingCart;
    override val title: String="Cart";
    @Composable
    override fun UI() {
        Text("Cart Screen")
    }
}