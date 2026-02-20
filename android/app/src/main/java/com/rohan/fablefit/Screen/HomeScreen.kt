package com.rohan.fablefit.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rohan.fablefit.R
import com.rohan.fablefit.ui.model.BannerUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(){
    val banners = listOf(

        BannerUiModel(
            id = "ai_style",
            imageUrl = "https://images.unsplash.com/photo-1520975922323-6a6a6f3f4f36?auto=format&fit=crop&w=1200&q=80",
            title = "AI Style Assistant"
        ),

        BannerUiModel(
            id = "new_arrivals",
            imageUrl = "https://images.unsplash.com/photo-1490481651871-ab68de25d43d?auto=format&fit=crop&w=1200&q=80",
            title = "New Arrivals"
        ),

        BannerUiModel(
            id = "winter_drop",
            imageUrl = "https://images.unsplash.com/photo-1519741497674-611481863552?auto=format&fit=crop&w=1200&q=80",
            title = "Winter Collection"
        ),

        BannerUiModel(
            id = "sale_40",
            imageUrl = "https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?auto=format&fit=crop&w=1200&q=80",
            title = "Flat 40% OFF"
        ),

        BannerUiModel(
            id = "trending_now",
            imageUrl = "https://images.unsplash.com/photo-1521334884684-d80222895322?auto=format&fit=crop&w=1200&q=80",
            title = "Trending Now"
        )
    )
    Column() {
        Row() {
            HomeCarousel(
                items=banners,
                onBannerClick = {}
            )
        }
    }


}



@Composable
fun HomeCarousel(
    items: List<BannerUiModel>,
    onBannerClick: (BannerUiModel) -> Unit,
) {
    val shape = RoundedCornerShape(24.dp) // Define shape once for consistency

    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState { items.count() },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 16.dp)
            .height(240.dp),
        preferredItemWidth = 186.dp,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) { i ->
        val item = items[i]

        ElevatedCard(
            // Fix 1: Ensure the card itself clips its children
            modifier = Modifier.clip(shape),
            shape = shape,
            onClick = { onBannerClick(item) }
        ) {
            Box(
                // Fix 2: Explicitly clip the Box to ensure the image follows the curve
                modifier = Modifier.fillMaxSize().clip(shape)
            ) {
                when {
                    item.imageRes != null -> {
                        Image(
                            painter = painterResource(item.imageRes),
                            contentDescription = item.title,
                            modifier = Modifier
                                .height(200.dp)
                                .fillMaxWidth(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    item.imageUrl != null -> {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(item.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = item.title,
                            placeholder = painterResource(R.drawable.placeholder),
                            error = painterResource(R.drawable.error_image),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                item.title?.let {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                                    startY = 300f // Adjust to start gradient near the bottom
                                )
                            )
                    )
                    Text(
                        text = it,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}