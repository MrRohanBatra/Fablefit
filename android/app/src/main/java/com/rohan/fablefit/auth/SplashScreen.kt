package com.rohan.fablefit.auth


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rohan.fablefit.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SplashScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Matching the logo style from AuthScreen
            Image(
                painter = painterResource(R.drawable.icon),
                contentDescription = "Fablefit Logo",
                modifier = Modifier
                    .size(120.dp) // Slightly larger for splash
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "FABLEFIT",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 6.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Style meets AI",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.outline
            )

            // Spacing before the indicator
            Spacer(modifier = Modifier.height(64.dp))

            // Using the same M3 LoadingIndicator as seen in your AuthScreen
            LoadingIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}