package com.rohan.fablefit.Screen
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.rohan.fablefit.BuildConfig
import com.rohan.fablefit.ui.Cart.CartModelUiState
import com.rohan.fablefit.ui.Cart.CartViewModel
import com.rohan.fablefit.ui.Product.ProductModelUiState
import com.rohan.fablefit.ui.Product.ProductViewModel
import com.rohan.fablefit.ui.Wishlist.WishlistViewModel
import com.rohan.fablefit.ui.model.CartItem
import com.rohan.fablefit.ui.model.CartUpdate
import com.rohan.fablefit.ui.model.Product
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProductDisplayScreen(
    productId: String,
    productViewModel: ProductViewModel= viewModel(),
    cartViewModel: CartViewModel,
    wishlistViewModel: WishlistViewModel = viewModel(),
) {
    val state     = productViewModel.uiState
    val cartState = cartViewModel.uiState
    val user      = FirebaseAuth.getInstance().currentUser
    val context   = LocalContext.current
    val scope     = rememberCoroutineScope()
    val haptic    = LocalHapticFeedback.current

    LaunchedEffect(productId) {
        productViewModel.loadProduct(productId)

        user?.uid?.let { wishlistViewModel.loadWishlist(it) }
    }
    LaunchedEffect(cartState) {
        if (cartState is CartModelUiState.Success) {
            // You can add logic here to only show toast if an action was just performed
            // For now, simple feedback:
            // Toast.makeText(context, "Cart Updated", Toast.LENGTH_SHORT).show()
        } else if (cartState is CartModelUiState.Error) {
            Toast.makeText(context, cartState.message, Toast.LENGTH_SHORT).show()
        }
    }
    when(state){
        is ProductModelUiState.Loading->{
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                LoadingIndicator()
            }
        }
        is ProductModelUiState.Success -> {
            val product   = state.product
            var selectedSize by remember { mutableStateOf("S") }
            val isInCart      = cartViewModel.isProductInCart(productId, selectedSize)
            val productQty    = cartViewModel.getProductQuantity(productId, selectedSize)
            val isUpdating    = cartState is CartModelUiState.ItemUpdate
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
                // 🔹 Header / Image Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f/3f)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    ProductImagePager(
                        modifier = Modifier.fillMaxSize(),
                        images   = product.images,
                        baseUrl  = BuildConfig.BASE_URL
                    )
                    if (product.supportsTryOn) {

                        Surface(
                            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(12.dp),
                            tonalElevation = 4.dp
                        ) {
                            Text(
                                "TRY ON",
                                Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
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
                            imageVector = if (isWishlisted) Icons.Filled.Favorite
                            else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (isWishlisted) "Remove from wishlist"
                            else "Add to wishlist",
                            tint               = heartTint,
                        )
                    }
                }
                Column(modifier = Modifier.padding(24.dp)) {


                    Text(
                        text = product.companyName.uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 📦 Product Name
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 💰 Price: Using a "Container" role for high attraction
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = product.formattedPrice,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 📝 Description
                    Text(
                        text = product.description,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // 📏 Size Selection: FilterChips for interactivity
                    if (product.sizes.isNotEmpty()) {
                        Text(
                            "Select Size",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(product.sizes) { size ->
                                FilterChip(
                                    selected = selectedSize==size,
                                    onClick = { selectedSize = size },
                                    label = { Text(size, modifier = Modifier.padding(vertical = 4.dp)) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    border = null,
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically // Keeps everything aligned
                    ) {
                        var vtonClicked by remember { mutableStateOf(false) }

                        // This button now stays visible regardless of cart state
                        OutlinedButton(
                            enabled = !vtonClicked,
                            onClick = {
                                if (!vtonClicked) {
                                    vtonClicked = true
                                    Toast.makeText(context, "Upcoming feature", Toast.LENGTH_SHORT).show()
                                    scope.launch {
                                        delay(300)
                                        vtonClicked = false
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Icon(Icons.Default.Camera, null)
                        }

                        // We wrap the conditional logic in a Box or Surface with weight(3f)
                        // to ensure it occupies the same space the "Add to Cart" button did.
                        Box(
                            modifier = Modifier.weight(3f).height(56.dp),
                            contentAlignment = Alignment.Center
                        )
                        {
                            if (isInCart) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    IconButton(onClick = {;
                                        cartViewModel.updateItemInCart(CartUpdate(
                                        uid=user?.uid?:"",
                                        productId=productId,
                                        size=selectedSize,
                                        color = product.color,
                                        quantity = productQty-1,
                                    ))  },
                                        enabled = !isUpdating) {
                                        Icon(Icons.Filled.Remove, null)
                                    }

                                    Text(
                                        text = "$productQty",
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    IconButton(onClick = {
                                        cartViewModel.updateItemInCart(CartUpdate(
                                        uid=user?.uid?:"",
                                        productId=productId,
                                        size=selectedSize,
                                        color = product.color,
                                        quantity = productQty+1,
                                    )) },enabled = !isUpdating) {
                                        Icon(Icons.Filled.Add, null)
                                    }
                                    FilledTonalIconButton(
                                        onClick = {
                                            cartViewModel.removeItemFromCart(CartUpdate(
                                                uid=user?.uid?:"",
                                                productId=productId,
                                                size=selectedSize,
                                                color = null,
                                                quantity = productQty,
                                            ))
                                        },
                                        modifier = Modifier.size(48.dp), // Standard icon button size
                                        shape = RoundedCornerShape(10.dp),
                                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.errorContainer,
                                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                                        ),
                                        enabled = !isUpdating
                                    ) {
                                        Icon(Icons.Outlined.Delete, contentDescription = "Delete")
                                    }
                                }
                            } else {
                                val isAdding=cartState is CartModelUiState.Loading
                                Button(
                                    onClick = {
                                        val cartUpdateItem = CartUpdate(
                                            uid = user?.uid ?: "",
                                            productId = productId,
                                            size = selectedSize ,
                                            color = product.color,
                                            quantity = 1,
                                        )
                                        cartViewModel.addItemToCart(cartUpdateItem)
//                                        if (cartState is CartModelUiState.Success) {
//                                            Toast.makeText(context, "Added ${product.name} to Cart", Toast.LENGTH_SHORT).show()
//                                        }
                                    },
                                    enabled = !isAdding,
                                    modifier = Modifier.fillMaxSize(), // Fill the Box area
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(4.dp)
                                ) {
                                    if (isAdding) {
                                        LoadingIndicator()
                                    } else {
                                        Text(
                                            "Add to Cart",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
        is ProductModelUiState.Error->
        {
            val message = state.message   // ✅ Correct extraction

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(text = message)

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            productViewModel.loadProduct(productId)
                        }
                    ) {
                        Text("Retry")
                    }
                }
            }

        }
        else -> {

        }
    }
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProductImagePager(
    modifier: Modifier,
    images: List<String>,
    baseUrl: String
) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    Box(
        modifier = modifier
    ) {

        HorizontalPager(
            state = pagerState
        ) { page ->

            AsyncImage(
                model = "$baseUrl${images[page]}",
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(images.size) { index ->

                val isSelected = pagerState.currentPage == index

                Box(
                    modifier = Modifier
                        .size(if (isSelected) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                )
            }
        }
    }
}
