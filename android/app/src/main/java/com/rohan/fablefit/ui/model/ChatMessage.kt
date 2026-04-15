package com.rohan.fablefit.ui.model

import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isFromUser: Boolean,
    val productIdsToRender: List<String> = emptyList(),
    val isLoading: Boolean = false // Useful for typing indicators
)

fun parseAgentMessage(rawMessage: String): ChatMessage {
    // 1. Regex to find the tags
    val regex = "\\[RENDER_PRODUCT:\\s*([a-zA-Z0-9_]+)\\]".toRegex()

    // 2. Extract IDs
    val productIds = regex.findAll(rawMessage).map { it.groupValues[1] }.toList()

    // 3. Remove tags from the text
    val cleanText = rawMessage.replace(regex, "").trim()

    return ChatMessage(
        text = cleanText,
        isFromUser = false,
        productIdsToRender = productIds
    )
}