package com.rohan.fablefit.ui.Home

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.rohan.fablefit.ui.model.HomeSection
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeSectionsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UiRepository(getApplication<Application>().applicationContext)

    var uiState by mutableStateOf<HomeUiState>(HomeUiState.Loading)
        private set

    init {
        loadHome()
    }

    fun loadHome() {
        viewModelScope.launch {

            uiState = HomeUiState.Loading

            repository.getHomeSections()
                .onSuccess {
                    uiState = HomeUiState.Success(it)
                }
                .onFailure {
                    uiState = HomeUiState.Error(
                        it.message ?: "Something went wrong"
                    )
                }
        }
    }
}
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val sections: List<HomeSection>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}