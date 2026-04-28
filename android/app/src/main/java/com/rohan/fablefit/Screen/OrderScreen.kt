package com.rohan.fablefit.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.rohan.fablefit.ui.Order.OrderUiState
import com.rohan.fablefit.ui.Order.OrderViewModel

@Composable
fun OrderScreen(
    orderViewModel: OrderViewModel = viewModel()
) {

    val user = FirebaseAuth.getInstance().currentUser
    val uiState by orderViewModel.uiState.collectAsState()

    // 🔥 Load orders on screen open
    LaunchedEffect(Unit) {
        user?.uid?.let {
            orderViewModel.loadOrders(it)
        }
    }

    when (uiState) {

        is OrderUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is OrderUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text((uiState as OrderUiState.Error).message)
            }
        }

        is OrderUiState.Success -> {

            val orders = (uiState as OrderUiState.Success).orders

            if (orders.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Orders Yet")
                }
                return
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(orders) { order ->

                    Card(
                        shape = MaterialTheme.shapes.medium
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {

                            // 🧾 Order ID
                            Text(
                                text = "Order #${order.order_id}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            //Address
                            Text(
                                text = "₹${order.address}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            // 💰 Total
                            Text(
                                text = "₹${order.total}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            // 📦 Status
                            Text(
                                text = order.status.uppercase(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = getStatusColor(order.status)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // 🔥 ACTION ROW
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {

                                // Track button
                                OutlinedButton(
                                    onClick = {
                                        orderViewModel.trackOrder(order.order_id)
                                    }
                                ) {
                                    Text("Track")
                                }

                                // Cancel button (only if not delivered)
                                if (order.status.lowercase() != "delivered") {
                                    TextButton(
                                        onClick = {
                                            orderViewModel.cancelOrder(order.order_id)
                                        }
                                    ) {
                                        Text(
                                            "Cancel",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        else -> {}
    }
}
@Composable
fun getStatusColor(status: String) = when (status.lowercase()) {
    "delivered" -> MaterialTheme.colorScheme.primary
    "shipped" -> MaterialTheme.colorScheme.tertiary
    "cancelled" -> MaterialTheme.colorScheme.error
    else -> MaterialTheme.colorScheme.secondary
}