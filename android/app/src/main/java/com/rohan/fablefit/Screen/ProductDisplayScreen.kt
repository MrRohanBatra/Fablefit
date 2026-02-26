package com.rohan.fablefit.Screen
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.rohan.fablefit.ui.Product.ProductModelUiState
import com.rohan.fablefit.ui.Product.ProductViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProductDisplayScreen(
    productId: String,
    productViewModel: ProductViewModel= viewModel()
) {
    val state=productViewModel.uiState;
    LaunchedEffect(productId) {
        productViewModel.loadProduct(productId = productId);
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
            val product = state.product
            var selectedSize by remember { mutableStateOf<String?>(null) }

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
                    ProductImagePager(modifier = Modifier.fillMaxSize(),images = product.images, baseUrl = "https://testserver.rohan.org.in")

                    if (product.supportsTryOn) {
                        // Feature Color: Secondary (Differentiates from Primary)
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
                }

                // 🔹 Info Section
                Column(modifier = Modifier.padding(24.dp)) {

                    // 🏷️ Company Name: Using Tertiary for distinct branding
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
                                val isSelected = selectedSize == size
                                FilterChip(
                                    selected = isSelected,
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

                    // 🛒 Improved Button Layout (Horizontal Split)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        var vtonClicked by remember { mutableStateOf(false) }
                        val context=LocalContext.current
                        val scope=rememberCoroutineScope()
                        // Secondary CTA (Outline style)
                        OutlinedButton(
                            enabled = !vtonClicked,
                            onClick = {
                                if(!vtonClicked){
                                    vtonClicked=true
                                    Toast.makeText(context,"Upcoming feature",Toast.LENGTH_SHORT).show();
                                    scope.launch {
                                        delay(300)
                                        vtonClicked=false
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Icon(Icons.Default.Camera, null)
                        }

                        // Primary CTA (Prominent style)
                        Button(
                            onClick = { },
                            modifier = Modifier.weight(3f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            elevation = ButtonDefaults.buttonElevation(4.dp)
                        ) {
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
//        is ProductModelUiState.Success -> {
//            val product = state.product
//            // State for the selectable chips
//            var selectedSize by remember { mutableStateOf<String?>(null) }
//
//            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .verticalScroll(rememberScrollState())
//                        .padding(bottom = 100.dp) // Space for the floating bottom buttons
//                ) {
//                    // 🔹 Modern Hero Image Section
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(340.dp)
//                            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
//                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
//                    ) {
//                        ProductImagePager(
//                            images = product.images,
//                            baseUrl = "https://testserver.rohan.org.in"
//                        )
//
//                        // High-Contrast Try-On Badge
//                        if (product.supportsTryOn) {
//                            Surface(
//                                modifier = Modifier.align(Alignment.TopEnd).padding(20.dp),
//                                color = MaterialTheme.colorScheme.secondaryContainer,
//                                shape = AbsoluteRoundedCornerShape(10.dp),
//                                shadowElevation = 8.dp
//                            ) {
//                                Row(
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
//                                ) {
//                                    Icon(Icons.Default.Face, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onTertiary)
//                                    Spacer(Modifier.width(6.dp))
//                                    Text(
//                                        text = "TRY ON",
//                                        style = MaterialTheme.typography.labelLarge,
//                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
//                                        fontWeight = FontWeight.ExtraBold
//                                    )
//                                }
//                            }
//                        }
//                    }
//
//                    // 🔹 Content Section
//                    Column(modifier = Modifier.padding(24.dp)) {
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.SpaceBetween,
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Text(
//                                text = product.companyName.uppercase(),
//                                style = MaterialTheme.typography.labelLarge,
//                                color = MaterialTheme.colorScheme.tertiary,
//                                fontWeight = FontWeight.ExtraBold,
//                                letterSpacing = 1.5.sp
//                            )
//
//                            // Dynamic Price Tag
//                            Text(
//                                text = product.formattedPrice,
//                                style = MaterialTheme.typography.headlineSmall,
//                                color = MaterialTheme.colorScheme.onSurface,
//                                fontWeight = FontWeight.ExtraBold
//                            )
//                        }
//
//                        Spacer(modifier = Modifier.height(8.dp))
//
//                        Text(
//                            text = product.name,
//                            style = MaterialTheme.typography.headlineMedium,
//                            color=MaterialTheme.colorScheme.onPrimaryContainer,
//                            fontWeight = FontWeight.Bold
//                        )
//
//                        Spacer(modifier = Modifier.height(16.dp))
//
//                        Text(
//                            text = product.description,
//                            style = MaterialTheme.typography.bodyLarge,
//                            lineHeight = 24.sp,
//                            color = MaterialTheme.colorScheme.onSurfaceVariant
//                        )
//
//                        Spacer(modifier = Modifier.height(24.dp))
//
//                        // 🔹 Interactive Sizes
//                        if (product.sizes.isNotEmpty()) {
//                            Text(
//                                text = "Select Size",
//                                style = MaterialTheme.typography.titleMedium,
//                                fontWeight = FontWeight.Bold
//                            )
//                            Spacer(modifier = Modifier.height(12.dp))
//
//                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
//                                items(product.sizes) { size ->
//                                    FilterChip(
//                                        selected = selectedSize == size,
//                                        onClick = { selectedSize = size },
//                                        label = { Text(size, modifier = Modifier.padding(vertical = 8.dp)) },
//                                        shape = RoundedCornerShape(12.dp),
//                                        colors = FilterChipDefaults.filterChipColors(
//                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
//                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
//                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
//                                        ),
//                                        border = null
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//
//                // 🔹 Sticky Bottom Action Bar
//                Surface(
//                    modifier = Modifier.align(Alignment.BottomCenter),
//                    tonalElevation = 8.dp,
//                    shadowElevation = 16.dp,
//                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
//                ) {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 20.dp, vertical = 16.dp),
//                        horizontalArrangement = Arrangement.spacedBy(12.dp)
//                    ) {
//                        if (product.supportsTryOn) {
//                            OutlinedButton(
//                                onClick = { /* Handle Try On */ },
//                                modifier = Modifier.weight(1f).height(56.dp),
//                                shape = RoundedCornerShape(16.dp),
//                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
//                            ) {
//                                Icon(Icons.Default.Camera, contentDescription = null)
//                                Spacer(Modifier.width(8.dp))
//                                Text("Try On", fontWeight = FontWeight.Bold)
//                            }
//                        }
//
//                        Button(
//                            onClick = { /* Handle Add to Cart */ },
//                            modifier = Modifier.weight(1.5f).height(56.dp),
//                            shape = RoundedCornerShape(16.dp),
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = MaterialTheme.colorScheme.primary
//                            )
//                        ) {
//                            Text("Add to Cart", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
//                        }
//                    }
//                }
//            }
//        }
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
                model = "$baseUrl/${images[page]}",
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