package com.rohan.fablefit.ui.Product

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohan.fablefit.ui.Home.HomeUiState
import com.rohan.fablefit.ui.model.HomeSection
import com.rohan.fablefit.ui.model.Product
import kotlinx.coroutines.launch

class ProductViewModel(): ViewModel(
) {
    private val productRepository= ProductRepository()
    var uiState by mutableStateOf<ProductModelUiState?>(null)
        private  set
    fun loadProduct(productId: String){
        viewModelScope.launch {
            uiState= ProductModelUiState.Loading
            productRepository.getProductById(id =productId)
                .onSuccess {
                    uiState= ProductModelUiState.Success(it)
                }
                .onFailure {err->
                    uiState= ProductModelUiState.Error(err.message ?: "Failed to load product")
                }
        }
    }
    fun searchProduct(searchQuery: String,limit: Int=10){
        if (searchQuery.isBlank()) {
            uiState = ProductModelUiState.Initial
            return // Stop the function here so no API call is made
        }
        viewModelScope.launch {
            uiState= ProductModelUiState.Loading
            productRepository.searchProduct(searchQuery,limit)
                .onSuccess {
                    uiState= ProductModelUiState.SuccessList(it)
                }
                .onFailure {
                    uiState= ProductModelUiState.Error("Failed to load")
                }
        }
    }
}
sealed class ProductModelUiState {
    object Initial : ProductModelUiState()
    object Loading : ProductModelUiState()
    data class Success(val product: Product) : ProductModelUiState()
    data class SuccessList(val products:List<Product>): ProductModelUiState()
    data class Error(val message: String) : ProductModelUiState()
}