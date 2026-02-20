package com.rohan.fablefit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.rohan.fablefit.Interface.ScreenInterFace
import com.rohan.fablefit.Screen.CartScreen
import com.rohan.fablefit.Screen.HomeScreen
import com.rohan.fablefit.Screen.ProfileScreen
import com.rohan.fablefit.auth.AuthScreen
import com.rohan.fablefit.auth.AuthViewModel
import com.rohan.fablefit.ui.theme.FablefitTheme
import org.jetbrains.annotations.ApiStatus

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FablefitTheme {
                // Check if the user is already logged in when the app starts
                var isAuthenticated by remember {
                    mutableStateOf(FirebaseAuth.getInstance().currentUser != null)
                }

                if (!isAuthenticated) {
                    // Show Login Screen
                    AuthScreen(
                        context = LocalContext.current,
                        onLoginSuccess = { isAuthenticated = true } // Switch to main app on success
                    )
                } else {
                    // Show Main E-commerce App
                    MainECommerceScaffold()
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainECommerceScaffold() {
    val screens = listOf<ScreenInterFace>(
        HomeScreen,
        CartScreen,
        ProfileScreen,
    )
    var authViewModel: AuthViewModel= viewModel();
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                selectedScreen.UI()

                Button(onClick = {
                    authViewModel.logOut()
                }) {
                    Text("logOut")
                }
                CircularWavyProgressIndicator()
                LinearWavyProgressIndicator()
                // Swapped the standard indicator for the built-in wavy one!

            }
        }
    }
}

@Composable
fun NAVBAR(
    screens: List<ScreenInterFace>,
    selectedScreen: ScreenInterFace,
    onTabSelected: (ScreenInterFace) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp)),
        tonalElevation = 10.dp,
    ) {
        screens.forEach { screen ->
            NavigationBarItem(
                selected = selectedScreen == screen,
                onClick = { onTabSelected(screen) },
                icon = { Icon(screen.icon, screen.title) },
                label = { Text(screen.title) },
                alwaysShowLabel = false,
            )
        }
    }
}