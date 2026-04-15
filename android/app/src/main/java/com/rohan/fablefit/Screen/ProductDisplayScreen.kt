package com.rohan.fablefit.Screen

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.rohan.fablefit.ui.Cart.CartModelUiState
import com.rohan.fablefit.ui.Cart.CartViewModel
import com.rohan.fablefit.ui.Product.ProductModelUiState
import com.rohan.fablefit.ui.Product.ProductViewModel
import com.rohan.fablefit.ui.Wishlist.WishlistViewModel
import com.rohan.fablefit.ui.model.CartUpdate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProductDisplayScreen(
    productId: String,
    productViewModel: ProductViewModel = viewModel(),
    cartViewModel: CartViewModel,
    wishlistViewModel: WishlistViewModel = viewModel(),   // ← NEW
) {
    val state     = productViewModel.uiState
    val cartState = cartViewModel.uiState
    val user      = FirebaseAuth.getInstance().currentUser
    val context   = LocalContext.current
    val scope     = rememberCoroutineScope()
    val haptic    = LocalHapticFeedback.current

    LaunchedEffect(productId) {
        productViewModel.loadProduct(productId)
        // Load wishlist so heart state is correct
        user?.uid?.let { wishlistViewModel.loadWishlist(it) }
    }

    LaunchedEffect(cartState) {
        if (cartState is CartModelUiState.Error) {
            Toast.makeText(context, cartState.message, Toast.LENGTH_SHORT).show()
        }
    }

    when (state) {

        is ProductModelUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator()
            }
        }

        is ProductModelUiState.Success -> {
            val product   = state.product
            var selectedSize by remember { mutableStateOf("S") }
            val isInCart      = cartViewModel.isProductInCart(productId, selectedSize)
            val productQty    = cartViewModel.getProductQuantity(productId, selectedSize)
            val isUpdating    = cartState is CartModelUiState.ItemUpdate

            // Wishlist state
            val isWishlisted  = wishlistViewModel.isWishlisted(productId)
            val heartTint by animateColorAsState(
                targetValue   = if (isWishlisted) Color(0xFFE91E63) else Color.White,
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                label         = "heartColor"
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.background)
            ) {

                // ── Image section ─────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f / 3f)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    ProductImagePager(
                        modifier = Modifier.fillMaxSize(),
                        images   = product.images,
                        baseUrl  = "https://testserver.rohan.org.in"
                    )

                    // TRY ON badge (top-start)
                    if (product.supportsTryOn) {
                        Surface(
                            modifier  = Modifier.align(Alignment.TopEnd).padding(16.dp),
                            color     = MaterialTheme.colorScheme.secondaryContainer,
                            shape     = RoundedCornerShape(12.dp),
                            tonalElevation = 4.dp
                        ) {
                            Text(
                                "TRY ON",
                                Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color      = MaterialTheme.colorScheme.onSecondaryContainer,
                                style      = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // ── Wishlist heart button (top-start of image) ────────────
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.ToggleOn)
                            user?.uid?.let { uid ->
                                wishlistViewModel.toggle(uid, productId, product.price)
                            } ?: Toast.makeText(context, "Login to wishlist items", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .background(
                                color  = Color.Black.copy(alpha = 0.35f),
                                shape  = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector        = if (isWishlisted) Icons.Filled.Favorite
                                                 else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (isWishlisted) "Remove from wishlist"
                                                 else "Add to wishlist",
                            tint               = heartTint,
                        )
                    }
                }

                // ── Info section ──────────────────────────────────────────────
                Column(modifier = Modifier.padding(24.dp)) {

                    Text(
                        text       = product.companyName.uppercase(),
                        style      = MaterialTheme.typography.labelLarge,
                        color      = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text       = product.name,
                        style      = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(16.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text       = product.formattedPrice,
                            modifier   = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style      = MaterialTheme.typography.titleLarge,
                            color      = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Text(
                        text      = product.description,
                        style     = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp,
                        color     = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(32.dp))

                    // ── Size selection ────────────────────────────────────────
                    if (product.sizes.isNotEmpty()) {
                        Text(
                            "Select Size",
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(12.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(product.sizes) { size ->
                                FilterChip(
                                    selected  = selectedSize == size,
                                    onClick   = { selectedSize = size },
                                    label     = { Text(size, modifier = Modifier.padding(vertical = 4.dp)) },
                                    colors    = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor     = MaterialTheme.colorScheme.onPrimary,
                                        containerColor         = MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    border = null,
                                    shape  = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(40.dp))

                    // ── Action row ────────────────────────────────────────────
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        // Camera / VTON button
                        var vtonClicked by remember { mutableStateOf(false) }
                        OutlinedButton(
                            enabled  = !vtonClicked,
                            onClick  = {
                                if (!vtonClicked) {
                                    vtonClicked = true
                                    Toast.makeText(context, "Upcoming feature", Toast.LENGTH_SHORT).show()
                                    scope.launch { delay(300); vtonClicked = false }
                                }
                            },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape    = RoundedCornerShape(16.dp),
                            border   = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Icon(Icons.Default.Camera, null)
                        }

                        // Cart action
                        Box(
                            modifier        = Modifier.weight(3f).height(56.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isInCart) {
                                Row(
                                    verticalAlignment     = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier              = Modifier.fillMaxWidth()
                                ) {
                                    IconButton(
                                        onClick  = {
                                            cartViewModel.updateItemInCart(
                                                CartUpdate(uid = user?.uid ?: "", productId = productId,
                                                    size = selectedSize, color = product.color,
                                                    quantity = productQty - 1)
                                            )
                                        },
                                        enabled  = !isUpdating
                                    ) { Icon(Icons.Filled.Remove, null) }

                                    Text(
                                        "$productQty",
                                        fontWeight = FontWeight.SemiBold,
                                        style      = MaterialTheme.typography.titleMedium
                                    )

                                    IconButton(
                                        onClick = {
                                            cartViewModel.updateItemInCart(
                                                CartUpdate(uid = user?.uid ?: "", productId = productId,
                                                    size = selectedSize, color = product.color,
                                                    quantity = productQty + 1)
                                            )
                                        },
                                        enabled = !isUpdating
                                    ) { Icon(Icons.Filled.Add, null) }

                                    FilledTonalIconButton(
                                        onClick  = {
                                            cartViewModel.removeItemFromCart(
                                                CartUpdate(uid = user?.uid ?: "", productId = productId,
                                                    size = selectedSize, color = null, quantity = productQty)
                                            )
                                        },
                                        modifier = Modifier.size(48.dp),
                                        shape    = RoundedCornerShape(10.dp),
                                        colors   = IconButtonDefaults.filledTonalIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.errorContainer,
                                            contentColor   = MaterialTheme.colorScheme.onErrorContainer
                                        ),
                                        enabled = !isUpdating
                                    ) { Icon(Icons.Outlined.Delete, "Delete") }
                                }
                            } else {
                                val isAdding = cartState is CartModelUiState.Loading
                                Button(
                                    onClick  = {
                                        cartViewModel.addItemToCart(
                                            CartUpdate(uid = user?.uid ?: "", productId = productId,
                                                size = selectedSize, color = product.color, quantity = 1)
                                        )
                                    },
                                    enabled  = !isAdding,
                                    modifier = Modifier.fillMaxSize(),
                                    shape    = RoundedCornerShape(16.dp),
                                    colors   = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor   = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(4.dp)
                                ) {
                                    if (isAdding) LoadingIndicator()
                                    else Text("Add to Cart", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        is ProductModelUiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.message)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { productViewModel.loadProduct(productId) }) { Text("Retry") }
                }
            }
        }

        else -> {}
    }
}

// ── Image pager (unchanged) ────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProductImagePager(modifier: Modifier, images: List<String>, baseUrl: String) {
    val pagerState = rememberPagerState(pageCount = { images.size })
    Box(modifier = modifier) {
        HorizontalPager(state = pagerState) { page ->
            AsyncImage(
                model            = "$baseUrl/${images[page]}",
                contentDescription = null,
                modifier         = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                contentScale     = ContentScale.Crop
            )
        }
        Row(
            modifier              = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(images.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                )
            }
        }
    }
}
