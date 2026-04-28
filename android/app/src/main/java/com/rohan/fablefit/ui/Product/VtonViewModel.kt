package com.rohan.fablefit.ui.Product

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohan.fablefit.BuildConfig
import com.rohan.fablefit.ui.model.VtonReponseModel
import com.rohan.fablefit.ui.model.VtonRequestModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class VtonViewModel : ViewModel() {
    private val repo: VtonRepository = VtonRepository()

    // Initializing as null or a specific "Idle" state is often better,
    // but here we'll use a nullable state to represent "not started"
    var uiState by mutableStateOf<VtonUIState?>(null)
        private set

    fun startVirtualTryOn(uid: String, productId: String) {
        viewModelScope.launch {
            uiState = VtonUIState.Loading("Initiating Try-On...")

            val item = VtonRequestModel(uid, productId)
            val result = repo.tryon(item)

            result.onSuccess { response ->
                startPolling(response)
            }.onFailure { error ->
                uiState = VtonUIState.Error(error.message ?: "Failed to connect to server")
            }
        }
    }

    private fun startPolling(item: VtonReponseModel) {
        viewModelScope.launch {
            // Initial smart delay based on queue position (10s per position)
            val initialDelay = (10 * item.queuePos).seconds
            uiState = VtonUIState.Loading("Waiting in queue (Position: ${item.queuePos})...")
            delay(initialDelay)

            var isChecking = true
            while (isChecking) {
                val statusResult = repo.getStatus(item.taskId)

                statusResult.onSuccess { statusData ->
                    when (statusData.status) {
                        "done" -> {
                            // Pointing to your FastAPI proxy result endpoint
                            val finalUrl = "api/vton/result/${item.taskId}"
                            uiState = VtonUIState.Success(finalUrl)
                            isChecking = false
                        }
                        "failed" -> {
                            uiState = VtonUIState.Error(statusData.error ?: "AI generation failed")
                            isChecking = false
                        }
                        "running" -> {
                            uiState = VtonUIState.Loading("AI Stylist is generating your look...")
                            delay(3.seconds) // Poll frequently during active generation
                        }
                        "queued" -> {
                            uiState = VtonUIState.Loading("Still in queue...")
                            delay(5.seconds) // Poll less frequently while waiting
                        }
                    }
                }.onFailure {
                    uiState = VtonUIState.Error("Lost connection to the styling service.")
                    isChecking = false
                }
            }
        }
    }

    fun reset() {
        uiState = null
    }
}

sealed class VtonUIState {
    data class Loading(val message: String) : VtonUIState()
    data class Success(val imagePath: String) : VtonUIState()
    data class Error(val message: String) : VtonUIState()
}