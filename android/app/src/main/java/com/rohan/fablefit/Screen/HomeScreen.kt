package com.rohan.fablefit.Screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.rohan.fablefit.Interface.ScreenInterFace

object HomeScreen: ScreenInterFace {
    override val title: String="Home";
    override val icon: ImageVector=Icons.Default.Home;

    @Composable
    override fun UI() {
        Text("Home Screen");
    }

}