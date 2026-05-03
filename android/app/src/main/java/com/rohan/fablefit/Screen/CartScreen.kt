package com.rohan.fablefit.Screen

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.SubcomposeAsyncImage
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.auth.FirebaseAuth
import com.rohan.fablefit.ui.Cart.CartModelUiState
import com.rohan.fablefit.ui.Cart.CartViewModel
import com.rohan.fablefit.ui.Order.OrderViewModel
import com.rohan.fablefit.ui.Product.ProductViewModel
import java.util.Locale

@SuppressLint("MissingPermission")
@Composable
fun CartScreen(
    context: Context,
    cartViewModel: CartViewModel,
    productViewModel: ProductViewModel= viewModel(),
    orderViewModel: OrderViewModel,
) {

    val user = FirebaseAuth.getInstance().currentUser
    val uiState = cartViewModel.uiState
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    LaunchedEffect(Unit) {
        cartViewModel.getUserCart(user?.uid ?: "")
    }
    val placeResult by orderViewModel.placeOrderResult.collectAsState()

    LaunchedEffect(placeResult) {
        placeResult?.let {
            // 🔥 Show toast or snackbar
            println("Order placed: ${it.order_id}")
            Toast.makeText(context,"Order placed: ${it.order_id}", Toast.LENGTH_LONG).show();

            // Optional: refresh cart
            cartViewModel.getUserCart(user?.uid ?: "")
        }
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

/*        is CartModelUiState.Success -> {

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
*/
        is CartModelUiState.Success -> {

            val cart = uiState.cart

            if (cart.items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Cart is empty")
                }
                return
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(cart.items) { item ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val imageUrl by produceState<String?>(initialValue = null, item.productId) {
                                value = productViewModel.getProductImageUrl(item.productId)
                            }
                            // 🔹 PRODUCT IMAGE (MAIN FOCUS)
                            if(imageUrl!=null){
                                SubcomposeAsyncImage(
                                    model = imageUrl, // 👈 make sure backend gives this
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop,
                                    loading = {
                                        Box(
                                            modifier = Modifier.size(90.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // 🔹 MINIMAL DETAILS (NOT TEXT HEAVY)
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {

                                Text(
                                    text = "Qty: ${item.quantity}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )

                                item.size.let {
                                    Text(
                                        text = "Size: $it",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                item.color?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // 🔹 TOTAL SECTION (keep simple)
//                item {
//                    Spacer(modifier = Modifier.height(12.dp))
//
//                    Card(
//                        shape = RoundedCornerShape(16.dp)
//                    ) {
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(16.dp),
//                            horizontalArrangement = Arrangement.SpaceBetween
//                        ) {
//                            Text("Total", style = MaterialTheme.typography.titleMedium)
//                            Text("₹${cart.totalPrice}", style = MaterialTheme.typography.titleMedium)
//                        }
//                    }
//                }
                item {
                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total", style = MaterialTheme.typography.titleMedium)
                            Text("₹${cart.totalPrice}", style = MaterialTheme.typography.titleMedium)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 🔥 PLACE ORDER BUTTON
                    Button(
                        onClick = {
                            val uid = user?.uid ?: return@Button
                            fusedLocationClient.getCurrentLocation(
                                Priority.PRIORITY_HIGH_ACCURACY,
                                CancellationTokenSource().token
                            ).addOnSuccessListener { location ->
                              if(location!=null){
                                  val addr=getAddressFromLocation(
                                      context,
                                      location.latitude,
                                      location.longitude
                                  )
                                  orderViewModel.placeOrder(uid,addr);
                              }
                            }

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Place Order")
                    }
                }
            }
        }
        else -> {}
    }
}
fun getAddressFromLocation(
    context: Context,
    latitude: Double,
    longitude: Double
): String {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)

        if (!addresses.isNullOrEmpty()) {
//            val addr = addresses[0].getAddressLine(0)
//
//            listOfNotNull(
//                addr.featureName,
//                addr.subLocality,
//                addr.locality,
//                addr.adminArea,
//                addr.countryName
//            ).joinToString(", ")
            addresses[0].getAddressLine(0)
        } else {
            "Delhi"
        }
    } catch (e: Exception) {
        "Noida-error"
    }
}