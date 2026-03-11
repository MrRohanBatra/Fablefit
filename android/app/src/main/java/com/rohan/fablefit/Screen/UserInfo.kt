package com.rohan.fablefit.Screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.auth.userProfileChangeRequest

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UserInfo(context: Context,onRefresh:()-> Unit) {
    var refreshTrigger by remember { mutableIntStateOf(0) }
    LaunchedEffect(refreshTrigger) {
        onRefresh()
    }
    val user = remember(refreshTrigger) { FirebaseAuth.getInstance().currentUser }
    var activeField by remember { mutableStateOf<EditField?>(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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

                InfoRow("Name", user?.displayName ?: "Not set", onClick = {
                    activeField= EditField.Text("Name",user?.displayName?:"", onSave = {
                        val profileUpdate= userProfileChangeRequest {
                            displayName=it
                        }
                        user?.updateProfile(profileUpdate)?.addOnSuccessListener {
                            Toast.makeText(context,"Name Updated", Toast.LENGTH_SHORT).show();
                            refreshTrigger++
                        }
                    })
                })
                HorizontalDivider()

                InfoRow("Email", user?.email ?: "Not set")
                HorizontalDivider()

                InfoRow("Phone", user?.phoneNumber ?: "Not linked")
                HorizontalDivider()

                InfoRow(
                    "Verified",
                    if (user?.isEmailVerified == true) "Yes" else "No"
                )
                HorizontalDivider()
                InfoRow(
                    label = "Uid",
                    value = user?.uid ?:"Not uid found",
                )
            }
        }
        activeField?.let { field ->
            ModalBottomSheet(onDismissRequest = { activeField = null }) {
                when (field) {
                    is EditField.Text -> TextEditSheetContent(field) { activeField = null }
                    is EditField.ProfileImage -> ImageUploadSheetContent { activeField = null }
                }
            }
        }
    }

}
@Composable
fun InfoRow(label: String, value: String,onClick:(()->Unit)?=null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            )
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

        if(onClick!=null){
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
@Composable
fun TextEditSheetContent(
    field: EditField.Text,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(field.initialValue) }

    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Update ${field.title}", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(field.title) }
        )
        Button(
            onClick = {
                field.onSave(text)
                onDismiss()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}

@Composable
fun ImageUploadSheetContent(onDismiss: () -> Unit) {
    // This is where you'll put your Coil image preview
    // and your "Select from Gallery" buttons later.
    Column(Modifier.padding(24.dp)) {
        Text("Upload Photo", style = MaterialTheme.typography.titleLarge)

    }
}


sealed class EditField {
    data class Text(val title: String, val initialValue: String, val onSave: (String) -> Unit) : EditField()
    object ProfileImage : EditField() // Future-proofing for your image upload
}