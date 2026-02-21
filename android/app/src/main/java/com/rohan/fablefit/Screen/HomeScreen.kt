package com.rohan.fablefit.Screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import com.rohan.fablefit.ui.model.BannerUiModel
import com.rohan.fablefit.ui.model.CategorySectionModel
import com.rohan.fablefit.ui.model.Product
import com.rohan.fablefit.ui.model.ProductCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(){
    val context=LocalContext.current;
    val scrollState= rememberScrollState();
    val banners = remember {  listOf(

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
    )}
    val categories = remember {
        listOf(
            CategorySectionModel(
                imagePath = "https://picsum.photos/seed/sale/300/300",
                title = "Sale",
                onClick = { /* Navigate to Sale */ }
            ),
            CategorySectionModel(
                imagePath = "https://picsum.photos/seed/trending/300/300",
                title = "Trending",
                onClick = { /* Navigate to Trending */ }
            ),
            CategorySectionModel(
                imagePath = "https://picsum.photos/seed/men/300/300",
                title = "Men",
                onClick = { /* Navigate to Men */ }
            ),
            CategorySectionModel(
                imagePath = "https://picsum.photos/seed/women/300/300",
                title = "Women",
                onClick = { /* Navigate to Women */ }
            ),
            CategorySectionModel(
                imagePath = "https://picsum.photos/seed/uppers/300/300",
                title = "Uppers",
                onClick = { /* Navigate to Tops/Jackets */ }
            ),
            CategorySectionModel(
                imagePath = "https://picsum.photos/seed/lowers/300/300",
                title = "Lowers",
                onClick = { /* Navigate to Pants/Joggers */ }
            ),
            CategorySectionModel(
                imagePath = "https://picsum.photos/seed/dresses/300/300",
                title = "Dresses",
                onClick = { /* Navigate to Full Dresses */ }
            )
        )
    }
    val trendingProducts = remember { listOf(
        Product(
            name = "Oversized Graphic Tee",
            description = "Heavyweight cotton tee with street art print.",
            category = "Uppers",
            price = 1499.0,
            companyName = "Fablefit Origin",
            images = listOf("https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?q=80&w=400"),
            vton_category = "top"
        ),
        Product(
            name = "Urban Cargo Pants",
            description = "Multi-pocket techwear cargos.",
            category = "Lowers",
            price = 2499.0,
            companyName = "TechStyle",
            images = listOf("https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?q=80&w=400")
        ),
        Product(
            name = "Minimalist Hoodie",
            description = "Clean aesthetic hoodie for daily wear.",
            category = "Uppers",
            price = 1999.0,
            companyName = "Fablefit Origin",
            images = listOf("https://images.unsplash.com/photo-1556821840-3a63f95609a7?q=80&w=400"),
            vton_category = "top"
        )
    )}
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
    ) {
        HomeCarousel(
            items=banners,
            onBannerClick = {i->
                Toast.makeText(context,i.title, Toast.LENGTH_SHORT).show()
            }
        )
        Spacer(modifier= Modifier.height(16.dp))

        CategorySection(categories)
        TrendingSection(products = trendingProducts)
    }


}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
)
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
                    model = item.imageUrl,
                    contentDescription = item.title,
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
    categories: List<CategorySectionModel>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Shop by Category",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categories) { category ->
                CategoryItem(category)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CategoryItem(category: CategorySectionModel) {
    val isSale = category.title == "Sale"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .clickable { category.onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .then(
                    if (isSale) Modifier.border(2.dp, MaterialTheme.colorScheme.errorContainer, CircleShape)
                    else Modifier
                )
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            SubcomposeAsyncImage(
                model = category.imagePath,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                loading = {

                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        LoadingIndicator()
                    }
                },
                error = {
                    // If it shows RED, the URL or Network is the problem
                    Box(Modifier.fillMaxSize().background(Color.Red.copy(alpha = 0.3f)))
                }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = category.title ?: "",
            style = MaterialTheme.typography.labelMedium,
            color = if (isSale) Color.Red else Color.Unspecified,
            fontWeight = if (isSale) FontWeight.Bold else FontWeight.Medium
        )
    }
}



@Composable
fun TrendingSection(products: List<Product>) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
        SectionHeader(title = "Trending Now")

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    modifier = Modifier.width(160.dp),
                    onProductClick = { /* Handle Navigation */ }
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(text = "See All", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
    }
}