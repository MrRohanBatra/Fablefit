package com.rohan.fablefit.Screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UserInfo() {

    val user = remember { FirebaseAuth.getInstance().currentUser }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

//        // ---- Avatar Card ----
//        Surface(
//            shape = RoundedCornerShape(24.dp),
//            tonalElevation = 4.dp,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                modifier = Modifier.padding(24.dp)
//            ) {
//
//                SubcomposeAsyncImage(
//                    model = user?.photoUrl,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(100.dp)
//                        .clip(CircleShape),
//                    contentScale = ContentScale.Crop,
//                    loading = {
//                        CircularWavyProgressIndicator()
//                    }
//                )
//
//                Spacer(Modifier.height(12.dp))
//
//                Text(
//                    user?.displayName ?: "FableFit User",
//                    style = MaterialTheme.typography.titleLarge,
//                    fontWeight = FontWeight.Bold
//                )
//
//                Text(
//                    user?.email ?: "",
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            }
//        }

        Spacer(Modifier.height(24.dp))

        Text(
            "Personal Information",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {

                InfoRow("Name", user?.displayName ?: "Not set")
                HorizontalDivider()

                InfoRow("Email", user?.email ?: "Not set")
                HorizontalDivider()

                InfoRow("Phone", user?.phoneNumber ?: "Not linked")
                HorizontalDivider()

                InfoRow(
                    "Verified",
                    if (user?.isEmailVerified == true) "Yes" else "No"
                )
            }
        }
    }
}
@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }

        androidx.compose.material3.Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}