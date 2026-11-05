package com.simats.aiskillmirror.ui.upload

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.simats.aiskillmirror.network.PerformanceUploadItemDto
import com.simats.aiskillmirror.ui.dashboard.DashboardTab
import com.simats.aiskillmirror.ui.recovery.BackArrowIcon
import com.simats.aiskillmirror.ui.splash.SparkleIcon
import com.simats.aiskillmirror.ui.theme.*

@Composable
fun PerformanceUploadScreen(
    viewModel: PerformanceUploadViewModel,
    onBackClick: () -> Unit = {},
    onTabSelected: (DashboardTab) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Listen to UI Events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is PerformanceUploadUiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is PerformanceUploadUiEvent.UploadSuccess -> {
                    // Handled in state
                }
            }
        }
    }

    // 1. Photo Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onMediaSelected(context, it) }
    }

    // 2. Camera Photo Capture Launcher
    val cameraPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let { viewModel.onPhotoCaptured(context, it) }
    }

    // 3. Video Picker Launcher
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onMediaSelected(context, it) }
    }

    // 4. Audio Picker Launcher
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onMediaSelected(context, it) }
    }

    // Decoded Bitmap for Image Preview
    val previewBitmap = remember(uiState.selectedUri, uiState.capturedBitmap) {
        if (uiState.capturedBitmap != null) {
            uiState.capturedBitmap
        } else if (uiState.selectedTab == MediaTypeTab.PHOTO && uiState.selectedUri != null) {
            try {
                context.contentResolver.openInputStream(uiState.selectedUri!!)?.use { input ->
                    BitmapFactory.decodeStream(input)
                }
            } catch (_: Exception) {
                null
            }
        } else {
            null
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
            // ==========================================
            // TOP BAR
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(2.dp, RoundedCornerShape(12.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardBackground)
                            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                            .clickable { onBackClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        BackArrowIcon(size = 18.dp, color = TextPrimary)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Upload Performance",
                            color = HeadingPurple,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Multi-Modal AI Evaluation",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Previous Uploads Button
                Box(
                    modifier = Modifier
                        .shadow(2.dp, RoundedCornerShape(12.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                        .clip(RoundedCornerShape(12.dp))
                        .background(TaglineBackground)
                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        .clickable { viewModel.toggleHistorySheet(true) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🕒", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "History",
                            color = TaglineText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Scrollable Body
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                // ==========================================
                // 1. THREE MEDIA UPLOAD SELECTION CARDS
                // ==========================================
                Text(
                    text = "Select Media Type",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Photo Card
                    UploadTypeCard(
                        modifier = Modifier.weight(1f),
                        title = "Photo",
                        icon = "🖼️",
                        subtitle = "JPG, PNG, WEBP",
                        isSelected = uiState.selectedTab == MediaTypeTab.PHOTO,
                        accentColor = AccentPurple,
                        onClick = { viewModel.selectTab(MediaTypeTab.PHOTO) }
                    )

                    // Video Card
                    UploadTypeCard(
                        modifier = Modifier.weight(1f),
                        title = "Video",
                        icon = "📹",
                        subtitle = "MP4, MOV, WEBM",
                        isSelected = uiState.selectedTab == MediaTypeTab.VIDEO,
                        accentColor = CyanNode,
                        onClick = { viewModel.selectTab(MediaTypeTab.VIDEO) }
                    )

                    // Audio Card
                    UploadTypeCard(
                        modifier = Modifier.weight(1f),
                        title = "Audio",
                        icon = "🎙️",
                        subtitle = "MP3, WAV, M4A",
                        isSelected = uiState.selectedTab == MediaTypeTab.AUDIO,
                        accentColor = GreenNode,
                        onClick = { viewModel.selectTab(MediaTypeTab.AUDIO) }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ==========================================
                // 2. SKILL CATEGORY SELECTOR
                // ==========================================
                Text(
                    text = "Skill Discipline",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val categories = listOf(
                        "photography" to "📷 Photography",
                        "sports" to "⚽ Sports",
                        "dance" to "💃 Dance",
                        "music" to "🎵 Music",
                        "coding" to "💻 Coding"
                    )
                    categories.forEach { (catKey, label) ->
                        val isSelected = uiState.skillCategory.equals(catKey, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .shadow(if (isSelected) 3.dp else 1.dp, RoundedCornerShape(10.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) TaglineBackground else CardBackground)
                                .border(
                                    1.dp,
                                    if (isSelected) AccentPurple else CardBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.setSkillCategory(catKey) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) AccentPurple else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ==========================================
                // 3. MEDIA UPLOAD & PREVIEW AREA
                // ==========================================
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(22.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                        .clip(RoundedCornerShape(22.dp))
                        .background(CardBackground)
                        .border(1.dp, CardBorder, RoundedCornerShape(22.dp))
                        .padding(18.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Title / Headline
                        val mediaLabel = when (uiState.selectedTab) {
                            MediaTypeTab.PHOTO -> "Photo Evaluation"
                            MediaTypeTab.VIDEO -> "Movement & Video Tracking"
                            MediaTypeTab.AUDIO -> "Audio & Pitch Decomposition"
                        }
                        Text(
                            text = mediaLabel,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (uiState.selectedTab) {
                                MediaTypeTab.PHOTO -> "AI Computer Vision analyzes framing, lighting & balance"
                                MediaTypeTab.VIDEO -> "MediaPipe pose estimation detects 33 anatomical joints"
                                MediaTypeTab.AUDIO -> "Acoustic AI measures pitch accuracy, cadence & timbre"
                            },
                            color = TextSecondary,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // ACTIVE PREVIEW OR SELECTION TRIGGER
                        if (uiState.selectedUri != null || uiState.capturedBitmap != null) {
                            // SHOW PREVIEW WITH METADATA & REMOVE/REPLACE CONTROLS
                            when (uiState.selectedTab) {
                                MediaTypeTab.PHOTO -> {
                                    PhotoPreviewSection(
                                        bitmap = previewBitmap,
                                        fileName = uiState.selectedFileName ?: "photo.jpg",
                                        fileSizeText = uiState.selectedFileSizeText ?: "",
                                        onReplace = { photoPickerLauncher.launch("image/*") },
                                        onRemove = { viewModel.clearSelectedMedia() }
                                    )
                                }
                                MediaTypeTab.VIDEO -> {
                                    VideoPreviewSection(
                                        uri = uiState.selectedUri!!,
                                        fileName = uiState.selectedFileName ?: "video.mp4",
                                        fileSizeText = uiState.selectedFileSizeText ?: "",
                                        durationText = uiState.selectedDurationText ?: "00:45",
                                        onReplace = { videoPickerLauncher.launch("video/*") },
                                        onRemove = { viewModel.clearSelectedMedia() }
                                    )
                                }
                                MediaTypeTab.AUDIO -> {
                                    AudioPreviewSection(
                                        fileName = uiState.selectedFileName ?: "audio.mp3",
                                        fileSizeText = uiState.selectedFileSizeText ?: "",
                                        durationText = uiState.selectedDurationText ?: "00:30",
                                        isPlaying = uiState.isPlayingAudio,
                                        onTogglePlay = { viewModel.toggleAudioPlayback(context) },
                                        onReplace = { audioPickerLauncher.launch("audio/*") },
                                        onRemove = { viewModel.clearSelectedMedia() }
                                    )
                                }
                            }
                        } else {
                            // SELECTION OPTIONS (GALLERY / CAMERA / RECORD)
                            when (uiState.selectedTab) {
                                MediaTypeTab.PHOTO -> {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        SelectMediaButton(
                                            icon = "🖼️",
                                            title = "Select Image from Gallery",
                                            subtitle = "JPG, JPEG, PNG, WEBP · Max 15MB",
                                            onClick = { photoPickerLauncher.launch("image/*") }
                                        )
                                        SelectMediaButton(
                                            icon = "📷",
                                            title = "Take Photo with Camera",
                                            subtitle = "Capture posture, art or framing live",
                                            onClick = { cameraPhotoLauncher.launch(null) }
                                        )
                                    }
                                }
                                MediaTypeTab.VIDEO -> {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        SelectMediaButton(
                                            icon = "📁",
                                            title = "Choose Video File",
                                            subtitle = "MP4, MOV, WEBM · Max 100MB",
                                            onClick = { videoPickerLauncher.launch("video/*") }
                                        )
                                    }
                                }
                                MediaTypeTab.AUDIO -> {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        SelectMediaButton(
                                            icon = "🎵",
                                            title = "Select Audio File",
                                            subtitle = "MP3, WAV, M4A, AAC · Max 50MB",
                                            onClick = { audioPickerLauncher.launch("audio/*") }
                                        )

                                        // In-App Voice Recording Area
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(CardBackground)
                                                .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                                                .padding(14.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = if (uiState.isRecordingAudio) "Recording Live Voice..." else "Record Audio Live",
                                                        color = TextPrimary,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = if (uiState.isRecordingAudio) "Timer: ${String.format("%02d:%02d", uiState.audioRecordSeconds / 60, uiState.audioRecordSeconds % 60)}" else "Tap mic to capture practice clip",
                                                        color = if (uiState.isRecordingAudio) Color(0xFFDC2626) else TextSecondary,
                                                        fontSize = 11.sp
                                                    )
                                                }

                                                Box(
                                                    modifier = Modifier
                                                        .size(44.dp)
                                                        .clip(CircleShape)
                                                        .background(if (uiState.isRecordingAudio) Color(0xFFDC2626) else TaglineBackground)
                                                        .clickable {
                                                            if (uiState.isRecordingAudio) {
                                                                viewModel.stopVoiceRecording(context)
                                                            } else {
                                                                viewModel.startVoiceRecording(context)
                                                            }
                                                        },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = if (uiState.isRecordingAudio) "⏹️" else "🎙️",
                                                        fontSize = 18.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Error Message Display
                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFEE2E2))
                            .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "⚠️ ${uiState.errorMessage}",
                            color = Color(0xFFDC2626),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Upload Progress Bar
                if (uiState.isUploading) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Uploading & running AI models...", color = TextSecondary, fontSize = 11.sp)
                            Text(text = "${(uiState.uploadProgress * 100).toInt()}%", color = TaglineText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(Color(0xFFEDE5FA))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(uiState.uploadProgress)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(PrimaryGradientStart, PrimaryGradientEnd)
                                        )
                                    )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Primary Submit Button
                Button(
                    onClick = { viewModel.uploadPerformance(context) },
                    enabled = !uiState.isUploading && (uiState.selectedUri != null || uiState.capturedBitmap != null),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = PrimaryGradientStart),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color(0xFFD9D4E8)
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (uiState.selectedUri != null || uiState.capturedBitmap != null) {
                                    Brush.horizontalGradient(
                                        colors = listOf(PrimaryGradientStart, PrimaryGradientEnd)
                                    )
                                } else {
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFFD9D4E8), Color(0xFFD9D4E8))
                                    )
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SparkleIcon(size = 18.dp, color = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (uiState.isUploading) "Evaluating via AI..." else "Submit for AI Evaluation →",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // ==========================================
                // 4. AI ANALYSIS RESULTS DISPLAY CARD
                // ==========================================
                if (uiState.successResult != null) {
                    Spacer(modifier = Modifier.height(24.dp))
                    AnalysisResultCard(result = uiState.successResult!!)
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        // ==========================================
        // PREVIOUS UPLOADS MODAL SHEET
        // ==========================================
        if (uiState.showHistorySheet) {
            PreviousUploadsDialog(
                historyList = uiState.historyList,
                onDismiss = { viewModel.toggleHistorySheet(false) }
            )
        }
    }
}

// ========================================================
// PREVIEW WIDGETS
// ========================================================

@Composable
fun PhotoPreviewSection(
    bitmap: android.graphics.Bitmap?,
    fileName: String,
    fileSizeText: String,
    onReplace: () -> Unit,
    onRemove: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (bitmap != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFEDE5FA))
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Selected Photo",
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFEDE5FA)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🖼️ Image Selected", color = TaglineText, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fileName,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$fileSizeText · Ready for vision analysis",
                    color = GreenNode,
                    fontSize = 11.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onReplace,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TaglineBackground),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = "Replace", color = AccentPurple, fontSize = 11.sp)
                }

                Button(
                    onClick = onRemove,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = "Remove", color = Color(0xFFDC2626), fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun VideoPreviewSection(
    uri: Uri,
    fileName: String,
    fileSizeText: String,
    durationText: String,
    onReplace: () -> Unit,
    onRemove: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Video View with playback controller
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF25213A))
                .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { ctx ->
                    VideoView(ctx).apply {
                        setVideoURI(uri)
                        val mediaController = MediaController(ctx)
                        mediaController.setAnchorView(this)
                        setMediaController(mediaController)
                        start()
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fileName,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "⏱️ $durationText · $fileSizeText",
                    color = CyanNode,
                    fontSize = 11.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onReplace,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TaglineBackground),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = "Replace", color = AccentPurple, fontSize = 11.sp)
                }

                Button(
                    onClick = onRemove,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = "Remove", color = Color(0xFFDC2626), fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun AudioPreviewSection(
    fileName: String,
    fileSizeText: String,
    durationText: String,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onReplace: () -> Unit,
    onRemove: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play / Pause Circle Button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(PrimaryGradientStart, PrimaryGradientEnd)
                            )
                        )
                        .clickable { onTogglePlay() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = if (isPlaying) "⏸️" else "▶️", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = fileName,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "⏱️ $durationText · $fileSizeText",
                        color = GreenNode,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onReplace,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TaglineBackground),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = "Replace", color = AccentPurple, fontSize = 11.sp)
                }

                Button(
                    onClick = onRemove,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = "Remove", color = Color(0xFFDC2626), fontSize = 11.sp)
                }
            }
        }
    }
}

// ========================================================
// AI RESULTS DISPLAY COMPONENT
// ========================================================

@Composable
fun AnalysisResultCard(result: PerformanceUploadItemDto) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(22.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
            .clip(RoundedCornerShape(22.dp))
            .background(CardBackground)
            .border(
                1.5.dp,
                Brush.horizontalGradient(
                    colors = listOf(PrimaryGradientStart, PrimaryGradientEnd)
                ),
                RoundedCornerShape(22.dp)
            )
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SparkleIcon(size = 18.dp, color = TaglineText)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AI PERFORMANCE SCORE",
                        color = TaglineText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color(0xFFE8F5E9))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${result.score}% OVERALL",
                        color = GreenNode,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = result.title,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = result.analysisSummary,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // AI Feedback Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(TaglineBackground)
                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💡", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI Coach Feedback",
                            color = TaglineText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = result.aiFeedback,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Gemini API status indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (result.geminiConfigured) "⚡ Powered by Gemini 1.5 Flash Cloud LLM" else "ℹ️ ${result.geminiNote ?: "Gemini API key is optional"}",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

// ========================================================
// PREVIOUS UPLOADS DIALOG
// ========================================================

@Composable
fun PreviousUploadsDialog(
    historyList: List<PerformanceUploadItemDto>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Close", color = TaglineText, fontWeight = FontWeight.Bold)
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🕒", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Previous Uploads (${historyList.size})",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Box(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                if (historyList.isEmpty()) {
                    Text(
                        text = "No previous performance uploads yet. Try uploading a photo, video or audio!",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 20.dp)
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        historyList.forEach { item ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CardBackground)
                                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val emoji = when (item.mediaType.lowercase()) {
                                        "photo" -> "🖼️"
                                        "video" -> "📹"
                                        else -> "🎙️"
                                    }
                                    Text(text = emoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.title,
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${item.skillCategory.uppercase()} · ${item.createdAt}",
                                            color = TextSecondary,
                                            fontSize = 10.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFE8F5E9))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${item.score}%",
                                            color = GreenNode,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        containerColor = CardBackground,
        shape = RoundedCornerShape(20.dp)
    )
}

// ========================================================
// HELPER TILES
// ========================================================

@Composable
fun UploadTypeCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: String,
    subtitle: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(if (isSelected) 4.dp else 2.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) TaglineBackground else CardBackground)
            .border(
                1.5.dp,
                if (isSelected) accentColor else CardBorder,
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = icon, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = if (isSelected) AccentPurple else TextPrimary,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 8.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SelectMediaButton(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFFD6C8F2), ambientColor = Color(0xFFE9DDFF))
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
            Text(text = "Choose →", color = TaglineText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
