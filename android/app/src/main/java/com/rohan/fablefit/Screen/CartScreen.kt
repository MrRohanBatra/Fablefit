package com.rohan.fablefit.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.rohan.fablefit.ui.Cart.CartModelUiState
import com.rohan.fablefit.ui.Cart.CartViewModel

// Tier colours — kept consistent with ProfileScreen
val TierGold   = Color(0xFFFFD700)
val TierSilver = Color(0xFFB0BEC5)
val TierBronze = Color(0xFFCD7F32)

fun tierColor(tier: String) = when (tier) {
    "Gold"   -> TierGold
    "Silver" -> TierSilver
    else     -> TierBronze
}

@Composable
fun CartScreen(cartViewModel: CartViewModel) {

    val user     = FirebaseAuth.getInstance().currentUser
    val uiState  = cartViewModel.uiState

    LaunchedEffect(Unit) {
        cartViewModel.getUserCart(user?.uid ?: "")
    }

    when (uiState) {

        is CartModelUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is CartModelUiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text  = "Error: ${uiState.message}",
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

                // ── Discount / Tier Banner ────────────────────────────────────
                if (cart.hasDiscount) {
                    Surface(
                        shape  = RoundedCornerShape(14.dp),
                        color  = tierColor(cart.tier).copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, tierColor(cart.tier)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier            = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment   = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector        = Icons.Default.LocalOffer,
                                contentDescription = null,
                                tint               = tierColor(cart.tier)
                            )
                            Column {
                                Text(
                                    text       = "${cart.tier} Member Discount Applied",
                                    style      = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color      = tierColor(cart.tier)
                                )
                                Text(
                                    text  = "You're saving ${cart.discountLabel} on this order!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // ── Total ─────────────────────────────────────────────────────
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text  = "Total",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text       = "₹${cart.totalPrice}",
                        style      = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color      = MaterialTheme.colorScheme.primary
                    )
                }

                HorizontalDivider()

                // ── Items ─────────────────────────────────────────────────────
                if (cart.items.isEmpty()) {
                    Box(
                        modifier        = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text  = "Your cart is empty",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Text(
                        text  = "Items (${cart.items.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    cart.items.forEachIndexed { index, item ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    "Item ${index + 1}",
                                    style      = MaterialTheme.typography.labelSmall,
                                    color      = MaterialTheme.colorScheme.secondary
                                )
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
