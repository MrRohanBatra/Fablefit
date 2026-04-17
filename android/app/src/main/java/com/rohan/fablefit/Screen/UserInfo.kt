package com.rohan.fablefit.Screen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import com.google.firebase.auth.userProfileChangeRequest
import com.rohan.fablefit.ui.User.UserViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UserInfo(context: Context,userViewModel: UserViewModel) {
    val user by userViewModel.user
    val userData by userViewModel.userData
    LaunchedEffect(Unit) {
        userViewModel.getUserData()
    }
    var activeField by remember { mutableStateOf<EditField?>(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .clip(RoundedCornerShape(20.dp))

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
                            userViewModel.refreshUser()
                            Toast.makeText(context,"Name Updated", Toast.LENGTH_SHORT).show();
                        }
                    })
                })
                HorizontalDivider()

                InfoRow("Email", user?.email ?: "Not set")
                HorizontalDivider()

//                InfoRow("Phone", user?.phoneNumber ?: "Not linked")
//                HorizontalDivider()

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
        Spacer(Modifier.height(24.dp))
        Text(
            "Delivery Information",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ){
            Column(

            ) {
                InfoRow("Phone Number",userData?.phone?:"",)
                HorizontalDivider()
                InfoRow("Address",userData?.address?.joinToString("\n")?:"")
            }
        }
        Spacer(Modifier.height(24.dp))
        Text(
            "Virtual Try On Profile",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ){
            Column(

            ) {
                InfoRow("Virtual Try On Image",if(userData?.vtonImage!=null){"Uploaded"}else{"Upload Image"}, onClick = {
                    activeField= EditField.ProfileImage
                })
                HorizontalDivider()

            }
        }
        activeField?.let { field ->
            ModalBottomSheet(onDismissRequest = { activeField = null }) {
                when (field) {
                    is EditField.Text -> TextEditSheetContent(field) { activeField = null }
                    is EditField.ProfileImage ->
                        ImageUploadSheetContent(
                            onDismiss = { activeField = null },
                            onImageSelected = { uri ->
                                userViewModel.uploadImage(context,uri)
                            }
                        )
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
fun ImageUploadSheetContent(
    onDismiss: () -> Unit,
    onImageSelected: (Uri) -> Unit = {}
) {

    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            selectedImageUri = uri
        }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            "Upload Photo",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(8.dp))

        // Image Preview
        if (selectedImageUri != null) {

            AsyncImage(
                model = selectedImageUri,
                contentDescription = "Selected Image",
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

        } else {

            Surface(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(16.dp)),
                tonalElevation = 2.dp
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Image Selected")
                }
            }

        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Button(
                onClick = {
                    launcher.launch("image/*")
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Select Image")
            }

            Button(
                enabled = selectedImageUri != null,
                onClick = {
                    selectedImageUri?.let {
                        onImageSelected(it)
                        Toast
                            .makeText(context, "Image Selected", Toast.LENGTH_SHORT)
                            .show()
                        onDismiss()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Upload")
            }

        }

    }
}

sealed class EditField {
    data class Text(val title: String, val initialValue: String, val onSave: (String) -> Unit) : EditField()
    object ProfileImage : EditField() // Future-proofing for your image upload
}