package com.rohan.fablefit.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel(
    private val authRepository: AuthRepository= AuthRepository()
): ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState());
    private fun resultCallback(s: Boolean, e: String?) {
        if (s) {
            _uiState.value = AuthUiState(isLoggedIn = true);
        } else {
            _uiState.value = AuthUiState(errorMessage = e);
        }
    }

    val uiState: StateFlow<AuthUiState> = _uiState;
    fun loginWithEmail(email: String, passwd: String) {
        _uiState.value = AuthUiState(isLoading = true);
        authRepository.loginWithEmail(email, passwd, onResult = { s, e -> resultCallback(s, e) })
    }

    fun loginWithGoogle(idtoken: String) {
        _uiState.value = AuthUiState(isLoading = true);
        authRepository.loginWithGoogle(idtoken, onResult = {s,e->resultCallback(s,e)});
    }
    fun logOut(){
        authRepository.logOut();
    }
}