package com.rohan.fablefit.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.rohan.fablefit.ui.Cart.CartModelUiState
import com.rohan.fablefit.ui.Cart.CartViewModel

@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
) {

    val user = FirebaseAuth.getInstance().currentUser
    val uiState = cartViewModel.uiState

    LaunchedEffect(Unit) {
        cartViewModel.getUserCart(user?.uid ?: "")
    }

    when (uiState) {

        is CartModelUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is CartModelUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: ${uiState.message}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        is CartModelUiState.Success -> {

            val cart = uiState.cart

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Cart ID: ${cart.id}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Total Price: ₹${cart.totalPrice}",
                    style = MaterialTheme.typography.titleMedium
                )

                HorizontalDivider()

                if (cart.items.isEmpty()) {
                    Text("Cart is empty")
                } else {

                    Text(
                        text = "Items (${cart.items.size})",
                        style = MaterialTheme.typography.titleMedium
                    )

                    cart.items.forEachIndexed { index, item ->

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {

                                Text("Item ${index + 1}")

                                Text("Product ID: ${item.productId}")
                                Text("Size: ${item.size}")
                                Text("Color: ${item.color ?: "N/A"}")
                                Text("Quantity: ${item.quantity}")
                            }
                        }
                    }
                }
            }
        }

        else -> {}
    }
}