package com.rohan.fablefit.ui.model

import androidx.annotation.DrawableRes

data class BannerUiModel(
    val id: String,
    val imageUrl: String? = null,   // For backend
    val imageRes: Int? = null, // For local testing
    val title: String? = null
)
