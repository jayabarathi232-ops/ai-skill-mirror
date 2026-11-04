package com.simats.aiskillmirror.ui.upload

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.aiskillmirror.data.network.UserSession
import com.simats.aiskillmirror.data.repository.PerformanceUploadRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

class PerformanceUploadViewModel(
    private val repository: PerformanceUploadRepository = PerformanceUploadRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerformanceUploadUiState())
    val uiState: StateFlow<PerformanceUploadUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<PerformanceUploadUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    private var mediaPlayer: MediaPlayer? = null
    private var mediaRecorder: MediaRecorder? = null
    private var audioRecordingFile: File? = null
    private var recordingTimerJob: Job? = null

    init {
        loadHistory()
    }

    fun selectTab(tab: MediaTypeTab) {
        clearSelectedMedia()
        val defaultCategory = when (tab) {
            MediaTypeTab.PHOTO -> "photography"
            MediaTypeTab.VIDEO -> "sports"
            MediaTypeTab.AUDIO -> "music"
        }
        _uiState.value = _uiState.value.copy(
            selectedTab = tab,
            skillCategory = defaultCategory,
            errorMessage = null,
            successResult = null
        )
    }

    fun setSkillCategory(category: String) {
        _uiState.value = _uiState.value.copy(skillCategory = category)
    }

    fun setPerformanceTitle(title: String) {
        _uiState.value = _uiState.value.copy(performanceTitle = title)
    }

    fun toggleHistorySheet(show: Boolean) {
        _uiState.value = _uiState.value.copy(showHistorySheet = show)
        if (show) {
            loadHistory()
        }
    }

    fun onMediaSelected(context: Context, uri: Uri) {
        clearSelectedMedia()
        val currentTab = _uiState.value.selectedTab

        // Extract metadata
        var fileName = "performance_media"
        var fileSize: Long = 0

        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex != -1) fileName = cursor.getString(nameIndex) ?: fileName
                    if (sizeIndex != -1) fileSize = cursor.getLong(sizeIndex)
                }
            }
        } catch (_: Exception) {}

        if (fileSize == 0L) {
            try {
                fileSize = context.contentResolver.openFileDescriptor(uri, "r")?.statSize ?: 0L
            } catch (_: Exception) {}
        }

        val ext = fileName.substringAfterLast(".", "").lowercase(Locale.ROOT)

        // Validation by media tab
        val validationError = when (currentTab) {
            MediaTypeTab.PHOTO -> validatePhoto(ext, fileSize)
            MediaTypeTab.VIDEO -> validateVideo(ext, fileSize)
            MediaTypeTab.AUDIO -> validateAudio(ext, fileSize)
        }

        if (validationError != null) {
            _uiState.value = _uiState.value.copy(errorMessage = validationError)
            return
        }

        val sizeText = formatFileSize(fileSize)

        _uiState.value = _uiState.value.copy(
            selectedUri = uri,
            capturedBitmap = null,
            selectedFileName = fileName,
            selectedFileSizeBytes = fileSize,
            selectedFileSizeText = sizeText,
            selectedDurationText = if (currentTab != MediaTypeTab.PHOTO) "00:45" else null,
            selectedDurationSeconds = if (currentTab != MediaTypeTab.PHOTO) 45 else null,
            errorMessage = null
        )

        // If audio, prepare duration with MediaPlayer
        if (currentTab == MediaTypeTab.AUDIO) {
            prepareAudioPreview(context, uri)
        }
    }

    fun onPhotoCaptured(context: Context, bitmap: Bitmap) {
        clearSelectedMedia()
        try {
            val cacheFile = File(context.cacheDir, "camera_capture_${System.currentTimeMillis()}.jpg")
            FileOutputStream(cacheFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            val size = cacheFile.length()
            val uri = Uri.fromFile(cacheFile)

            _uiState.value = _uiState.value.copy(
                selectedTab = MediaTypeTab.PHOTO,
                selectedUri = uri,
                capturedBitmap = bitmap,
                selectedFileName = cacheFile.name,
                selectedFileSizeBytes = size,
                selectedFileSizeText = formatFileSize(size),
                errorMessage = null
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(errorMessage = "Failed to process captured image: ${e.message}")
        }
    }

    fun startVoiceRecording(context: Context) {
        try {
            stopAudioPlayback()
            val audioFile = File(context.cacheDir, "recorded_voice_${System.currentTimeMillis()}.m4a")
            audioRecordingFile = audioFile

            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFile.absolutePath)
                prepare()
                start()
            }
            mediaRecorder = recorder

            _uiState.value = _uiState.value.copy(
                isRecordingAudio = true,
                audioRecordSeconds = 0,
                errorMessage = null
            )

            recordingTimerJob?.cancel()
            recordingTimerJob = viewModelScope.launch {
                while (_uiState.value.isRecordingAudio) {
                    delay(1000)
                    _uiState.value = _uiState.value.copy(
                        audioRecordSeconds = _uiState.value.audioRecordSeconds + 1
                    )
                }
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(errorMessage = "Microphone error: ${e.message}. Please check permissions.")
        }
    }

    fun stopVoiceRecording(context: Context) {
        try {
            recordingTimerJob?.cancel()
            mediaRecorder?.apply {
                try {
                    stop()
                } catch (_: Exception) {}
                release()
            }
            mediaRecorder = null

            val file = audioRecordingFile
            if (file != null && file.exists() && file.length() > 0) {
                val uri = Uri.fromFile(file)
                val durationSec = _uiState.value.audioRecordSeconds.coerceAtLeast(1)
                val durationText = String.format(Locale.US, "%02d:%02d", durationSec / 60, durationSec % 60)

                _uiState.value = _uiState.value.copy(
                    isRecordingAudio = false,
                    selectedUri = uri,
                    selectedFileName = file.name,
                    selectedFileSizeBytes = file.length(),
                    selectedFileSizeText = formatFileSize(file.length()),
                    selectedDurationText = durationText,
                    selectedDurationSeconds = durationSec,
                    errorMessage = null
                )
                prepareAudioPreview(context, uri)
            } else {
                _uiState.value = _uiState.value.copy(
                    isRecordingAudio = false,
                    errorMessage = "Recording was too short or empty."
                )
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isRecordingAudio = false,
                errorMessage = "Failed to save recording: ${e.message}"
            )
        }
    }

    fun toggleAudioPlayback(context: Context) {
        val uri = _uiState.value.selectedUri ?: return
        if (mediaPlayer == null) {
            prepareAudioPreview(context, uri)
        }
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
                _uiState.value = _uiState.value.copy(isPlayingAudio = false)
            } else {
                player.start()
                _uiState.value = _uiState.value.copy(isPlayingAudio = true)
            }
        }
    }

    private fun prepareAudioPreview(context: Context, uri: Uri) {
        stopAudioPlayback()
        try {
            val player = MediaPlayer().apply {
                setDataSource(context, uri)
                prepare()
                setOnCompletionListener {
                    _uiState.value = _uiState.value.copy(isPlayingAudio = false)
                }
            }
            mediaPlayer = player
            val durationMs = player.duration
            if (durationMs > 0) {
                val sec = durationMs / 1000
                val durationText = String.format(Locale.US, "%02d:%02d", sec / 60, sec % 60)
                _uiState.value = _uiState.value.copy(
                    selectedDurationText = durationText,
                    selectedDurationSeconds = sec
                )
            }
        } catch (_: Exception) {}
    }

    private fun stopAudioPlayback() {
        mediaPlayer?.apply {
            try {
                if (isPlaying) stop()
            } catch (_: Exception) {}
            release()
        }
        mediaPlayer = null
        _uiState.value = _uiState.value.copy(isPlayingAudio = false)
    }

    fun clearSelectedMedia() {
        stopAudioPlayback()
        _uiState.value = _uiState.value.copy(
            selectedUri = null,
            capturedBitmap = null,
            selectedFileName = null,
            selectedFileSizeBytes = 0,
            selectedFileSizeText = null,
            selectedDurationText = null,
            selectedDurationSeconds = null,
            errorMessage = null,
            successResult = null,
            isUploading = false,
            uploadProgress = 0f
        )
    }

    fun uploadPerformance(context: Context) {
        val uri = _uiState.value.selectedUri
        if (uri == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please select or capture a file first.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isUploading = true,
                uploadProgress = 0.2f,
                errorMessage = null
            )

            try {
                // Copy URI to cache file for multipart submission
                val ext = (_uiState.value.selectedFileName ?: "media").substringAfterLast(".", "tmp")
                val tempFile = File(context.cacheDir, "upload_temp_${System.currentTimeMillis()}.$ext")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }

                _uiState.value = _uiState.value.copy(uploadProgress = 0.5f)

                val mediaType = when (_uiState.value.selectedTab) {
                    MediaTypeTab.PHOTO -> "photo"
                    MediaTypeTab.VIDEO -> "video"
                    MediaTypeTab.AUDIO -> "audio"
                }

                val mime = when (ext.lowercase(Locale.ROOT)) {
                    "jpg", "jpeg" -> "image/jpeg"
                    "png" -> "image/png"
                    "webp" -> "image/webp"
                    "mp4" -> "video/mp4"
                    "mov" -> "video/quicktime"
                    "webm" -> "video/webm"
                    "mp3" -> "audio/mpeg"
                    "wav" -> "audio/wav"
                    "m4a" -> "audio/mp4"
                    "aac" -> "audio/aac"
                    else -> "*/*"
                }

                val title = _uiState.value.performanceTitle.ifBlank {
                    "${_uiState.value.skillCategory.replaceFirstChar { it.uppercase() }} $mediaType performance"
                }

                _uiState.value = _uiState.value.copy(uploadProgress = 0.8f)

                val result = repository.uploadPerformance(
                    userId = UserSession.userId,
                    mediaType = mediaType,
                    skillCategory = _uiState.value.skillCategory,
                    title = title,
                    file = tempFile,
                    mimeType = mime
                )

                _uiState.value = _uiState.value.copy(uploadProgress = 1.0f, isUploading = false)

                if (result.isSuccess && result.data != null) {
                    _uiState.value = _uiState.value.copy(
                        successResult = result.data,
                        errorMessage = null
                    )
                    _eventChannel.send(PerformanceUploadUiEvent.UploadSuccess)
                    _eventChannel.send(PerformanceUploadUiEvent.ShowToast("Upload complete! AI Score: ${result.data.score}%"))
                    loadHistory()
                } else {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = result.message ?: "Upload failed. Please check connection."
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUploading = false,
                    errorMessage = "Upload error: ${e.message}"
                )
            }
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingHistory = true)
            val res = repository.getPerformanceHistory(UserSession.userId)
            if (res.isSuccess && res.data != null) {
                _uiState.value = _uiState.value.copy(
                    historyList = res.data,
                    isLoadingHistory = false
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoadingHistory = false)
            }
        }
    }

    private fun validatePhoto(ext: String, sizeBytes: Long): String? {
        val allowed = listOf("jpg", "jpeg", "png", "webp")
        if (ext !in allowed) {
            return "Invalid image format ($ext). Only JPG, JPEG, PNG, and WEBP are supported."
        }
        if (sizeBytes > 15 * 1024 * 1024) {
            return "Image size exceeds 15MB limit."
        }
        return null
    }

    private fun validateVideo(ext: String, sizeBytes: Long): String? {
        val allowed = listOf("mp4", "mov", "webm")
        if (ext !in allowed) {
            return "Invalid video format ($ext). Only MP4, MOV, and WEBM are supported."
        }
        if (sizeBytes > 100 * 1024 * 1024) {
            return "Video size exceeds 100MB limit."
        }
        return null
    }

    private fun validateAudio(ext: String, sizeBytes: Long): String? {
        val allowed = listOf("mp3", "wav", "m4a", "aac")
        if (ext !in allowed) {
            return "Invalid audio format ($ext). Only MP3, WAV, M4A, and AAC are supported."
        }
        if (sizeBytes > 50 * 1024 * 1024) {
            return "Audio size exceeds 50MB limit."
        }
        return null
    }

    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 -> String.format(Locale.US, "%.1f MB", bytes.toFloat() / (1024 * 1024))
            bytes >= 1024 -> String.format(Locale.US, "%.1f KB", bytes.toFloat() / 1024)
            else -> "$bytes B"
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopAudioPlayback()
        recordingTimerJob?.cancel()
        mediaRecorder?.release()
    }
}

