package com.rohan.fablefit.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.rohan.fablefit.navigation.BottomRoute
import com.rohan.fablefit.ui.Profile.ProfileUiState
import com.rohan.fablefit.ui.Profile.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    onLogout: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val firebaseUser = remember { FirebaseAuth.getInstance().currentUser }

    LaunchedEffect(firebaseUser?.uid) {
        firebaseUser?.uid?.let { profileViewModel.loadUser(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Spacer(Modifier.height(12.dp))

        // ── Tier Card ──────────────────────────────────────────────────────────
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            when (val state = profileViewModel.uiState) {
                is ProfileUiState.Loading ->
                    TierCardSkeleton()

                is ProfileUiState.Success ->
                    TierCard(
                        tier        = state.user.tier,
                        totalSpent  = state.user.totalSpent
                    )

                is ProfileUiState.Error ->
                    // Non-critical — just show a minimal card
                    TierCard(tier = "Bronze", totalSpent = 0f)
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Account Section ────────────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
            Text(
                "Account",
                style    = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ProfileCard {
                ProfileMenuItem(Icons.Default.Person, "My Account") {
                    navController.navigate(BottomRoute.MyInfo.route)
                }
                ProfileMenuItem(Icons.Default.ShoppingBag, "My Orders") {}
                ProfileMenuItem(Icons.Default.Settings, "App Settings") {}
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "Danger Zone",
                style    = MaterialTheme.typography.labelLarge,
                color    = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ProfileCard {
                ProfileMenuItem(
                    icon          = Icons.AutoMirrored.Filled.ExitToApp,
                    title         = "Logout",
                    isDestructive = true
                ) { onLogout() }
            }
        }
    }
}

// ── Tier Card ──────────────────────────────────────────────────────────────────

@Composable
fun TierCard(tier: String, totalSpent: Float) {

    val tierColor = when (tier) {
        "Gold"   -> Color(0xFFFFD700)
        "Silver" -> Color(0xFFB0BEC5)
        else     -> Color(0xFFCD7F32)
    }

    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(tierColor.copy(alpha = 0.85f), tierColor.copy(alpha = 0.35f))
    )

    // Progress toward next tier
    val (progress, nextTierName, amountLeft) = when (tier) {
        "Gold"   -> Triple(1f, "Gold", 0f)
        "Silver" -> Triple(
            ((totalSpent - 1000f) / 4000f).coerceIn(0f, 1f),
            "Gold",
            (5000f - totalSpent).coerceAtLeast(0f)
        )
        else     -> Triple(
            (totalSpent / 1000f).coerceIn(0f, 1f),
            "Silver",
            (1000f - totalSpent).coerceAtLeast(0f)
        )
    }

    val discountText = when (tier) {
        "Gold"   -> "15% discount applied to your cart"
        "Silver" -> "5% discount applied to your cart"
        else     -> "Spend ₹${amountLeft.toInt()} more to unlock Silver (5% off)"
    }

    Surface(
        shape  = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradientBrush)
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                // Tier title row
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Default.Star,
                        contentDescription = null,
                        tint               = Color.White,
                        modifier           = Modifier.size(22.dp)
                    )
                    Text(
                        text       = "$tier Member",
                        style      = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color      = Color.White
                    )
                }

                // Total spent
                Text(
                    text  = "Total Spent: ₹${String.format("%.2f", totalSpent)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )

                // Progress bar
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    LinearProgressIndicator(
                        progress           = { progress },
                        modifier           = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color              = Color.White,
                        trackColor         = Color.White.copy(alpha = 0.3f),
                    )
                    if (tier == "Gold") {
                        Text(
                            text  = "🎉 You're at the highest tier!",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    } else {
                        Text(
                            text  = "₹${amountLeft.toInt()} more to reach $nextTierName",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                // Discount info chip
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.25f)
                ) {
                    Text(
                        text     = discountText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style    = MaterialTheme.typography.labelMedium,
                        color    = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun TierCardSkeleton() {
    Surface(
        shape         = RoundedCornerShape(20.dp),
        tonalElevation = 2.dp,
        modifier      = Modifier.fillMaxWidth().height(140.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.size(28.dp))
        }
    }
}

// ── Reusable card / menu components ────────────────────────────────────────────

@Composable
fun ProfileCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape         = RoundedCornerShape(20.dp),
        tonalElevation = 4.dp,
        modifier      = Modifier.fillMaxWidth()
    ) {
        Column(content = content)
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape  = CircleShape,
            color  = if (isDestructive) MaterialTheme.colorScheme.errorContainer
                     else MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(42.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon, null,
                    tint = if (isDestructive) MaterialTheme.colorScheme.error
                           else MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(Modifier.width(16.dp))

        Text(
            title,
            modifier   = Modifier.weight(1f),
            style      = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight, null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
