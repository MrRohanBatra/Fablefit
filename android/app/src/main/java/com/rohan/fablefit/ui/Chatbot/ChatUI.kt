package com.rohan.fablefit.ui.Chatbot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.rohan.fablefit.ui.model.ChatMessage
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AgentChatScreen(
    navController: NavController
) {
    var messages by remember {
        mutableStateOf(listOf(
            ChatMessage(
                text = "Hi! I'm Rasberry, your personal stylist. What are you looking for today?",
                isFromUser = false
            )
        ))
    }
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1. Chat History
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { msg ->
                ChatBubbleUnified(message = msg, navController = navController)
            }
        }

        // 2. Input Area
//        Surface(
//            color = MaterialTheme.colorScheme.surface,
//            tonalElevation = 8.dp,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Row(
//                modifier = Modifier
//                    .padding(horizontal = 16.dp, vertical = 12.dp)
//                    .navigationBarsPadding(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                // Optional: Image Upload Button for visual search
//                IconButton(onClick = { /* Launch Image Picker */ }) {
//                    Icon(Icons.Outlined.Image, contentDescription = "Upload Image")
//                }
//
//                OutlinedTextField(
//                    value = inputText,
//                    onValueChange = { inputText = it },
//                    placeholder = { Text("Ask about styles, colors...") },
//                    modifier = Modifier
//                        .weight(1f)
//                        .padding(horizontal = 8.dp),
//                    shape = RoundedCornerShape(24.dp),
//                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
//                    maxLines = 3
//                )
//
//                IconButton(
//                    onClick = {
//                        if (inputText.isNotBlank()) {
//                            val userMsg = ChatMessage(text = inputText, isFromUser = true)
//                            messages = messages + userMsg
//                            val currentInput = inputText
//                            inputText = ""
//
//                            // Scroll to bottom
//                            coroutineScope.launch { listState.animateScrollToItem(messages.size - 1) }
//
//                            // TODO: Call your Retrofit API here: process_chat(user_id, currentInput)
//                            // On Success:
//                            // val agentMsg = parseAgentMessage(apiResponse.message)
//                            // messages = messages + agentMsg
//                        }
//                    },
//                    colors = IconButtonDefaults.iconButtonColors(
//                        containerColor = MaterialTheme.colorScheme.primary,
//                        contentColor = MaterialTheme.colorScheme.onPrimary
//                    ),
//                    modifier = Modifier.clip(CircleShape)
//                ) {
//                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
//                }
//            }
//        }
        HorizontalFloatingToolbar(
            expanded = true,

        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Optional: Image Upload Button for visual search
                IconButton(onClick = { /* Launch Image Picker */ }) {
                    Icon(Icons.Outlined.Image, contentDescription = "Upload Image")
                }

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask about styles, colors...") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    maxLines = 3
                )

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            val userMsg = ChatMessage(text = inputText, isFromUser = true)
                            messages = messages + userMsg
                            val currentInput = inputText
                            inputText = ""

                            // Scroll to bottom
                            coroutineScope.launch { listState.animateScrollToItem(messages.size - 1) }

                            // TODO: Call your Retrofit API here: process_chat(user_id, currentInput)
                            // On Success:
                            // val agentMsg = parseAgentMessage(apiResponse.message)
                            // messages = messages + agentMsg
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.clip(CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        }
    }
}
@Composable
fun ChatBubbleUnified(message: ChatMessage, navController: NavController) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth(0.85f) // Don't let bubbles stretch all the way across
        ) {
            // Agent Avatar
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

            // Text Bubble
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
                contentPadding = PaddingValues(start = 36.dp) // Align with agent text
            ) {
                items(message.productIdsToRender) { productId ->
                    // Replace this with your actual Product Card composable
                    ElevatedCard(
                        onClick = { navController.navigate("productdisplay/$productId") },
                        modifier = Modifier.width(140.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            // Placeholder for image
                            Box(modifier = Modifier.fillMaxWidth().height(120.dp).background(Color.LightGray))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("View Product", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}