package com.rohan.fablefit.Screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import kotlin.collections.filter
import kotlin.math.max
import kotlin.ranges.rangeTo
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchScreen(
    query: String,
    filters: SearchFilters?,
    onProductClick: ((Product) -> Unit)? = null,
    productViewModel: ProductViewModel = viewModel()
) {
    val haptic=LocalHapticFeedback.current
    val uiState = productViewModel.uiState
    var activeFilter by remember { mutableStateOf<SearchFilters?>(filters) }
    LaunchedEffect(query, filters) {
        // Optional: debounce to avoid hitting the API on every single keystroke
        delay(300)

        productViewModel.searchProduct(query)//product from backend fetcher
    }

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
        ) {
            when (uiState) {
                is ProductModelUiState.Initial -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Search Something")
                    }
                }

                is ProductModelUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularWavyProgressIndicator()
                    }
                }

                is ProductModelUiState.SuccessList -> {
                    val products = uiState.products
                    val filteredProduct =remember(products,activeFilter) {filterProducts(products,activeFilter)  }
                    if (products.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No products found for '$query'")
                        }
                    } else {

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            itemsIndexed(items = filteredProduct) { _, product ->
                                ProductCard(
                                    product = product,
                                    onProductClick = { onProductClick?.invoke(product) }
                                )
                            }
                        }
                    }
                }

                is ProductModelUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(uiState.message)
                    }
                }

                else -> {}
            }
        }
        var expandedModalSheet by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ExtendedFloatingActionButton(
            text = { Text("Filters") },
            icon = { Icon(Icons.Default.FilterList, contentDescription = "filter button") },
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                expandedModalSheet = !expandedModalSheet
                      },
            modifier = Modifier.align(Alignment.BottomEnd).padding(18.dp)
        )

        if(expandedModalSheet){
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = {haptic.performHapticFeedback(HapticFeedbackType.Confirm);expandedModalSheet=!expandedModalSheet}
            ) {
                FilterBottomSheetContent(filters=activeFilter,onDismiss = {
                    haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                    expandedModalSheet = false
                }, onApply = {filters ->
                    activeFilter=filters;
                })
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class,)
@Composable
fun FilterBottomSheetContent(
    filters: SearchFilters?,
    onDismiss: () -> Unit,
    onApply: (SearchFilters) -> Unit
) {
    val haptic= LocalHapticFeedback.current
    var selectedFilters by remember { mutableStateOf<SearchFilters?>(null) }
    var selectedGender by remember { mutableStateOf(filters?.category ?:"") }
    var minPrice by remember { mutableStateOf(filters?.minPrice ?: 100f) }
    var maxPrice by remember { mutableStateOf(filters?.maxPrice ?:10000f) }
    var selectedSizes by remember { mutableStateOf<List<String>>(filters?.sizes ?: emptyList()) }
    var selectedColor by remember { mutableStateOf<List<String>>(filters?.colors?:emptyList()) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Filters", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = {  }) {
                Text("Clear All")
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Gender", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            val genderCat=listOf("Men","Women","Unisex")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                genderCat.forEach { it->
                    FilterChip(
                        selected = selectedGender==it,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType. ContextClick)
                            selectedGender=it},
                        label = {Text(it)}
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Sizes", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val sizes = listOf("XS", "S", "M", "XL", "XXL")
                sizes.forEach { size ->
                    FilterChip(
                        selected = size in selectedSizes,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType. ContextClick)
                            selectedSizes =
                                if (size in selectedSizes) {
                                    selectedSizes - size
                                } else {
                                    selectedSizes + size
                                }
                        },
                        label = { Text(size) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Colors", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val colors = listOf(
                    "Black",
                    "White",
                    "Navy",
                    "Beige",
                    "Brown",
                    "Olive",
                    "Maroon",
                    "Charcoal"
                )
                colors.forEach { color ->
                    FilterChip(
                        selected = color in selectedColor,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType. ContextClick)
                            selectedColor =
                                if (color in selectedColor) {
                                    selectedColor - color
                                } else {
                                    selectedColor + color
                                }
                        },
                        label = { Text(color) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            var sliderPosition by remember { mutableStateOf(minPrice..maxPrice) }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Price Range", style = MaterialTheme.typography.titleMedium)
                Text("₹${sliderPosition.start.toInt()} - ₹${sliderPosition.endInclusive.toInt()}")
            }
            RangeSlider(
                modifier = Modifier.fillMaxWidth().padding(3.dp),
                value = sliderPosition,
                onValueChange = {
                    if (it.start != sliderPosition.start || it.endInclusive != sliderPosition.endInclusive) {
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                    }
                    sliderPosition = it;
                    minPrice = sliderPosition.start;
                    maxPrice = sliderPosition.endInclusive;
                },
                valueRange = 0f..5000f,
                steps = 100
            )
        }

        Button(
            onClick = {
                val updatedFilters = SearchFilters(
                    category = selectedGender,
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    sizes = selectedSizes,
                    colors = selectedColor
                )
                onApply(updatedFilters);
                onDismiss();
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Apply Filters")
        }
    }
}
// Remove @Composable from here; it's just logic
fun filterProducts(allProductsList: List<Product>, filters: SearchFilters?): List<Product> {
    if (filters == null) return allProductsList

    // Improved naming and price range logic
    val priceMin = filters.minPrice?:0f
    val priceMax = filters.maxPrice?:5000f

    return allProductsList.filter { p ->
        val matchGender = filters.category.isNullOrEmpty() || filters.category == p.category
        val matchSizes = filters.sizes.isEmpty() || p.sizes.any { it in filters.sizes }
        val matchColors = filters.colors.isEmpty() || filters.colors.contains(p.color)
        val matchPriceRange = p.price in priceMin..priceMax

        matchPriceRange && matchColors && matchGender && matchSizes
    }
}