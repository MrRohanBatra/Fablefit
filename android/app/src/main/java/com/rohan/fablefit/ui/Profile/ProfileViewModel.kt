package com.rohan.fablefit.ui.Profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohan.fablefit.ui.model.UserModel
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repository = ProfileRepository()

    var uiState by mutableStateOf<ProfileUiState>(ProfileUiState.Loading)
        private set

    fun loadUser(uid: String) {
        viewModelScope.launch {
            uiState = ProfileUiState.Loading
            repository.getUserProfile(uid)
                .onSuccess { uiState = ProfileUiState.Success(it) }
                .onFailure { uiState = ProfileUiState.Error(it.message ?: "Failed to load profile") }
        }
    }
}

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val user: UserModel) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}
