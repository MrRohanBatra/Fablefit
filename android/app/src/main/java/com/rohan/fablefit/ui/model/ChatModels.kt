package com.rohan.fablefit.ui.model

import com.google.gson.annotations.SerializedName

//package com.rohan.fablefit.ui.model
//
//import com.google.gson.annotations.SerializedName
//
//data class ChatMessage(
//    val text: String,
//    val isFromUser: Boolean,
//    val productIdsToRender: List<String> = emptyList()
//)

data class ChatResponse(
    @SerializedName("message") val message: String
)