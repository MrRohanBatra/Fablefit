package com.rohan.fablefit.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatbotViewModel : ViewModel() {

    private val _messages = MutableStateFlow(listOf(
        ChatMessage(text = "Hi! I'm Raspberry, your Fablefit assistant.", isFromUser = false)
    ))
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    fun onTextChanged(newText: String) {
        _inputText.value = newText
    }

    fun sendMessage() {
        val textToSend = _inputText.value.trim()
        if (textToSend.isNotEmpty()) {
            val userMsg = ChatMessage(text = textToSend, isFromUser = true)
            _messages.value += userMsg
            _inputText.value = ""

            // Start the thinking and response simulation
            simulateThinkingAndResponse(textToSend)
        }
    }

    private fun simulateThinkingAndResponse(userQuery: String) {
        viewModelScope.launch {
            // 1. Create a "Thinking" placeholder with a specific ID
            val thinkingId = "thinking_placeholder"
            val thinkingMsg = ChatMessage(
                id = thinkingId,
                text = "Raspberry is thinking...",
                isFromUser = false,
                isLoading = true
            )
            _messages.value += thinkingMsg

            // 2. Artificial delay to simulate LLM processing
            delay(1500)

            // 3. Mock a raw response from an LLM (containing your custom tags)
            val rawResponse = when {
                userQuery.contains("shirt", ignoreCase = true) ->
                    "I found some great shirts for you! [RENDER_PRODUCT:shirt_001] [RENDER_PRODUCT:shirt_002]"
                userQuery.contains("jeans", ignoreCase = true) ->
                    "Check these out: [RENDER_PRODUCT:denim_77]"
                else -> "I'm not sure about that, but here's a popular pick: [RENDER_PRODUCT:bestseller_1]"
            }

            // 4. Parse the raw response using your helper
            val parsedMsg = parseAgentMessage(rawResponse)

            // 5. Replace the "Thinking" message with the actual response
            _messages.value = _messages.value.map {
                if (it.id == thinkingId) parsedMsg else it
            }
        }
    }
}