package com.rohan.fablefit.agent

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import coil3.compose.AsyncImage
import com.rohan.fablefit.AgentBubble
import com.rohan.fablefit.UserBubble
import com.rohan.fablefit.UserOptionBubble
import com.rohan.fablefit.ui.Product.ProductViewModel

sealed class AgentRoutes(
    val title: String,
    val route: String,
    val ui: @Composable () -> Unit
) {

    object ImageSearch : AgentRoutes(
        title = "Image Search",
        route = "image_search",
        ui = { ImageSearchScreen() }
    )
    object ChatBot: AgentRoutes(
        "Chat",
        "chat",
        ui={},
    )
}
@Composable
fun ImageSearchScreen() {

    val productViewModel: ProductViewModel = viewModel()

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            imageUri = it
            // Later call your API here
            // productViewModel.searchImage(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        AgentBubble("Please upload the photo here")

        if (imageUri == null) {

            UserBubble(
                custom = {
                    IconButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null)
                    }
                }
            )

        } else {

            UserBubble(
                custom = {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
            )

            AgentBubble("Searching products...")

        }
    }
}