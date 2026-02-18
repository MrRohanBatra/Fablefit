package com.rohan.fablefit.Screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rohan.fablefit.Interface.ScreenInterFace

object ProfileScreen: ScreenInterFace {
    override val title="Profile";
    override val icon=Icons.Default.Person;
    @Composable
    override fun UI(){
        Text("Profile Screen");
    }
}