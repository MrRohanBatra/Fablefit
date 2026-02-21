package com.rohan.fablefit.ui.model

data class CategorySectionModel(
    val imagePath:String?=null,
    val title:String?=null,
    val onClick:()-> Unit,
)
