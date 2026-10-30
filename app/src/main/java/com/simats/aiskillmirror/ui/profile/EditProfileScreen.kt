package com.simats.aiskillmirror.ui.profile

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.aiskillmirror.ui.recovery.BackArrowIcon
import com.simats.aiskillmirror.ui.register.CustomOutlinedInputField
import com.simats.aiskillmirror.ui.theme.*

@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit = {},
    onProfileSaved: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var name by remember(uiState.userName) { mutableStateOf(uiState.userName) }
    var email by remember(uiState.email) { mutableStateOf(uiState.email) }
    var phone by remember(uiState.phone) { mutableStateOf(uiState.phone) }
    var education by remember(uiState.education) { mutableStateOf(uiState.education) }
    var skills by remember(uiState.skills) { mutableStateOf(uiState.skills) }
    var careerGoal by remember(uiState.careerGoal) { mutableStateOf(uiState.careerGoal) }
    var bio by remember(uiState.userBio) { mutableStateOf(uiState.userBio) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var selectedImageFileName by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()
                selectedImageBytes = bytes
                selectedImageFileName = "avatar_${System.currentTimeMillis()}.jpg"
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to read image", Toast.LENGTH_SHORT).show()
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
                    color = TextPrimary,
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
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .shadow(16.dp, CircleShape, spotColor = PrimaryGradientStart)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(CenterCardGradientStart, CenterCardGradientEnd)
                                    )
                                )
                                .border(2.dp, PrimaryGradientStart, CircleShape)
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedImageBytes != null) {
                                val bitmap = remember(selectedImageBytes) {
                                    BitmapFactory.decodeByteArray(selectedImageBytes, 0, selectedImageBytes!!.size)
                                }
                                if (bitmap != null) {
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = "Profile Picture",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text(
                                        text = uiState.initials,
                                        color = Color.White,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Text(
                                    text = uiState.initials,
                                    color = Color.White,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Change profile picture",
                            color = TaglineText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { imagePickerLauncher.launch("image/*") }
                        )
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
                CustomOutlinedInputField(value = name, onValueChange = { name = it }, placeholder = "Full name")

                Spacer(modifier = Modifier.height(16.dp))

                // Email
                Text(text = "Email address", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                CustomOutlinedInputField(value = email, onValueChange = { email = it }, placeholder = "Email address", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))

                Spacer(modifier = Modifier.height(16.dp))

                // Phone
                Text(text = "Phone number", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                CustomOutlinedInputField(value = phone, onValueChange = { phone = it }, placeholder = "Phone number", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))

                Spacer(modifier = Modifier.height(16.dp))

                // Education
                Text(text = "Education", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                CustomOutlinedInputField(value = education, onValueChange = { education = it }, placeholder = "Degree, university or major")

                Spacer(modifier = Modifier.height(16.dp))

                // Skills
                Text(text = "Skills (comma separated)", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                CustomOutlinedInputField(value = skills, onValueChange = { skills = it }, placeholder = "e.g. Java, Kotlin, Communication")

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
                            onSuccess = {
                                onProfileSaved()
                            }
                        )
                    },
                    enabled = !uiState.isLoading,
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
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text(
                                text = "Save changes",
                                color = TextPrimary,
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

