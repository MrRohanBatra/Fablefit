package com.rohan.fablefit.auth

data class AuthUiState(
    val isLoading: Boolean=false,
    val isLoggedIn: Boolean=false,
    val errorMessage: String?=null,
    )
