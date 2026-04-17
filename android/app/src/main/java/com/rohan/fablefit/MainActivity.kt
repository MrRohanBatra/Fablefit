package com.rohan.fablefit

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.compose.SubcomposeAsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.msseo.android.arrowtooltip.ArrowTooltip
import com.rohan.fablefit.Screen.CartScreen
import com.rohan.fablefit.Screen.HomeScreen
import com.rohan.fablefit.Screen.ProductDisplayScreen
import com.rohan.fablefit.Screen.ProfileScreen
import com.rohan.fablefit.Screen.SearchScreen
import com.rohan.fablefit.Screen.UserInfo
import com.rohan.fablefit.auth.AuthScreen
import com.rohan.fablefit.auth.SplashScreen
import com.rohan.fablefit.navigation.AppRoute
import com.rohan.fablefit.navigation.BottomRoute
import com.rohan.fablefit.services.FableFitMessagingService
import com.rohan.fablefit.ui.Cart.CartModelUiState
import com.rohan.fablefit.ui.Cart.CartViewModel
import com.rohan.fablefit.ui.Wishlist.WishlistViewModel
import com.rohan.fablefit.ui.model.SearchFilters
import com.rohan.fablefit.ui.theme.FablefitTheme
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {

    // Runtime permission launcher for POST_NOTIFICATIONS (Android 13+)
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Log.d("FCM", "Notification permission granted")
        } else {
            Log.w("FCM", "Notification permission denied — push notifications will not show")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            FablefitTheme {
                val navController = rememberNavController()
                Surface(color = MaterialTheme.colorScheme.background) {
                    NavHost(
                        navController  = navController,
                        startDestination = AppRoute.Splash
                    ) {
                        composable(AppRoute.Splash) {
                            SplashScreen()
                            LaunchedEffect(Unit) {
                                val startTime   = System.currentTimeMillis()
                                val currentUser = FirebaseAuth.getInstance().currentUser
                                val elapsed     = System.currentTimeMillis() - startTime
                                if (elapsed < 800) delay(800 - elapsed)
                                if (currentUser != null) {
                                    // Upload FCM token now that we know the user
                                    uploadFcmTokenForUser(currentUser.uid)
                                    navController.navigate(AppRoute.Main) {
                                        popUpTo(AppRoute.Splash) { inclusive = true }
                                    }
                                } else {
                                    navController.navigate(AppRoute.Auth) {
                                        popUpTo(AppRoute.Splash) { inclusive = true }
                                    }
                                }
                            }
                        }

                        composable(AppRoute.Auth) {
                            AuthScreen(
                                context      = LocalContext.current,
                                onLoginSuccess = {
                                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                                    Log.d("login", "Login complete: $uid")
                                    // Upload FCM token right after login — this is the key fix.
                                    // onNewToken may have fired before login, so uid was null then.
                                    uid?.let { uploadFcmTokenForUser(it) }
                                    navController.navigate(AppRoute.Main) {
                                        popUpTo(AppRoute.Auth) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(AppRoute.Main) {
                            MainECommerceScaffold(
                                onLogout = {
                                    FirebaseAuth.getInstance().signOut()
                                    navController.navigate(AppRoute.Auth) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Fetches the current FCM registration token and uploads it to the backend.
     * Called both after login and when an existing session is resumed on app start.
     */
    private fun uploadFcmTokenForUser(uid: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM", "Uploading token for uid=$uid")
                FableFitMessagingService().uploadToken(uid, token)
            } else {
                Log.e("FCM", "Failed to get FCM token: ${task.exception?.message}")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainECommerceScaffold(onLogout: () -> Unit) {
    val cartViewModel: CartViewModel       = viewModel()
    val wishlistViewModel: WishlistViewModel = viewModel()   // ← Hoisted here so wishlist state
                                                              //   survives navigation between screens
    val user     = FirebaseAuth.getInstance().currentUser
    val uiState  = cartViewModel.uiState

    LaunchedEffect(user?.uid) {
        user?.uid?.let {
            cartViewModel.getUserCart(it)
            wishlistViewModel.loadWishlist(it)   // ← Load once at scaffold level
        }
    }

    val navController   = rememberNavController()
    val currentRoute    = navController.currentBackStackEntryAsState().value?.destination?.route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val activeFilters   = navBackStackEntry?.savedStateHandle?.get<SearchFilters>("search_filters")

    var searchQuery by remember(activeFilters) {
        mutableStateOf(activeFilters?.query ?: "")
    }

    val haptic      = LocalHapticFeedback.current
    val hapticValue = HapticFeedbackType.ContextClick

    val screens = listOf(
        BottomRoute.Home,
        BottomRoute.Search,
        BottomRoute.Cart,
        BottomRoute.Profile
    )

    Scaffold(
        topBar = {
            if (currentRoute == BottomRoute.Home.route) {
                TopAppBar(
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                    title    = { Text(stringResource(R.string.app_name)) },
                    actions  = {
                        IconButton(onClick = { navController.navigate(BottomRoute.Search.route) }) {
                            Icon(Icons.Default.Search, contentDescription = "Search Icon")
                        }
                        BadgedBox(
                            modifier = Modifier.padding(8.dp),
                            badge    = {
                                if (uiState is CartModelUiState.Success) {
                                    val count = uiState.cart.items.size
                                    if (count > 0) Badge { Text(if (count > 9) "9+" else count.toString()) }
                                }
                            }
                        ) {
                            IconButton(onClick = { navController.navigate(BottomRoute.Cart.route) }) {
                                Icon(Icons.Default.ShoppingCart, "Shopping Cart")
                            }
                        }
                    },
                    navigationIcon = {
                        Image(
                            painterResource(R.drawable.icon),
                            contentDescription = null,
                            modifier = Modifier.padding(horizontal = 12.dp).size(32.dp).clip(RoundedCornerShape(8.dp))
                        )
                    }
                )
            }

            if (currentRoute == BottomRoute.Search.route) {
                SearchBar(
                    modifier   = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                    inputField = {
                        SearchBarDefaults.InputField(
                            query          = searchQuery,
                            onQueryChange  = { newQuery ->
                                searchQuery = newQuery
                                navController.currentBackStackEntry?.savedStateHandle?.set("search_query", newQuery)
                            },
                            onSearch       = {},
                            expanded       = false,
                            onExpandedChange = {},
                            placeholder    = { Text("Search clothes, brands...") },
                            leadingIcon    = {
                                IconButton(onClick = {
                                    searchQuery = ""
                                    navController.navigate(BottomRoute.Home.route) {
                                        popUpTo(BottomRoute.Home.route) { inclusive = true }
                                    }
                                }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Close, "Clear")
                                    }
                                }
                            }
                        )
                    },
                    expanded       = false,
                    onExpandedChange = {}
                ) {}
            }

            if (currentRoute == BottomRoute.Profile.route || currentRoute == BottomRoute.MyInfo.route) {
                Surface(
                    shape         = RoundedCornerShape(24.dp),
                    tonalElevation = 4.dp,
                    modifier      = Modifier.fillMaxWidth().padding(18.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier            = Modifier.fillMaxWidth().padding(24.dp)
                        ) {
                            SubcomposeAsyncImage(
                                model              = user?.photoUrl,
                                contentDescription = null,
                                modifier           = Modifier.size(100.dp).clip(CircleShape),
                                contentScale       = ContentScale.Crop,
                                loading            = { CircularWavyProgressIndicator() }
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                user?.displayName ?: "FableFit User",
                                style      = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                user?.email ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (currentRoute == BottomRoute.Cart.route) {
                TopAppBar(
                    title          = { Text("My Cart") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                    }
                )
            }
        },

        bottomBar = {
            Surface(
                modifier       = Modifier.fillMaxWidth(),
                shape          = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color          = Color.Transparent,
                tonalElevation = 8.dp
            ) {
                NavigationBar(tonalElevation = 8.dp) {
                    screens.forEach { screen ->
                        NavigationBarItem(
                            selected = currentRoute == screen.route,
                            onClick  = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            },
                            icon  = {
                                if (screen.route == BottomRoute.Profile.route) {
                                    SubcomposeAsyncImage(
                                        model              = user?.photoUrl,
                                        contentDescription = "profile image",
                                        modifier           = Modifier.size(28.dp).clip(CircleShape),
                                        contentScale       = ContentScale.Crop,
                                        loading = { Icon(screen.icon, screen.title) },
                                        error   = { Icon(screen.icon, screen.title) }
                                    )
                                } else {
                                    Icon(screen.icon, screen.title)
                                }
                            },
                            label          = { Text(screen.title) },
                            alwaysShowLabel = false
                        )
                    }
                }
            }
        },

        floatingActionButton = { AiChatBot(true) },

        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))

    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = BottomRoute.Home.route,
            modifier         = Modifier.padding(innerPadding).clip(RoundedCornerShape(20.dp)),
            enterTransition  = { slideInHorizontally({ it }, tween(300)) + fadeIn(tween(300)) },
            exitTransition   = { slideOutHorizontally({ -it }, tween(300)) + fadeOut(tween(300)) },
            popEnterTransition = { slideInHorizontally({ -it }, tween(300)) + fadeIn(tween(300)) },
            popExitTransition  = { slideOutHorizontally({ it }, tween(300)) + fadeOut(tween(300)) }
        ) {
            composable(BottomRoute.Home.route) {
                LaunchedEffect(Unit) { haptic.performHapticFeedback(hapticValue) }
                HomeScreen(navController)
            }

            composable(BottomRoute.Search.route) { backStackEntry ->
                LaunchedEffect(Unit) { haptic.performHapticFeedback(hapticValue) }
                val liveQuery by backStackEntry.savedStateHandle
                    .getStateFlow("search_query", searchQuery)
                    .collectAsState()
                val filters = backStackEntry.savedStateHandle.get<SearchFilters>("search_filters")
                SearchScreen(
                    query          = liveQuery,
                    filters        = filters,
                    onProductClick = { product ->
                        navController.navigate("productdisplay/${product.id}")
                    }
                )
            }

            composable(BottomRoute.Cart.route) {
                LaunchedEffect(Unit) { haptic.performHapticFeedback(hapticValue) }
                CartScreen(cartViewModel)
            }

            composable(BottomRoute.Profile.route) {
                LaunchedEffect(Unit) { haptic.performHapticFeedback(hapticValue) }
                ProfileScreen(navController, onLogout = { onLogout() })
            }

            composable("${BottomRoute.ProductDisplay.route}/{productId}") { navBackStackEntry ->
                val productId = navBackStackEntry.arguments?.getString("productId")
                if (productId == null) {
                    haptic.performHapticFeedback(HapticFeedbackType.Reject)
                    Toast.makeText(LocalContext.current, "Product Id not Found", Toast.LENGTH_SHORT).show()
                } else {
                    ProductDisplayScreen(
                        productId        = productId,
                        cartViewModel    = cartViewModel,
                        wishlistViewModel = wishlistViewModel   // ← Pass hoisted VM
                    )
                }
            }

            composable(BottomRoute.MyInfo.route) {
                UserInfo()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AiChatBot(showIcon: Boolean) {
    if (showIcon) {
        var showTip by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { delay(300); showTip = true }
        ArrowTooltip(
            visible        = showTip,
            tooltipContent = { Text("Simple Tool Tip") }
        ) {
            FloatingActionButton(
                onClick        = { /* TODO: Navigate to AI Chat Screen */ },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor   = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Outlined.SupportAgent, "AI Chat Assistant")
            }
        }
    }
}
