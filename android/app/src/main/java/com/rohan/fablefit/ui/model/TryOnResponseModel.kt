package com.rohan.fablefit.ui.model

import com.google.gson.annotations.SerializedName

data class TryOnResponseModel(
    @SerializedName("task_id") val taskId: String,
    @SerializedName("status") val status: String, //running,completed,error
    @SerializedName("result_path") val resultPath: String? = null,
    @SerializedName("error") val error: String? = null
)
