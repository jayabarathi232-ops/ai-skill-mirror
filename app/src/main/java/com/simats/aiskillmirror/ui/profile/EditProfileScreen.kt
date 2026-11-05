package com.simats.aiskillmirror.ui.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.aiskillmirror.data.network.UserSession
import com.simats.aiskillmirror.ui.recovery.BackArrowIcon
import com.simats.aiskillmirror.ui.register.CustomOutlinedInputField
import com.simats.aiskillmirror.ui.theme.*

@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit = {},
    onProfileSaved: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var name by remember(uiState.userName) { mutableStateOf(uiState.userName) }
    var phone by remember(uiState.phone) { mutableStateOf(uiState.phone) }
    var education by remember(uiState.education) { mutableStateOf(uiState.education) }
    var skills by remember(uiState.skills) { mutableStateOf(uiState.skills) }
    var careerGoal by remember(uiState.careerGoal) { mutableStateOf(uiState.careerGoal) }
    var bio by remember(uiState.userBio) { mutableStateOf(uiState.userBio) }

    var selectedImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var selectedImageFileName by remember { mutableStateOf<String?>(null) }
    var isPhotoRemoved by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
                val ext = when {
                    mimeType.contains("png", ignoreCase = true) -> "png"
                    mimeType.contains("webp", ignoreCase = true) -> "webp"
                    else -> "jpg"
                }

                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes != null && bytes.isNotEmpty()) {
                    if (bytes.size > 5 * 1024 * 1024) {
                        Toast.makeText(context, "Image exceeds 5MB size limit", Toast.LENGTH_SHORT).show()
                    } else {
                        selectedImageBytes = bytes
                        selectedImageFileName = "profile_${UserSession.userId}_${System.currentTimeMillis()}.$ext"
                        isPhotoRemoved = false
                        Toast.makeText(context, "Photo selected! Save changes to apply.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Failed to read selected image", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load image: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is ProfileUiEvent.ShowToast) {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    BackArrowIcon(size = 20.dp, color = TextPrimary)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Personal information",
                    color = HeadingPurple,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Scrollable Form
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // Profile Picture Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ProfileAvatar(
                            imageUrl = if (isPhotoRemoved) "" else uiState.profilePictureUrl,
                            initials = uiState.initials,
                            size = 100.dp,
                            shape = CircleShape,
                            previewBytes = selectedImageBytes,
                            showEditBadge = true,
                            fontSize = 32,
                            onEditClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier.clickable { imagePickerLauncher.launch("image/*") }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Change photo",
                                color = AccentPurple,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { imagePickerLauncher.launch("image/*") }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )

                            val hasPhoto = (uiState.profilePictureUrl.isNotBlank() && !isPhotoRemoved) || selectedImageBytes != null
                            if (hasPhoto) {
                                Text(
                                    text = "Remove photo",
                                    color = Color(0xFFEF4444),
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            selectedImageBytes = null
                                            selectedImageFileName = null
                                            isPhotoRemoved = true
                                            viewModel.removePhoto(context)
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                if (!uiState.errorMessage.isNullOrBlank()) {
                    Text(
                        text = uiState.errorMessage ?: "",
                        color = Color(0xFFFF5252),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // Full Name
                Text(text = "Full name", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                CustomOutlinedInputField(value = name, onValueChange = { name = it }, placeholder = "Enter your full name")

                Spacer(modifier = Modifier.height(16.dp))

                // Phone
                Text(text = "Phone number", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                CustomOutlinedInputField(value = phone, onValueChange = { phone = it }, placeholder = "Enter phone number")

                Spacer(modifier = Modifier.height(16.dp))

                // Education
                Text(text = "Education", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                CustomOutlinedInputField(value = education, onValueChange = { education = it }, placeholder = "Highest qualification or degree")

                Spacer(modifier = Modifier.height(16.dp))

                // Core Skills
                Text(text = "Core skills", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                CustomOutlinedInputField(value = skills, onValueChange = { skills = it }, placeholder = "e.g. Public Speaking, Coding, Python")

                Spacer(modifier = Modifier.height(16.dp))

                // Career Goal
                Text(text = "Career goal", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                CustomOutlinedInputField(value = careerGoal, onValueChange = { careerGoal = it }, placeholder = "Target role or ambition")

                Spacer(modifier = Modifier.height(16.dp))

                // Bio
                Text(text = "Bio", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                CustomOutlinedInputField(value = bio, onValueChange = { bio = it }, placeholder = "Brief professional summary")

                Spacer(modifier = Modifier.height(28.dp))

                // Save Changes Button
                Button(
                    onClick = {
                        viewModel.saveProfile(
                            name = name,
                            phone = phone,
                            education = education,
                            skills = skills,
                            careerGoal = careerGoal,
                            bio = bio,
                            imageBytes = selectedImageBytes,
                            fileName = selectedImageFileName,
                            context = context,
                            onSuccess = {
                                onProfileSaved()
                            }
                        )
                    },
                    enabled = !uiState.isLoading && !uiState.isUploadingPhoto,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = PrimaryGradientStart),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(PrimaryGradientStart, PrimaryGradientEnd)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.isLoading || uiState.isUploadingPhoto) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text(
                                text = "Save changes",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}
