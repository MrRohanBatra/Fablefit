package com.rohan.fablefit.Interface

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

interface ScreenInterFace {
    val title: String;
    val icon: ImageVector;
    @Composable
    fun UI();
}