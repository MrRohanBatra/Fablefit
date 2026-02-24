package com.rohan.fablefit.Screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rohan.fablefit.ui.Product.ProductModelUiState
import com.rohan.fablefit.ui.Product.ProductViewModel
import com.rohan.fablefit.ui.model.Product
import com.rohan.fablefit.ui.model.ProductCard
import com.rohan.fablefit.ui.model.SearchFilters
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchScreen(
    query: String,
    filters: SearchFilters?,
    onProductClick: ((Product) -> Unit)? = null,
    productViewModel: ProductViewModel = viewModel()
) {
    val uiState = productViewModel.uiState
    var searchQuery by remember {
        mutableStateOf(
            // 1. Check if the filter has a valid query
            filters?.query?.takeIf { it.isNotBlank() }
            // 2. If it doesn't, fall back to the main query
                ?: query
        )
    }
    LaunchedEffect(query, filters) {
        delay(300)
        // We removed the 'if (query.isNotEmpty())' check here.
        // Why? Because our updated ViewModel already checks if the query is blank
        // and safely resets the state to Initial without making an API call.
        productViewModel.searchProduct(searchQuery)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
    ) {
        when (uiState) {
            // 1. The Initial State (Empty search bar)
            is ProductModelUiState.Initial -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Search Something")
                }
            }

            // 2. The Loading State (Fetching data)
            is ProductModelUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Using the indicator you imported!
                    CircularWavyProgressIndicator()
                }
            }

            // 3. The Success State (Got data)
            is ProductModelUiState.SuccessList -> {
                val products = uiState.products

                if (products.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No products found")
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        itemsIndexed(items = products) { index, product ->
                            ProductCard(
                                product = product,
                                onProductClick = {
                                    onProductClick?.invoke(product)
                                }
                            )
                        }
                    }
                }
            }

            // 4. The Error State
            is ProductModelUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(uiState.message) // Use the actual error message here
                }
            }

            else -> {}
        }
    }
}
//
//@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
//@Composable
//fun SearchScreen(
//    query: String,
//    filters: SearchFilters?,
//    onProductClick: ((Product) -> Unit)? = null,
//    productViewModel: ProductViewModel= viewModel()
//) {
//    val uiState = productViewModel.uiState
//
//// Trigger search when query changes
//    LaunchedEffect(query, filters) {
//        if (query.isNotEmpty()) {
//            productViewModel.searchProduct(query)
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .clip(RoundedCornerShape(20.dp))
//    ) {
//
//        when (uiState) {
//
//            is ProductModelUiState.Loading -> {
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text("Search Something")
//                }
//            }
//
//            is ProductModelUiState.SuccessList -> {
//
//                val products = uiState.products
//
//                if (products.isEmpty()) {
//                    Box(
//                        modifier = Modifier.fillMaxSize(),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text("No products found")
//                    }
//                } else {
//                    LazyVerticalGrid(
//                        columns = GridCells.Fixed(2),
//                        contentPadding = PaddingValues(16.dp),
//                        verticalArrangement = Arrangement.spacedBy(16.dp),
//                        horizontalArrangement = Arrangement.spacedBy(16.dp)
//                    ) {
//                        itemsIndexed(items=products) { index,product ->
//                            ProductCard(
//                                product = product,
//                                onProductClick = {
//                                    onProductClick?.invoke(product)
//                                }
//                            )
//                        }
//                    }
//                }
//            }
//
//            is ProductModelUiState.Error -> {
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text("Something went wrong")
//                }
//            }
//
//            else -> {}
//        }
//    }
//}

fun filterProducts(allProductsList: List<Product>, filters: SearchFilters?): List<Product> {

    val query=if(filters?.query !=null){
        filters.query
    }
    else{
        ""
    }
    // 4. Otherwise, perform the actual filtering logic
    return allProductsList.filter { product ->
        val matchesQuery =
                product.name.contains(query.trim(), ignoreCase = true)

        val matchesCategory = filters?.category == null ||
                product.category.equals(filters.category, ignoreCase = true)

        val matchesVton = filters?.vton_category == null ||
                product.vton_category?.equals(filters.vton_category, ignoreCase = true) == true

        matchesQuery && matchesCategory && matchesVton
    }
}