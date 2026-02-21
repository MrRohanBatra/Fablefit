package com.rohan.fablefit.ui.model

data class HomeSection(
    val id: String,
    val title: String,
    val type: SectionType,
    val products: List<Product>
)

enum class SectionType {
    HORIZONTAL_LIST,
    GRID,
    FEATURED,
}
