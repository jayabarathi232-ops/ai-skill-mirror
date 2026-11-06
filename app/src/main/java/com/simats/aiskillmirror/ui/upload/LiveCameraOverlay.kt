package com.simats.aiskillmirror.ui.upload

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.view.TextureView
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.simats.aiskillmirror.ui.theme.*
import java.io.File
import java.util.Locale

@Composable
fun LiveCameraOverlay(
    uiState: PerformanceUploadUiState,
    viewModel: PerformanceUploadViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var hasCameraPerm by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasAudioPerm by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        hasCameraPerm = perms[Manifest.permission.CAMERA] ?: hasCameraPerm
        hasAudioPerm = perms[Manifest.permission.RECORD_AUDIO] ?: hasAudioPerm
        viewModel.setCameraPermission(hasCameraPerm && hasAudioPerm)
    }

    // Camera2 Manager Instance
    val cameraManager = remember { LiveCameraManager(context) }
    var textureViewRef by remember { mutableStateOf<TextureView?>(null) }

    // Pulsing recording dot animation
    val infiniteTransition = rememberInfiniteTransition(label = "recordingPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // Clean up camera streams when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            cameraManager.stopCamera()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (!hasCameraPerm || (!hasAudioPerm && uiState.liveCameraMode == LiveCameraMode.VIDEO)) {
            // Permission Request Card
            CameraPermissionCard(
                onGrantClick = {
                    permissionLauncher.launch(
                        arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                    )
                },
                onCancelClick = onDismiss
            )
        } else if (uiState.isReviewingCapture) {
            // ==========================================
            // PREVIEW & CONFIRMATION SCREEN (Retake / Use / Cancel)
            // ==========================================
            LiveCaptureReviewScreen(
                uiState = uiState,
                onRetake = {
                    viewModel.retakeLiveCapture()
                    textureViewRef?.let { tv ->
                        cameraManager.startCamera(tv, front = uiState.isUsingFrontCamera)
                    }
                },
                onUsePhoto = { viewModel.usePhotoCapturedLive(context) },
                onUseVideo = { viewModel.useVideoRecordedLive(context) },
                onCancel = {
                    viewModel.closeLiveCamera()
                    onDismiss()
                }
            )
        } else {
            // ==========================================
            // LIVE CAMERA VIEWFINDER (TextureView Preview)
            // ==========================================
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    TextureView(ctx).apply {
                        textureViewRef = this
                        cameraManager.startCamera(
                            textureView = this,
                            front = uiState.isUsingFrontCamera,
                            onError = { err ->
                                Toast.makeText(ctx, err, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            )

            // Camera Viewfinder Framing & Guides
            CameraViewfinderGuide()

            // TOP CONTROLS BAR: Back, Mode Switcher, Switch Front/Rear Camera
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Close button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable {
                            cameraManager.stopCamera()
                            viewModel.closeLiveCamera()
                            onDismiss()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✕", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                // Mode Selector: Photo vs Video
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (uiState.liveCameraMode == LiveCameraMode.PHOTO) AccentPurple else Color.Transparent)
                            .clickable {
                                if (!uiState.isRecordingVideo) {
                                    viewModel.setLiveCameraMode(LiveCameraMode.PHOTO)
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "📸 Photo",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = if (uiState.liveCameraMode == LiveCameraMode.PHOTO) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (uiState.liveCameraMode == LiveCameraMode.VIDEO) AccentPurple else Color.Transparent)
                            .clickable {
                                if (!uiState.isRecordingVideo) {
                                    viewModel.setLiveCameraMode(LiveCameraMode.VIDEO)
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "🎥 Video",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = if (uiState.liveCameraMode == LiveCameraMode.VIDEO) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                // Switch Camera Facing (Front ⟲ Rear)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable {
                            if (!uiState.isRecordingVideo) {
                                viewModel.toggleCameraFacing()
                                cameraManager.switchCamera { err ->
                                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🔄", fontSize = 18.sp)
                }
            }

            // BOTTOM CONTROLS BAR: Shutter / Record / Pause / Stop
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Recording status & timer banner for Video Mode
                if (uiState.liveCameraMode == LiveCameraMode.VIDEO) {
                    val formattedDuration = String.format(
                        Locale.US,
                        "%02d:%02d",
                        uiState.videoRecordSeconds / 60,
                        uiState.videoRecordSeconds % 60
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.Black.copy(alpha = 0.65f))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (uiState.isRecordingVideo) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .alpha(if (uiState.isVideoRecordingPaused) 0.5f else pulseAlpha)
                                        .clip(CircleShape)
                                        .background(Color.Red)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (uiState.isVideoRecordingPaused) "PAUSED $formattedDuration" else "REC $formattedDuration",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    text = "Ready to record (Max 5:00)",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Shutter / Record Action Buttons
                when (uiState.liveCameraMode) {
                    LiveCameraMode.PHOTO -> {
                        // Capture Photo Button
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f))
                                .border(3.dp, Color.White, CircleShape)
                                .clickable {
                                    cameraManager.capturePhoto(
                                        onSuccess = { bitmap, file ->
                                            viewModel.onPhotoCapturedLive(bitmap, file)
                                        },
                                        onError = { err ->
                                            Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }
                    }

                    LiveCameraMode.VIDEO -> {
                        if (!uiState.isRecordingVideo) {
                            // Start Recording Button
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red.copy(alpha = 0.25f))
                                    .border(3.dp, Color.White, CircleShape)
                                    .clickable {
                                        cameraManager.startVideoRecording(
                                            onStarted = {
                                                viewModel.onVideoRecordingStarted()
                                            },
                                            onError = { err ->
                                                Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red)
                                )
                            }
                        } else {
                            // Recording in progress: Pause, Resume, Stop Controls
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Pause / Resume Button
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.6f))
                                        .border(1.dp, Color.White, CircleShape)
                                        .clickable {
                                            if (uiState.isVideoRecordingPaused) {
                                                cameraManager.resumeVideoRecording()
                                                viewModel.onVideoRecordingResumed()
                                            } else {
                                                cameraManager.pauseVideoRecording()
                                                viewModel.onVideoRecordingPaused()
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (uiState.isVideoRecordingPaused) "▶" else "⏸",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Stop Recording Button (Square red)
                                Box(
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red.copy(alpha = 0.25f))
                                        .border(3.dp, Color.White, CircleShape)
                                        .clickable {
                                            cameraManager.stopVideoRecording(
                                                onSuccess = { file ->
                                                    viewModel.onVideoRecordingStopped(file)
                                                },
                                                onError = { err ->
                                                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                                }
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color.Red)
                                    )
                                }

                                // Cancel Button
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.6f))
                                        .border(1.dp, Color.White, CircleShape)
                                        .clickable {
                                            cameraManager.stopCamera()
                                            viewModel.closeLiveCamera()
                                            onDismiss()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "✕", color = Color.White, fontSize = 18.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// CAPTURED MEDIA REVIEW SCREEN (Retake / Use / Cancel)
// -------------------------------------------------------------
@Composable
private fun LiveCaptureReviewScreen(
    uiState: PerformanceUploadUiState,
    onRetake: () -> Unit,
    onUsePhoto: () -> Unit,
    onUseVideo: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (uiState.capturedPhotoPreview != null) "Review Live Photo" else "Review Recorded Video",
                    color = HeadingPurple,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = onCancel) {
                    Text("Cancel", color = TextSecondary, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Preview Area (Photo or Video)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.Black)
                    .border(2.dp, CardBorder, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.capturedPhotoPreview != null) {
                    Image(
                        bitmap = uiState.capturedPhotoPreview.asImageBitmap(),
                        contentDescription = "Captured Photo Preview",
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (uiState.recordedVideoPreviewUri != null) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            VideoView(ctx).apply {
                                setVideoURI(uiState.recordedVideoPreviewUri)
                                val controller = MediaController(ctx)
                                controller.setAnchorView(this)
                                setMediaController(controller)
                                setOnPreparedListener { mp ->
                                    mp.isLooping = true
                                    start()
                                }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info Note
            Text(
                text = if (uiState.capturedPhotoPreview != null)
                    "Photo captured successfully! Choose 'Use Photo' to send for AI analysis."
                else
                    "Video recorded successfully (${uiState.videoRecordSeconds}s)! Choose 'Use Video' to send for AI analysis.",
                color = TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons: Retake vs Use Media
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Retake Button
                OutlinedButton(
                    onClick = onRetake,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = CardBackground,
                        contentColor = TextPrimary
                    )
                ) {
                    Text("🔄 Retake", fontWeight = FontWeight.SemiBold)
                }

                // Use Media Button
                Button(
                    onClick = {
                        if (uiState.capturedPhotoPreview != null) {
                            onUsePhoto()
                        } else {
                            onUseVideo()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPurple,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = if (uiState.capturedPhotoPreview != null) "Use Photo ✓" else "Use Video ✓",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// CAMERA PERMISSION REQUEST CARD
// -------------------------------------------------------------
@Composable
private fun CameraPermissionCard(
    onGrantClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(LightPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📷", fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Camera Permission Required",
                    color = HeadingPurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "AI Skill Mirror requires camera and audio permissions to capture live photo poses and record skill performances for AI computer vision and speech analysis.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onGrantClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
                ) {
                    Text("Grant Permission", fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(onClick = onCancelClick) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// CAMERA VIEWFINDER OVERLAY GUIDES (Grid lines)
// -------------------------------------------------------------
@Composable
private fun CameraViewfinderGuide() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 80.dp)
    ) {
        // Subtle rule-of-thirds grid lines
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.2f))
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.2f))
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        Row(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(Color.White.copy(alpha = 0.2f))
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(Color.White.copy(alpha = 0.2f))
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

