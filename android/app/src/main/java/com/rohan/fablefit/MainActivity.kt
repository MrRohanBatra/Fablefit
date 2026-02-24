package com.rohan.fablefit

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.rohan.fablefit.auth.AuthScreen
import com.rohan.fablefit.navigation.BottomRoute
import com.rohan.fablefit.ui.theme.FablefitTheme
import com.rohan.fablefit.Screen.HomeScreen
import com.rohan.fablefit.Screen.CartScreen
import com.rohan.fablefit.Screen.ProfileScreen
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import com.rohan.fablefit.Screen.ProductDisplayScreen
import com.rohan.fablefit.Screen.SearchScreen
import com.rohan.fablefit.auth.SplashScreen
import com.rohan.fablefit.ui.model.Product
import com.rohan.fablefit.ui.model.SearchFilters


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
//            FablefitTheme {
//                // 1. Start with a 'null' or 'Loading' state
//                var authChecked by remember { mutableStateOf(false) }
//                var user by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }
//
//                // 2. Use a LaunchedEffect to verify the user once on startup
//                LaunchedEffect(Unit) {
//                    // This ensures the SDK has a moment to initialize
//                    user = FirebaseAuth.getInstance().currentUser
//                    authChecked = true
//                }
//
//                // 3. Handle the UI based on the check status
//                if (!authChecked) {
//
//                    SplashScreen()
//                } else {
//                    if (user == null) {
//                        AuthScreen(
//                            context = LocalContext.current,
//                            onLoginSuccess = {
//                                user = FirebaseAuth.getInstance().currentUser
//                            }
//                        )
//                    } else {
//                        MainECommerceScaffold()
//                    }
//                }
//            }
            FablefitTheme {
                // 1. Define states for the check
                var isCheckingAuth by remember { mutableStateOf(true) }
                var isAuthenticated by remember { mutableStateOf(false) }

                // 2. Perform the background check on startup
                LaunchedEffect(Unit) {
                    val startTime= System.currentTimeMillis();
                    // Check Firebase for an existing session
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    isAuthenticated = currentUser != null
                    val elapsed = System.currentTimeMillis() - startTime
                    if (elapsed < 800) kotlinx.coroutines.delay(800 - elapsed)
                    kotlinx.coroutines.delay(300)
                    isCheckingAuth = false
                }
                LaunchedEffect(Unit) {
                    FirebaseAuth.getInstance().addAuthStateListener { auth ->
                        isAuthenticated = auth.currentUser != null
                    }
                }

                // 3. Navigation Switchboard
                Surface(color = MaterialTheme.colorScheme.background) {

                    val targetState by remember {
                        mutableStateOf(
                            when {
                                isCheckingAuth -> "SPLASH"
                                !isAuthenticated -> "AUTH"
                                else -> "MAIN"
                            }
                        )
                    }
                    AnimatedContent(
                        targetState = targetState,
                        transitionSpec = {
                            fadeIn(tween(500)) togetherWith fadeOut(tween(800))
                        },
                        label = "AppStartTransition"
                    ) { state ->

                        when (state) {
                            "SPLASH" -> SplashScreen()

                            "AUTH" -> AuthScreen(
                                context = LocalContext.current,
                                onLoginSuccess = { isAuthenticated = true }
                            )

                            "MAIN" -> MainECommerceScaffold(
                                onLogout = {isAuthenticated=false}
                            )
                        }
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainECommerceScaffold(onLogout:()-> Unit) {

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
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val activeFilters = navBackStackEntry?.savedStateHandle?.get<SearchFilters>("search_filters")

    // 2. Initialize the searchQuery with filters.query, or empty if null
    var searchQuery by remember(activeFilters) {
        mutableStateOf(activeFilters?.query ?: "")
    }
    Scaffold(

        topBar = {
            if(currentRoute==BottomRoute.Home.route){
                TopAppBar(
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                    title = { Text(stringResource(R.string.app_name)) },
                    actions= {Icon(Icons.Default.Search, contentDescription = "Search Icon")
                        BadgedBox(
                            modifier = Modifier.padding(8.dp),
                            badge = {
                                val cartCount by remember { mutableStateOf(10) }
                                Badge {
                                    Text(
                                        if(cartCount>9){
                                            "9+"
                                        }
                                        else{
                                            cartCount.toString()
                                        }
                                    )
                                }
                            }
                        ) {
                            IconButton(onClick = { }) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart, // Outlined looks cleaner
                                    contentDescription = "Shopping Cart"
                                )
                            }
                        }
                             },
                    navigationIcon = {Image(
                        painterResource(R.drawable.icon),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 12.dp, end = 12.dp)
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )}
                )
            }
            if(currentRoute== BottomRoute.Search.route){
                var query by remember { mutableStateOf("") }
                SearchBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            onSearch = { /* Handle search */ },
                            expanded = false,
                            onExpandedChange = {},
                            placeholder = { Text("Search clothes, brands...") },
                            leadingIcon ={
                                IconButton(onClick = {
                                    searchQuery=""
                                    navController.navigate(BottomRoute.Home.route) {
                                        popUpTo(BottomRoute.Home.route) { inclusive = true }
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear"
                                        )
                                    }
                                }
                            }
                        )
                    },
                    expanded = false,
                    onExpandedChange = {}
                ) {}
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = Color.Transparent, // Makes the 'box' corners invisible
                tonalElevation = 8.dp
            ) {
                NavigationBar(
                    tonalElevation = 8.dp
                ) {


                    screens.forEach { screen ->

                        NavigationBarItem(
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            },
                            icon = {
                                if(screen.route== BottomRoute.Profile.route){
//                                    Icon(screen.icon, screen.title)
                                    val user= FirebaseAuth.getInstance().currentUser
                                    Log.d("Fablefit", user?.photoUrl.toString())
                                    SubcomposeAsyncImage(
                                        model =user?.photoUrl,
                                        contentDescription = "profile image",
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop,
                                        loading = {
                                            Icon(screen.icon,contentDescription=screen.title)
                                        },
                                        error = {
                                            Icon(screen.icon,contentDescription=screen.title)
                                        }
                                    )
                                }
                                else{
                                    Icon(screen.icon, screen.title)
                                }
                            },
                            label = { Text(screen.title) },
                            alwaysShowLabel = false,
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = BottomRoute.Home.route,
            modifier = Modifier
                .padding(innerPadding)
                .clip(RoundedCornerShape(20.dp)),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                ) + fadeOut(tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(300)
                ) + fadeIn(tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeOut(tween(300))
            }
        ) {

            composable(BottomRoute.Home.route) {
                HomeScreen(navController)
            }
            composable(BottomRoute.Search.route) { backStackEntry ->

                val filters = navController
                    .previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<SearchFilters>("search_filters")

                SearchScreen(
                    query=searchQuery,
                    filters = filters
                )
            }
            composable(BottomRoute.Cart.route) {
                CartScreen()
            }
            composable(BottomRoute.Profile.route) {
                ProfileScreen(navController, onLogout = {
                    onLogout
                })
            }
            composable("${BottomRoute.ProductDisplay.route}/{productId}") { navBackStackEntry ->
                // Extract the ID from the arguments
                val productId = navBackStackEntry.arguments?.getString("productId")
                if(productId==null){
                    Toast.makeText(LocalContext.current,"Product Id not Found", Toast.LENGTH_SHORT).show()
                }
                else{
                    ProductDisplayScreen(productId)
                }
            }
        }
    }
}