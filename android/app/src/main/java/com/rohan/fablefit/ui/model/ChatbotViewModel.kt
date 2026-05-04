package com.rohan.fablefit.ui.model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.rohan.fablefit.network.RetrofitInstance.api
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.http.Multipart

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

    fun sendMessage(context: Context) {
        val textToSend = _inputText.value.trim()
        if (textToSend.isNotEmpty()) {
            val userMsg = ChatMessage(text = textToSend, isFromUser = true)
            _messages.value += userMsg
            _inputText.value = ""

            // Start the thinking and response simulation
            simulateThinkingAndResponse(context,textToSend)
        }
    }

//    private fun simulateThinkingAndResponse(userQuery: String) {
//        viewModelScope.launch {
//            // 1. Create a "Thinking" placeholder with a specific ID
//            val thinkingId = "thinking_placeholder"
//            val thinkingMsg = ChatMessage(
//                id = thinkingId,
//                text = "Raspberry is thinking...",
//                isFromUser = false,
//                isLoading = true
//            )
//            _messages.value += thinkingMsg
//
//            // 2. Artificial delay to simulate LLM processing
//            delay(1500)
//
//            // 3. Mock a raw response from an LLM (containing your custom tags)
//            val rawResponse = when {
//                userQuery.contains("shirt", ignoreCase = true) ->
//                    "I found some great shirts for you! [RENDER_PRODUCT:shirt_001] [RENDER_PRODUCT:shirt_002]"
//                userQuery.contains("jeans", ignoreCase = true) ->
//                    "Check these out: [RENDER_PRODUCT:denim_77]"
//                else -> "I'm not sure about that, but here's a popular pick: [RENDER_PRODUCT:bestseller_1]"
//            }
//
//            // 4. Parse the raw response using your helper
//            val parsedMsg = parseAgentMessage(rawResponse)
//
//            // 5. Replace the "Thinking" message with the actual response
//            _messages.value = _messages.value.map {
//                if (it.id == thinkingId) parsedMsg else it
//            }
//        }
//    }

private fun simulateThinkingAndResponse(context: Context, userQuery: String, imageUri: Uri?=null) {
    viewModelScope.launch {
        val thinkingId = "thinking_placeholder"

        val thinkingMsg = ChatMessage(
            id = thinkingId,
            text = "Raspberry is thinking...",
            isFromUser = false,
            isLoading = true
        )
        _messages.value += thinkingMsg

        try {
            // Prepare request bodies
            val userId= FirebaseAuth.getInstance().uid ?: "fb_user_12345";
            val userIdBody = userId.toRequestBody("text/plain".toMediaType())
            val messageBody = userQuery.toRequestBody("text/plain".toMediaType())

            val imageBody: MultipartBody.Part?=null
            // 🔥 API CALL
            imageUri?.let {
                val inputStream = context.contentResolver.openInputStream(it)
                val requestBody = inputStream?.use { it.readBytes() }?.toRequestBody("image/*".toMediaTypeOrNull())
                val imageBody = requestBody?.let {
                    MultipartBody.Part.createFormData("image", "upload.jpg", it)
                }
            }
            val response = api.chatWithAgent(
                userId = userIdBody,
                message = messageBody,
                image =imageBody
            )

            val rawResponse = if (response.isSuccessful) {
                response.body()?.message ?: "Empty response"
            } else {
                "Something went wrong"
            }

            val parsedMsg = parseAgentMessage(rawResponse)

            _messages.value = _messages.value.map {
                if (it.id == thinkingId) parsedMsg else it
            }

        } catch (e: Exception) {
            _messages.value = _messages.value.map {
                if (it.id == thinkingId) {
                    ChatMessage(
                        text = "Error: ${e.message}",
                        isFromUser = false
                    )
                } else it
            }
        }
    }
}
}