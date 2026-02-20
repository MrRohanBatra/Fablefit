package com.rohan.fablefit

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.rohan.fablefit.auth.AuthScreen
import com.rohan.fablefit.auth.AuthViewModel
import com.rohan.fablefit.navigation.BottomRoute
import com.rohan.fablefit.ui.theme.FablefitTheme
import com.rohan.fablefit.Screen.HomeScreen
import com.rohan.fablefit.Screen.CartScreen
import com.rohan.fablefit.Screen.ProfileScreen
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.rohan.fablefit.Screen.SearchScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FablefitTheme {
                var isAuthenticated by remember {
                    mutableStateOf(FirebaseAuth.getInstance().currentUser != null)
                }

                if (!isAuthenticated) {
                    AuthScreen(
                        context = LocalContext.current,
                        onLoginSuccess = { isAuthenticated = true }
                    )
                } else {
                    MainECommerceScaffold()
                }
//                AuthScreen(
//                    context = LocalContext.current,
//                    onLoginSuccess = {isAuthenticated=false}
//                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainECommerceScaffold() {

    val navController = rememberNavController()
    val currentRoute =
        navController.currentBackStackEntryAsState()
            .value?.destination?.route
    val screens = listOf(
        BottomRoute.Home,
        BottomRoute.Search,
        BottomRoute.Cart,
        BottomRoute.Profile
    )

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { Image(
                    painterResource(R.drawable.icon),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) },
                title = {
                    Text(
                        when (currentRoute) {
                            BottomRoute.Home.route -> stringResource(R.string.app_name)
                            BottomRoute.Search.route -> "Search"
                            BottomRoute.Cart.route -> "My Cart"
                            BottomRoute.Profile.route -> "Profile"
                            else -> ""
                        },
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {

                    when (currentRoute) {

                        BottomRoute.Home.route -> {
                            IconButton(
                                onClick = {
                                    navController.navigate(BottomRoute.Search.route)
                                }
                            ) {
                                Icon(
                                    Icons.Default.Search,

                                    contentDescription = "Search"
                                )
                            }
                        }

                        BottomRoute.Cart.route -> {
                        }

                        BottomRoute.Profile.route -> {
                            // Settings icon maybe later
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topStart = 28.dp,
                            topEnd = 28.dp
                        )
                    )
                    .border(
                        width = 0.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(
                            topStart = 28.dp,
                            topEnd = 28.dp
                        )
                    ),
                tonalElevation = 8.dp
            ){



                screens.forEach { screen ->

                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(screen.icon, screen.title) },
                        label = { Text(screen.title) },
                        alwaysShowLabel = false,
                    )
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = BottomRoute.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(tween(200)) },
            exitTransition = { fadeOut(tween(200)) },
        ) {

            composable(BottomRoute.Home.route) {
                HomeScreen()
            }
            composable(BottomRoute.Search.route) {
                SearchScreen()
            }
            composable(BottomRoute.Cart.route) {
                CartScreen()
            }

            composable(BottomRoute.Profile.route) {
                ProfileScreen()
            }
        }
    }
}