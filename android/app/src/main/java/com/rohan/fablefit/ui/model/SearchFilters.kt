package com.rohan.fablefit.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchFilters(
    val query: String? = null,
    val category: String? = null,
    val sizes: List<String> = emptyList(),
    val colors: List<String> = emptyList(),
    val minPrice: Float? = null,
    val maxPrice: Float? = null,
    val vton_category: String? = null,
    val brands: List<String> = emptyList(),
    val inStockOnly: Boolean = false
) : Parcelable