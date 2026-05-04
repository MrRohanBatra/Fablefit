package com.rohan.fablefit.ui.Chatbot

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.rohan.fablefit.ui.Product.ProductViewModel
import kotlinx.coroutines.launch
import com.rohan.fablefit.ui.model.ChatMessage
import com.rohan.fablefit.ui.model.ChatbotViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AgentChatScreen(
    context: Context,
    navController: NavController,
    viewModel: ChatbotViewModel = viewModel() // Injecting the ViewModel
) {
    // Collecting state from ViewModel
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val inputText by viewModel.inputText.collectAsStateWithLifecycle()
    var inputImageUri by remember { mutableStateOf<Uri?>(null) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { inputImageUri=it }
        }
    )
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp) ,// Adjusted for toolbar height
//            verticalArrangement = Arrangement.SpaceBetween
            ) {
            items(messages) { message ->
                Spacer(Modifier.height(10.dp))
                ChatBubbleUnified(message, navController = navController)
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            HorizontalFloatingToolbar(
                expanded = true,
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { viewModel.sendMessage(context) },
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                    }
                }
            ) {
                IconButton(onClick = {
                    if(inputImageUri==null){
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                    else{
                        inputImageUri=null;
                    }
                }) {
                    if(inputImageUri==null){
                        Icon(Icons.Default.Add, contentDescription = "Add attachment")
                    }
                    else{
                        AsyncImage(
                            model = inputImageUri,
                            contentDescription = ""
                        )
                    }
                }

                TextField(
                    value = inputText,
                    onValueChange = { viewModel.onTextChanged(it) },
                    placeholder = { Text("Type a message...") },
                    modifier = Modifier.widthIn(max = 200.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}
//@Composable
//fun AgentChatScreen(
//    navController: NavController
//) {
//    var messages by remember {
//        mutableStateOf(listOf(
//            ChatMessage(text = "Hi! I'm Rasberry...", isFromUser = false)
//        ))
//    }
//    var inputText by remember { mutableStateOf("") }
//    val listState = rememberLazyListState()
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        // 1. Message List (Occupies full screen)
//        LazyColumn(
//            state = listState,
//            modifier = Modifier.fillMaxSize(),
//            contentPadding = PaddingValues(bottom = 100.dp) // Space for the toolbar
//        ) {
//            items(messages) { message ->
//                ChatBubbleUnified(message, navController = navController)
//            }
//        }
//
//        // 2. Bottom-Aligned Toolbar Container
//        Row(
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .padding(bottom = 24.dp, start = 16.dp, end = 16.dp)
//                .fillMaxWidth(),
//            horizontalArrangement = Arrangement.Center
//        ) {
//            HorizontalFloatingToolbar(
//                expanded = true,
//                // The Main Action (Send)
//                floatingActionButton = {
//                    FloatingActionButton(
//                        onClick = { /* Handle Send */ },
//                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
//                    ) {
//                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
//                    }
//                }
//            ) {
//                // Items inside the toolbar (Left side of the FAB)
//                IconButton(onClick = { /* Handle Attach */ }) {
//                    Icon(Icons.Default.Add, contentDescription = "Add attachment")
//                }
//
//                TextField(
//                    value = inputText,
//                    onValueChange = { inputText = it },
//                    placeholder = { Text("Type a message...") },
//                    modifier = Modifier.widthIn(max = 200.dp),
//                    colors = TextFieldDefaults.colors(
//                        focusedContainerColor = Color.Transparent,
//                        unfocusedContainerColor = Color.Transparent,
//                        focusedIndicatorColor = Color.Transparent,
//                        unfocusedIndicatorColor = Color.Transparent
//                    )
//                )
//            }
//        }
//    }
//}
@Composable
fun ChatBubbleUnified(message: ChatMessage, navController: NavController,productViewModel: ProductViewModel=viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 2.dp, end = 2.dp),
        horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth(0.85f) // Don't let bubbles stretch all the way across
        ) {
            if (!message.isFromUser) {
                Icon(
                    imageVector = Icons.Outlined.SupportAgent,
                    contentDescription = "Agent",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(28.dp)
                        .padding(end = 8.dp)
                )
            }

            if (message.text.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (message.isFromUser) 20.dp else 4.dp,
                        bottomEnd = if (message.isFromUser) 4.dp else 20.dp
                    ),
                    color = if (message.isFromUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = message.text,
                        modifier = Modifier.padding(12.dp),
                        color = if (message.isFromUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Product Carousel (Renders ONLY if IDs were extracted)
        if (message.productIdsToRender.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(start = 36.dp)
            ) {
                items(message.productIdsToRender) { productId ->

                    val imageUrl by produceState<String?>(initialValue = null, productId) {
                        value = productViewModel.getProductImageUrl(productId)
                    }

                    ElevatedCard(
                        onClick = { navController.navigate("productdisplay/$productId") },
                        modifier = Modifier.width(140.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                           if(imageUrl==null){

                               Box(modifier = Modifier
                                   .fillMaxWidth()
                                   .height(120.dp)
                                   .background(Color.LightGray))
                           }
                            else{
                               Box(modifier = Modifier.fillMaxWidth().aspectRatio(2f/3f).height(120.dp).clip(RoundedCornerShape(8.dp))) {
                                   AsyncImage(
                                       model = imageUrl,
                                       contentDescription = "Product Image",
                                       modifier = Modifier
                                           .fillMaxWidth(),
                                       contentScale = ContentScale.Crop
                                   )
                               }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("View Product", style = MaterialTheme.typography.labelMedium)
                        }
                    }

                }
            }
        }
    }
}