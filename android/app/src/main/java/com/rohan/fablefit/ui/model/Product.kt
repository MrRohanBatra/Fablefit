package com.rohan.fablefit.ui.model

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import com.rohan.fablefit.ui.theme.FablefitTheme

import java.util.Date

data class Product(
    val name: String,
    val description: String,
    val category: String,
    val price: Double,
    val sizes: List<String> = emptyList(),
    val color: String = "unknown",
    val stock: Int = 0,
    val companyName: String,
    val images: List<String> = emptyList(),
    val vton_category: String? = null,
    // Note: 'embedding' is excluded as per your requirement
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) {
    /**
     * Returns the first image URL or a placeholder if the list is empty.
     */
    val thumbnail: String
        get() = images.firstOrNull() ?: "https://via.placeholder.com/150"

    /**
     * Formats the price for display (e.g., ₹499.00)
     */
    val formattedPrice: String
        @SuppressLint("DefaultLocale")
        get() = "₹${String.format("%.2f", price)}"
}
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier,
    onProductClick: (Product) -> Unit
) {
    ElevatedCard(
        shape = MaterialTheme.shapes.medium, // Uses 12.dp by default in M3
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onProductClick(product) }
    ) {
        Column {
            Box(modifier = Modifier.height(200.dp).fillMaxWidth()) {
                SubcomposeAsyncImage(
                    model = product.images.firstOrNull(),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
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

                if (product.vton_category != null) {
                    Text(
                        text = "TRY ON",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .background(
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(12.dp)
            ) {
                Text(
                    text = product.companyName.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.formattedPrice,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}