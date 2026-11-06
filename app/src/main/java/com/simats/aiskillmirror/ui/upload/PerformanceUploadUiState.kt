package com.simats.aiskillmirror.ui.upload

import android.graphics.Bitmap
import android.net.Uri
import com.simats.aiskillmirror.network.PerformanceUploadItemDto
import java.io.File

enum class MediaTypeTab {
    PHOTO,
    VIDEO,
    AUDIO,
    LIVE_CAPTURE
}

enum class LiveCameraMode {
    PHOTO,
    VIDEO
}

data class PerformanceUploadUiState(
    val selectedTab: MediaTypeTab = MediaTypeTab.PHOTO,
    val skillCategory: String = "photography",
    val performanceTitle: String = "",
    val selectedUri: Uri? = null,
    val capturedBitmap: Bitmap? = null,
    val selectedFileName: String? = null,
    val selectedFileSizeBytes: Long = 0,
    val selectedFileSizeText: String? = null,
    val selectedDurationText: String? = null,
    val selectedDurationSeconds: Int? = null,
    val isUploading: Boolean = false,
    val uploadProgress: Float = 0f,
    val isPlayingAudio: Boolean = false,
    val isRecordingAudio: Boolean = false,
    val audioRecordSeconds: Int = 0,
    val errorMessage: String? = null,
    val successResult: PerformanceUploadItemDto? = null,
    val historyList: List<PerformanceUploadItemDto> = emptyList(),
    val isLoadingHistory: Boolean = false,
    val showHistorySheet: Boolean = false,

    // Live In-App Camera Viewfinder & Recording State
    val isLiveCameraOpen: Boolean = false,
    val liveCameraMode: LiveCameraMode = LiveCameraMode.PHOTO,
    val isUsingFrontCamera: Boolean = false,
    val isRecordingVideo: Boolean = false,
    val isVideoRecordingPaused: Boolean = false,
    val videoRecordSeconds: Int = 0,
    val capturedPhotoPreview: Bitmap? = null,
    val recordedVideoPreviewUri: Uri? = null,
    val recordedVideoFile: File? = null,
    val isReviewingCapture: Boolean = false,
    val hasCameraPermission: Boolean = false,
    val liveCameraError: String? = null
)

sealed interface PerformanceUploadUiEvent {
    data class ShowToast(val message: String) : PerformanceUploadUiEvent
    object UploadSuccess : PerformanceUploadUiEvent
    object RequestCameraPermission : PerformanceUploadUiEvent
}
