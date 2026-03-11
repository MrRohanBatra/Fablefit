package com.rohan.fablefit

import android.os.Bundle
import android.util.Log
import android.widget.Toast

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.compose.SubcomposeAsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.msseo.android.arrowtooltip.ArrowTooltip
import com.rohan.fablefit.Screen.CartScreen
import com.rohan.fablefit.Screen.HomeScreen
import com.rohan.fablefit.Screen.ProductDisplayScreen
import com.rohan.fablefit.Screen.ProfileScreen
import com.rohan.fablefit.Screen.SearchScreen
import com.rohan.fablefit.Screen.UserInfo
import com.rohan.fablefit.agent.AgentRoutes
import com.rohan.fablefit.auth.AuthScreen
import com.rohan.fablefit.auth.SplashScreen
import com.rohan.fablefit.navigation.AppRoute
import com.rohan.fablefit.navigation.BottomRoute
import com.rohan.fablefit.ui.Cart.CartModelUiState
import com.rohan.fablefit.ui.Cart.CartViewModel
import com.rohan.fablefit.ui.model.SearchFilters
import com.rohan.fablefit.ui.theme.FablefitTheme
import kotlinx.coroutines.delay
import androidx.compose.foundation.lazy.items
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FablefitTheme() {
                val navController=rememberNavController();
                Surface(
                    color=MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = AppRoute.Splash
                    ){
                        composable(AppRoute.Splash) {
                            SplashScreen()
                            LaunchedEffect(Unit) {
                                val startTime=System.currentTimeMillis();
                                val currentUser= FirebaseAuth.getInstance().currentUser
                                val elapsed = System.currentTimeMillis() - startTime
                                if (elapsed < 800) delay(800 - elapsed)
                                if (currentUser!=null){
                                    navController.navigate(
                                        AppRoute.Main
                                    ){
                                        popUpTo(AppRoute.Splash){
                                            inclusive=true
                                        }
                                    }
                                }
                                else{
                                    navController.navigate(AppRoute.Auth){
                                        popUpTo (AppRoute.Splash){
                                            inclusive=true
                                        }
                                    }
                                }
                            }
                        }
                        composable(AppRoute.Auth) {
                            AuthScreen(
                                context=LocalContext.current,
                                onLoginSuccess = {

                                    Log.d("login","login Complete Signed in as ${FirebaseAuth.getInstance().currentUser?.email}")
                                    navController.navigate(AppRoute.Main){
                                        popUpTo(AppRoute.Auth) {
                                            inclusive=true;
                                        }
                                    }
                                }

                            )
                        }
                        composable(AppRoute.Main) {
                            MainECommerceScaffold(
                                onLogout = {
                                    FirebaseAuth.getInstance().signOut();
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
}
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainECommerceScaffold(onLogout:()-> Unit) {
    var refresh by remember { mutableStateOf(0) }
    val cartViewModel: CartViewModel=viewModel()
    val user by remember(refresh) {   mutableStateOf(FirebaseAuth.getInstance().currentUser)}
    val uiState=cartViewModel.uiState
    LaunchedEffect(user?.uid) {
        user?.uid?.let {
            cartViewModel.getUserCart(it)
        }
    }
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

    var searchQuery by remember(activeFilters) {
        mutableStateOf(activeFilters?.query ?: "")
    }
    val haptic=LocalHapticFeedback.current
    val hapticValue=HapticFeedbackType.ContextClick
    Scaffold(
        topBar = {
            if(currentRoute==BottomRoute.Home.route){
                TopAppBar(
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                    title = { Text(stringResource(R.string.app_name)) },
                    actions= {
                        IconButton(onClick ={
                            navController.navigate(BottomRoute.Search.route)
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "Search Icon")
                        }
                        BadgedBox(
                            modifier = Modifier.padding(8.dp),
                            badge = {
                                if (uiState is CartModelUiState.Success) {

                                    val itemCount = uiState.cart.items.size

                                    if (itemCount > 0) {
                                        Badge {
                                            Text(
                                                if (itemCount > 9) "9+" else itemCount.toString()
                                            )
                                        }
                                    }
                                }
                            }
                        )
                                 {
                            IconButton(onClick = { navController.navigate(BottomRoute.Cart.route){

                            } }) {
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
                SearchBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = searchQuery,
                            onQueryChange = { newQuery ->
                                searchQuery = newQuery
                                // Update the navigation handle so the screen can see it
                                navController.currentBackStackEntry?.savedStateHandle?.set("search_query", newQuery)
                            },
                            onSearch = {  },
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
            if(currentRoute== BottomRoute.Profile.route || currentRoute== BottomRoute.MyInfo.route){
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    tonalElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    val user= FirebaseAuth.getInstance().currentUser
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {

                            SubcomposeAsyncImage(
                                model = user?.photoUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                                loading = {
                                    CircularWavyProgressIndicator()
                                }
                            )

                            Spacer(Modifier.height(12.dp))

                            Text(
                                user?.displayName ?: "FableFit User",
                                style = MaterialTheme.typography.titleLarge,
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
            if(currentRoute== BottomRoute.Cart.route){
                TopAppBar(
                    title = { Text("My Cart") },
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
        },
        floatingActionButton = {
            val show=true
            if(currentRoute==BottomRoute.Home.route){
                AiChatBot(show)
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
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
                LaunchedEffect(Unit) { haptic.performHapticFeedback(hapticValue)}
                HomeScreen(navController)
            }
            composable(BottomRoute.Search.route) { backStackEntry ->
                LaunchedEffect(Unit) { haptic.performHapticFeedback(hapticValue)}
                val liveQuery by backStackEntry.savedStateHandle
                    .getStateFlow("search_query", searchQuery)
                    .collectAsState()
                val filters = backStackEntry.savedStateHandle.get<SearchFilters>("search_filters")
                SearchScreen(
                    query = liveQuery, // Use the live value from the handle
                    filters = filters,
                    onProductClick = {
                        product->
                        navController.navigate(
                            "productdisplay/${product.id}"
                        )
                    }
                )
            }
            composable(BottomRoute.Cart.route) {
                LaunchedEffect(Unit) { haptic.performHapticFeedback(hapticValue)}

                CartScreen(cartViewModel)
            }
            composable(BottomRoute.Profile.route) {
//                haptic.performHapticFeedback(hapticValue)
                LaunchedEffect(Unit) { haptic.performHapticFeedback(hapticValue)}
                ProfileScreen(navController, onLogout = {
                    onLogout()
                })
            }
            composable("${BottomRoute.ProductDisplay.route}/{productId}") { navBackStackEntry ->
                // Extract the ID from the arguments
                val productId = navBackStackEntry.arguments?.getString("productId")
                if(productId==null){
                    haptic.performHapticFeedback(hapticFeedbackType = HapticFeedbackType.Reject)
                    Toast.makeText(LocalContext.current,"Product Id not Found", Toast.LENGTH_SHORT).show()
                }
                else{
//                    LaunchedEffect(Unit) { haptic.performHapticFeedback(hapticValue)}
                    ProductDisplayScreen(productId,cartViewModel=cartViewModel)
                }
            }
            composable(BottomRoute.MyInfo.route) {
//                LaunchedEffect(Unit) { haptic.performHapticFeedback(hapticValue)}
                UserInfo(context = LocalContext.current,onRefresh={
                    refresh++;
                })
            }
        }
    }
}
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AiChatBot(showIcon: Boolean) {

    var expanded by remember { mutableStateOf(false) }
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }

    // Loop animation
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            expanded = true
            delay(6000)
            expanded = false
            delay(4000)
        }
    }

    if (showIcon) {
        ExtendedFloatingActionButton(
            onClick = {
                showBottomSheet = true
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.SupportAgent,
                    contentDescription = null
                )
            },
            text = {
                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    Text("Rasberry")
                }
            }
        )
    }
    if (showBottomSheet) {

        val routes = listOf(
            AgentRoutes.ImageSearch,
            AgentRoutes.ChatBot
        )

        var selectedOption by remember { mutableStateOf<AgentRoutes?>(null) }

        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            }
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                item {
                    AgentBubble("What would you like to do?")
                }

                if (selectedOption == null) {

                    items(routes) { route ->

                        UserOptionBubble(
                            text = route.title,
                            onClick = {
                                selectedOption = route
                            }
                        )
                    }

                } else {

                    item {
                        UserBubble(selectedOption!!.title)
                    }

                    item {
                        selectedOption!!.ui()
                    }

                }
            }
        }
    }
}
@Composable
fun UserBubble(
    text: String? = null,
    custom: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Top
    ) {

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                text?.let {
                    Text(it)
                }

                custom?.invoke()
            }
        }

        Spacer(Modifier.width(8.dp))

        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
    }
}
@Composable
fun UserOptionBubble(
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.End
    ) {

        AssistChip(
            onClick = onClick,
            label = { Text(text) }
        )
    }
}
@Composable
fun AgentBubble(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {

        Icon(
            imageVector = Icons.Outlined.SupportAgent,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}