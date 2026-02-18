package com.rohan.fablefit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.rohan.fablefit.Interface.ScreenInterFace
import com.rohan.fablefit.Screen.CartScreen
import com.rohan.fablefit.Screen.HomeScreen
import com.rohan.fablefit.Screen.ProfileScreen
import com.rohan.fablefit.ui.theme.FablefitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            setContent {
                FablefitTheme {

                    val screens = listOf<ScreenInterFace>(
                        HomeScreen,
                        CartScreen,
                        ProfileScreen,
                    )

                    var selectedScreen by remember { mutableStateOf(screens[0]) }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            NAVBAR(
                                screens = screens,
                                selectedScreen = selectedScreen,
                                onTabSelected = { selectedScreen = it }
                            )
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            selectedScreen.UI()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NAVBAR(
    screens: List<ScreenInterFace>,
    selectedScreen: ScreenInterFace,
    onTabSelected:(ScreenInterFace)-> Unit
){
    NavigationBar(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(30.dp)),
        tonalElevation = 10.dp,
    ) {
        screens.forEach { screen->
            NavigationBarItem(
                selected = selectedScreen == screen,
                onClick = {onTabSelected(screen)},
                icon = { Icon(screen.icon,screen.title) },
                label = {Text(screen.title)},
                alwaysShowLabel = false,
            )
        }
    }
}