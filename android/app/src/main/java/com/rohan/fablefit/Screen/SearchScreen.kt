package com.rohan.fablefit.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.rohan.fablefit.ui.model.Product
import com.rohan.fablefit.ui.model.ProductCard
import com.rohan.fablefit.ui.model.SearchFilters
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    query: String,
    filters: SearchFilters?,
    onProductClick: ((Product) -> Unit)? = null
) {
    val productsFromBackend = remember(query,filters) { fetchProducts(query,filters) }
    Column(modifier = Modifier.fillMaxSize().clip(shape = RoundedCornerShape(20.dp))) {
        if (productsFromBackend.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No products found")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(
                    items = productsFromBackend,
                ) { index,product ->

                    ProductCard(
                        product = product,
                        onProductClick = { onProductClick?.invoke(product) }
                    )
                }
            }
        }
    }
}

fun fetchProducts(query: String, filters: SearchFilters?): List<Product> {
    val allProductsList=listOf(
        Product("1","Black T-Shirt","Classic black tee","men",499.0,listOf("S","M","L"),"black",20,"FableFit",listOf("https://via.placeholder.com/300"),"upper"),
        Product("2","White T-Shirt","Premium cotton tee","men",599.0,listOf("M","L","XL"),"white",15,"FableFit",listOf("https://via.placeholder.com/300"),"upper"),
        Product("3","Blue Jeans","Slim fit jeans","men",1499.0,listOf("30","32","34"),"blue",10,"DenimCo",listOf("https://via.placeholder.com/300"),"lower"),
        Product("4","Grey Hoodie","Warm hoodie","men",1999.0,listOf("M","L"),"grey",8,"UrbanWear",listOf("https://via.placeholder.com/300"),"upper"),
        Product("5","Red Dress","Elegant evening dress","women",2499.0,listOf("S","M"),"red",5,"StyleHub",listOf("https://via.placeholder.com/300"),"dress"),
        Product("6","Floral Dress","Summer floral dress","women",1899.0,listOf("S","M","L"),"pink",7,"StyleHub",listOf("https://via.placeholder.com/300"),"dress"),
        Product("7","Black Leggings","Stretch leggings","women",799.0,listOf("S","M","L"),"black",25,"ActiveFit",listOf("https://via.placeholder.com/300"),"lower"),
        Product("8","Running Shorts","Breathable shorts","men",699.0,listOf("M","L"),"blue",18,"ActiveFit",listOf("https://via.placeholder.com/300"),"lower"),
        Product("9","Leather Jacket","Premium leather jacket","men",4999.0,listOf("M","L"),"black",3,"UrbanWear",listOf("https://via.placeholder.com/300"),"upper"),
        Product("10","Denim Jacket","Classic denim jacket","women",2999.0,listOf("S","M"),"blue",6,"DenimCo",listOf("https://via.placeholder.com/300"),"upper"),

        Product("11","Yellow Top","Casual summer top","women",899.0,listOf("S","M","L"),"yellow",12,"StyleHub",listOf("https://via.placeholder.com/300"),"upper"),
        Product("12","Cargo Pants","Utility cargo pants","men",1799.0,listOf("30","32","34"),"green",9,"UrbanWear",listOf("https://via.placeholder.com/300"),"lower"),
        Product("13","Sports Jacket","Lightweight jacket","men",2299.0,listOf("M","L","XL"),"black",11,"ActiveFit",listOf("https://via.placeholder.com/300"),"upper"),
        Product("14","White Shirt","Formal white shirt","men",1299.0,listOf("M","L","XL"),"white",14,"FableFit",listOf("https://via.placeholder.com/300"),"upper"),
        Product("15","Pencil Skirt","Office wear skirt","women",1399.0,listOf("S","M"),"black",8,"StyleHub",listOf("https://via.placeholder.com/300"),"lower"),

        Product("16","Track Pants","Comfort track pants","men",999.0,listOf("M","L"),"grey",20,"ActiveFit",listOf("https://via.placeholder.com/300"),"lower"),
        Product("17","Crop Top","Stylish crop top","women",699.0,listOf("S","M","L"),"white",17,"StyleHub",listOf("https://via.placeholder.com/300"),"upper"),
        Product("18","Denim Shorts","Summer denim shorts","women",1199.0,listOf("S","M"),"blue",10,"DenimCo",listOf("https://via.placeholder.com/300"),"lower"),
        Product("19","Sweatshirt","Casual sweatshirt","men",1499.0,listOf("M","L","XL"),"grey",13,"UrbanWear",listOf("https://via.placeholder.com/300"),"upper"),
        Product("20","Party Dress","Evening party dress","women",2999.0,listOf("S","M"),"black",4,"StyleHub",listOf("https://via.placeholder.com/300"),"dress"),

        Product("21","Blue Shirt","Casual blue shirt","men",1099.0,listOf("M","L"),"blue",15,"FableFit",listOf("https://via.placeholder.com/300"),"upper"),
        Product("22","Yoga Pants","Stretch yoga pants","women",1299.0,listOf("S","M","L"),"black",22,"ActiveFit",listOf("https://via.placeholder.com/300"),"lower"),
        Product("23","Tank Top","Gym tank top","men",499.0,listOf("M","L"),"red",18,"ActiveFit",listOf("https://via.placeholder.com/300"),"upper"),
        Product("24","Blazer","Formal blazer","women",3499.0,listOf("S","M"),"navy",6,"StyleHub",listOf("https://via.placeholder.com/300"),"upper"),
        Product("25","Chinos","Slim chinos","men",1699.0,listOf("30","32","34"),"beige",9,"UrbanWear",listOf("https://via.placeholder.com/300"),"lower"),

        Product("26","Maxi Dress","Long maxi dress","women",2599.0,listOf("S","M","L"),"purple",7,"StyleHub",listOf("https://via.placeholder.com/300"),"dress"),
        Product("27","Polo T-Shirt","Classic polo","men",799.0,listOf("M","L","XL"),"green",19,"FableFit",listOf("https://via.placeholder.com/300"),"upper"),
        Product("28","Jeggings","Comfort jeggings","women",1399.0,listOf("S","M"),"blue",16,"DenimCo",listOf("https://via.placeholder.com/300"),"lower"),
        Product("29","Windbreaker","Outdoor windbreaker","men",2199.0,listOf("M","L"),"orange",5,"UrbanWear",listOf("https://via.placeholder.com/300"),"upper"),
        Product("30","Pleated Skirt","Stylish pleated skirt","women",1499.0,listOf("S","M"),"pink",10,"StyleHub",listOf("https://via.placeholder.com/300"),"lower"),

        Product("31","Graphic Tee","Printed tee","men",599.0,listOf("S","M","L"),"white",30,"FableFit",listOf("https://via.placeholder.com/300"),"upper"),
        Product("32","Formal Pants","Office pants","men",1899.0,listOf("30","32","34"),"black",12,"UrbanWear",listOf("https://via.placeholder.com/300"),"lower"),
        Product("33","Cardigan","Soft cardigan","women",1999.0,listOf("S","M","L"),"grey",8,"StyleHub",listOf("https://via.placeholder.com/300"),"upper"),
        Product("34","Athletic Shorts","Workout shorts","men",899.0,listOf("M","L"),"black",21,"ActiveFit",listOf("https://via.placeholder.com/300"),"lower"),
        Product("35","Summer Dress","Light summer dress","women",1799.0,listOf("S","M"),"yellow",9,"StyleHub",listOf("https://via.placeholder.com/300"),"dress"),

        Product("36","Flannel Shirt","Check flannel shirt","men",1299.0,listOf("M","L","XL"),"red",14,"UrbanWear",listOf("https://via.placeholder.com/300"),"upper"),
        Product("37","Bodycon Dress","Stylish bodycon","women",2699.0,listOf("S","M"),"black",6,"StyleHub",listOf("https://via.placeholder.com/300"),"dress"),
        Product("38","Slim Jeans","Skinny jeans","women",1599.0,listOf("28","30","32"),"blue",11,"DenimCo",listOf("https://via.placeholder.com/300"),"lower"),
        Product("39","Oversized Hoodie","Trendy hoodie","men",2099.0,listOf("M","L","XL"),"brown",7,"UrbanWear",listOf("https://via.placeholder.com/300"),"upper"),
        Product("40","Active Top","Gym active top","women",899.0,listOf("S","M","L"),"black",20,"ActiveFit",listOf("https://via.placeholder.com/300"),"upper"),

        Product("41","Linen Shirt","Breathable linen shirt","men",1399.0,listOf("M","L"),"white",13,"FableFit",listOf("https://via.placeholder.com/300"),"upper"),
        Product("42","Sweatpants","Cozy sweatpants","women",1199.0,listOf("S","M"),"grey",17,"ActiveFit",listOf("https://via.placeholder.com/300"),"lower"),
        Product("43","Wrap Dress","Elegant wrap dress","women",2399.0,listOf("S","M"),"green",5,"StyleHub",listOf("https://via.placeholder.com/300"),"dress"),
        Product("44","Ripped Jeans","Distressed jeans","men",1799.0,listOf("30","32","34"),"blue",10,"DenimCo",listOf("https://via.placeholder.com/300"),"lower"),
        Product("45","Bomber Jacket","Classic bomber","men",2999.0,listOf("M","L"),"black",6,"UrbanWear",listOf("https://via.placeholder.com/300"),"upper"),

        Product("46","Midi Dress","Elegant midi dress","women",2199.0,listOf("S","M","L"),"maroon",8,"StyleHub",listOf("https://via.placeholder.com/300"),"dress"),
        Product("47","Sleeveless Top","Summer sleeveless","women",699.0,listOf("S","M","L"),"blue",19,"StyleHub",listOf("https://via.placeholder.com/300"),"upper"),
        Product("48","Joggers","Casual joggers","men",1299.0,listOf("M","L","XL"),"grey",16,"ActiveFit",listOf("https://via.placeholder.com/300"),"lower"),
        Product("49","Denim Skirt","Trendy denim skirt","women",1499.0,listOf("S","M"),"blue",9,"DenimCo",listOf("https://via.placeholder.com/300"),"lower"),
        Product("50","Classic Suit","Formal suit set","men",5999.0,listOf("M","L","XL"),"navy",4,"UrbanWear",listOf("https://via.placeholder.com/300"),"upper")
    );
    // 1. Define what "Default/Sample" products look like
    // You could take the first 6 products or products from a specific brand
    val sampleInitialProducts = allProductsList.take(6)

    // 2. Check if the user has provided any input or filter
    val isInitialState = query.isBlank() &&
            filters?.category == null &&
            filters?.vton_category == null

    // 3. If they just opened the screen, show the samples
    if (isInitialState) {
        return sampleInitialProducts
    }

    // 4. Otherwise, perform the actual filtering logic
    return allProductsList.filter { product ->
        val matchesQuery = query.isBlank() ||
                product.name.contains(query.trim(), ignoreCase = true)

        val matchesCategory = filters?.category == null ||
                product.category.equals(filters.category, ignoreCase = true)

        val matchesVton = filters?.vton_category == null ||
                product.vton_category?.equals(filters.vton_category, ignoreCase = true) == true

        matchesQuery && matchesCategory && matchesVton
    }
}