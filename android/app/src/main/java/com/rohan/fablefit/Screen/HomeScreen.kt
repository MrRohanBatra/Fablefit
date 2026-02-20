package com.rohan.fablefit.Screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.dropShadow
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
import com.rohan.fablefit.ui.model.CategorySectionModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(){
    val context=LocalContext.current;
    val banners = listOf(

        BannerUiModel(
            id = "ai_style",
            imageUrl ="https://images.unsplash.com/photo-1490481651871-ab68de25d43d?auto=format&fit=crop&w=1200&q=80" ,
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
        HomeCarousel(
            items=banners,
            onBannerClick = {i->
                Toast.makeText(context,i.title, Toast.LENGTH_SHORT).show()
            }
        )
        Spacer(modifier= Modifier.height(16.dp))
//        CategorySection()
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeCarousel(
    items: List<BannerUiModel>,
    onBannerClick: (BannerUiModel) -> Unit,
) {
    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState { items.count() },
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp), // Set fixed height for the carousel container
        preferredItemWidth = 186.dp,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) { i ->
        val item = items[i]
        Box(
            modifier = Modifier
                .height(200.dp)
                .maskClip(MaterialTheme.shapes.extraLarge)
                .background(Color.LightGray) // Fallback background
                .clickable { onBannerClick(item) }
        ) {
            if (item.imageUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.title,
                    placeholder = painterResource(R.drawable.placeholder),
                    error = painterResource(R.drawable.error_image),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else if (item.imageRes != null) {
                Image(
                    painter = painterResource(item.imageRes),
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Text Overlay with simple gradient for readability
            item.title?.let {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)),
                                startY = 300f
                            )
                        )

                )
                Text(
                    text = it,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun CategorySection(
    items:List<CategorySectionModel>,
    onBannerClick: (CategorySectionModel) -> Unit
){
}