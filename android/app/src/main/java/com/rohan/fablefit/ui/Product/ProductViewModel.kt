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
    var uiState by mutableStateOf<ProductModelUiState>(ProductModelUiState.Loading)
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
}
sealed class ProductModelUiState {
    object Loading : ProductModelUiState()
    data class Success(val product: Product) : ProductModelUiState()
    data class Error(val message: String) : ProductModelUiState()
}