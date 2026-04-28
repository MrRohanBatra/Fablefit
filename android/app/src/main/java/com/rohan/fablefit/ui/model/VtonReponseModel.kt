package com.rohan.fablefit.ui.model

import com.google.gson.annotations.SerializedName

data class VtonReponseModel(
    @SerializedName("task_id")
    val taskId:String,
    @SerializedName("message")
    val message: String,
    @SerializedName("position_in_queue")
    val queuePos: Int,
)
