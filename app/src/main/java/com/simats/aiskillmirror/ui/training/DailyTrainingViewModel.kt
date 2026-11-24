package com.simats.aiskillmirror.ui.training

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.aiskillmirror.data.network.ApiClient
import com.simats.aiskillmirror.data.network.UserSession
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.ceil

class DailyTrainingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DailyTrainingUiState())
    val uiState: StateFlow<DailyTrainingUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<DailyTrainingUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    private var sessionTimerJob: Job? = null
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var voiceTimerJob: Job? = null
    private var audioRecordingFile: File? = null

    init {
        loadTraining()
    }

    fun loadTraining(subcategoryId: String? = null, mainSkill: String? = null) {
        // Immediate optimistic skill switch if subcategoryId or mainSkill is provided
        if (!subcategoryId.isNullOrBlank() || !mainSkill.isNullOrBlank()) {
            val subLow = (subcategoryId ?: "").lowercase().trim()
            val skillLow = (mainSkill ?: "").lowercase().trim()

            val isCoding = skillLow.contains("cod") || skillLow.contains("prog") ||
                    subLow.contains("cod") || subLow.contains("python") || subLow.contains("java") ||
                    subLow.contains("script") || subLow.contains("c++") || subLow.contains("c_prog") ||
                    subLow.contains("algo") || subLow.contains("php") || subLow.contains("html")

            val isPhoto = skillLow.contains("photo") ||
                    subLow.contains("photo") || subLow.contains("camera") ||
                    subLow.contains("portrait") || subLow.contains("landscape") ||
                    subLow.contains("nature") || subLow.contains("street") || subLow.contains("wildlife")

            val isMusic = skillLow.contains("music") ||
                    subLow.contains("guitar") || subLow.contains("piano") || subLow.contains("drum") ||
                    subLow.contains("violin") || subLow.contains("flute") || subLow.contains("veena") ||
                    subLow.contains("sing") || subLow.contains("vocal")

            val inferredMain = when {
                isCoding -> "coding"
                isPhoto -> "photography"
                isMusic -> "music"
                else -> _uiState.value.activeMainSkill
            }

            val inferredName = when {
                subLow == "python" -> "Python Programming"
                subLow == "java" -> "Java"
                subLow == "c_prog" -> "C Programming"
                subLow == "cpp" -> "C++"
                subLow == "javascript" -> "JavaScript"
                subLow == "html_css" -> "HTML & CSS"
                subLow == "php" -> "PHP"
                subLow == "coding" -> "Coding & Algorithms"
                subLow == "portrait_photo" -> "Portrait Photography"
                subLow == "nature_photo" -> "Nature Photography"
                subLow == "landscape_photo" -> "Landscape Photography"
                subLow == "street_photo" -> "Street Photography"
                subLow == "product_photo" -> "Product Photography"
                subLow == "mobile_photo" -> "Mobile Photography"
                subLow == "wildlife_photo" -> "Wildlife Photography"
                subLow == "photography" -> "Photography"
                subLow == "guitar" -> "Guitar"
                subLow == "piano" -> "Piano / Keyboard"
                subLow == "violin" -> "Violin"
                subLow == "drums" -> "Drums"
                subLow == "flute" -> "Flute"
                subLow == "veena" -> "Veena"
                subLow == "singing" -> "Singing / Vocal Training"
                subLow.isNotBlank() -> subLow.replace("_", " ").split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
                else -> _uiState.value.activeSubcategoryName
            }

            val langToSet = if (isCoding) {
                when {
                    subLow.contains("python") -> "Python"
                    subLow.contains("java") -> "Java"
                    subLow.contains("cpp") -> "C++"
                    subLow.contains("c_prog") -> "C"
                    subLow.contains("script") -> "JavaScript"
                    else -> _uiState.value.selectedLanguage
                }
            } else _uiState.value.selectedLanguage

            val starterCode = if (isCoding) {
                getStarterCodeForLanguage(langToSet, inferredName)
            } else _uiState.value.codeEditorText

            _uiState.value = _uiState.value.copy(
                activeMainSkill = inferredMain,
                activeSubcategoryName = inferredName,
                upNextTitle = "Daily $inferredName Practice",
                upNextCategory = "${inferredMain.replaceFirstChar { it.uppercase() }} · $inferredName",
                capturedPhotoPath = null,
                selectedImagePath = null,
                showEvaluationModal = false,
                showSessionCompletionDialog = false,
                showVocalReviewDialog = false,
                isEvaluatingArtifact = false,
                evaluationError = null,
                canRetryAnalysis = false,
                codeMistake = null,
                codingOutput = "",
                codingError = "",
                selectedLanguage = langToSet,
                codeEditorText = starterCode,
                codeSnippetInput = starterCode
            )
        }

        viewModelScope.launch {
            val result = ApiClient.fetchTraining(UserSession.userId, subcategoryId)
            if (result.isSuccess && result.data != null) {
                val d = result.data
                val completed = d.completedPracticeItems.map {
                    PracticeItem(
                        id = it.id,
                        title = it.title,
                        category = it.category,
                        durationText = it.durationText,
                        xpText = it.xpText,
                        isCompleted = it.isCompleted,
                        performanceScore = it.performanceScore,
                        scoreStatus = it.scoreStatus,
                        scoreReason = it.scoreReason,
                        strengths = it.strengths,
                        weaknesses = it.weaknesses,
                        aiFeedback = it.aiFeedback,
                        metrics = it.metrics,
                        assessmentType = it.assessmentType
                    )
                }

                val pending = d.pendingPracticeItems.map {
                    PracticeItem(
                        id = it.id,
                        title = it.title,
                        category = it.category,
                        durationText = it.durationText,
                        xpText = it.xpText,
                        isCompleted = false
                    )
                }

                val weekly = d.weeklyLearningPlan.map {
                    WeeklyPlanItem(day = it.day, focus = it.focus)
                }

                val sub = d.activeSubcategory
                val finalMain = sub?.mainSkill ?: _uiState.value.activeMainSkill
                val finalSubName = sub?.subcategoryName ?: _uiState.value.activeSubcategoryName

                // Safeguard: Ensure received upNext category aligns with active skill
                val isCodingActive = finalMain.lowercase().contains("cod") || finalSubName.lowercase().contains("python") || finalSubName.lowercase().contains("code")
                val isPhotoActive = finalMain.lowercase().contains("photo") || finalSubName.lowercase().contains("photo")
                val isMusicActive = finalMain.lowercase().contains("music")

                val rawCat = d.upNextCategory.lowercase()
                val isMismatched = (isCodingActive && (rawCat.contains("photo") || rawCat.contains("music"))) ||
                        (isPhotoActive && (rawCat.contains("cod") || rawCat.contains("music"))) ||
                        (isMusicActive && (rawCat.contains("cod") || rawCat.contains("photo")))

                val cleanTitle = if (!isMismatched && d.upNextTitle.isNotBlank()) d.upNextTitle else "Daily $finalSubName Practice"
                val cleanCategory = if (!isMismatched && d.upNextCategory.isNotBlank()) d.upNextCategory else "${finalMain.replaceFirstChar { it.uppercase() }} · $finalSubName"

                _uiState.value = _uiState.value.copy(
                    headerDateText = d.headerDateText,
                    streakBadgeText = d.streakBadgeText,
                    currentMinutes = d.currentMinutes,
                    targetMinutes = d.targetMinutes,
                    totalXp = d.totalXp,
                    practiceProgressText = d.practiceProgressText,
                    completedPracticeItems = completed,
                    pendingPracticeItems = pending,
                    upNextTitle = cleanTitle,
                    upNextCategory = cleanCategory,
                    upNextMetaText = d.upNextMetaText,
                    upNextDescription = d.upNextDescription,
                    startButtonText = d.startButtonText,
                    activeSubcategoryName = finalSubName,
                    activeMainSkill = finalMain,
                    activeProficiencyLevel = sub?.proficiencyLevel ?: _uiState.value.activeProficiencyLevel,
                    activeLearningGoal = sub?.learningGoal ?: _uiState.value.activeLearningGoal,
                    subcatXp = sub?.xpEarned ?: 0,
                    subcatLessonsCount = sub?.lessonsCompleted ?: 0,
                    subcatMinutes = sub?.practiceMinutes ?: 0,
                    stepByStepInstructions = if (d.stepByStepInstructions.isNotEmpty()) d.stepByStepInstructions else _uiState.value.stepByStepInstructions,
                    beginnerExercises = if (d.beginnerExercises.isNotEmpty()) d.beginnerExercises else _uiState.value.beginnerExercises,
                    practiceChallenges = if (d.practiceChallenges.isNotEmpty()) d.practiceChallenges else _uiState.value.practiceChallenges,
                    weeklyLearningPlan = if (weekly.isNotEmpty()) weekly else _uiState.value.weeklyLearningPlan,
                    aiTip = if (d.aiTip.isNotBlank()) d.aiTip else _uiState.value.aiTip
                )
            }
        }
    }

    // ==========================================
    // Real Practice Session Timer Management
    // ==========================================

    fun startPracticeSession(context: Context? = null, autoRecordVoice: Boolean = false) {
        sessionTimerJob?.cancel()
        _uiState.value = _uiState.value.copy(
            isSessionActive = true,
            isSessionPaused = false,
            elapsedSeconds = 0,
            formattedElapsed = "00:00",
            completionCelebration = null,
            lastSessionScoreResult = null,
            showSessionCompletionDialog = false,
            showVocalReviewDialog = false,
            vocalAnalysisState = VocalAnalysisState.IDLE,
            recordedAudioPath = null,
            voiceRecordSeconds = 0
        )

        sessionTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val current = _uiState.value
                if (current.isSessionActive && !current.isSessionPaused) {
                    val nextSec = current.elapsedSeconds + 1
                    val m = nextSec / 60
                    val s = nextSec % 60
                    val formatted = String.format("%02d:%02d", m, s)
                    _uiState.value = current.copy(
                        elapsedSeconds = nextSec,
                        formattedElapsed = formatted
                    )
                }
            }
        }

        if (context != null && autoRecordVoice && _uiState.value.isVocalSkill) {
            startVoiceRecording(context)
        }
    }

    fun pausePracticeSession() {
        _uiState.value = _uiState.value.copy(isSessionPaused = true)
        if (_uiState.value.isRecordingVoice) {
            pauseVoiceRecording()
        }
    }

    fun resumePracticeSession() {
        _uiState.value = _uiState.value.copy(isSessionPaused = false)
        if (_uiState.value.isRecordingVoice && _uiState.value.isRecordingPaused) {
            resumeVoiceRecording()
        }
    }

    fun cancelPracticeSession() {
        sessionTimerJob?.cancel()
        sessionTimerJob = null
        stopVoiceRecording()
        clearVoiceRecording()
        _uiState.value = _uiState.value.copy(
            isSessionActive = false,
            isSessionPaused = false,
            elapsedSeconds = 0,
            formattedElapsed = "00:00",
            showVocalReviewDialog = false
        )
    }

    /**
     * Called when user presses Finish on the practice session.
     * In Music Vocal sessions, stops recording, preserves audio, and opens preview/analysis.
     * In other skills, completes normally.
     */
    fun onFinishSessionClick(context: Context) {
        val current = _uiState.value
        if (current.isVocalSkill) {
            if (current.isRecordingVoice) {
                stopVoiceRecording()
            }
            sessionTimerJob?.cancel()
            sessionTimerJob = null

            val audioPath = _uiState.value.recordedAudioPath
            val audioFile = audioPath?.let { File(it) }
            if (audioFile != null && audioFile.exists() && audioFile.length() >= 400) {
                // Show Vocal Review & AI Analysis preview (Requirements 4, 7, 10)
                _uiState.value = _uiState.value.copy(
                    isSessionActive = false,
                    isSessionPaused = false,
                    showVocalReviewDialog = true,
                    vocalAnalysisState = VocalAnalysisState.IDLE,
                    evaluationError = null,
                    canRetryAnalysis = false
                )
            } else {
                // No vocal recording provided -> Award XP with "Score Not Available" (Requirement 11)
                finishAndCompleteSession(audioPath = null)
            }
        } else {
            finishAndCompleteSession()
        }
    }

    fun finishAndCompleteSession(
        codeSnippet: String? = null,
        transcript: String? = null,
        quizAnswers: List<Pair<Int, Boolean>>? = null,
        audioPath: String? = null,
        imagePath: String? = null
    ) {
        sessionTimerJob?.cancel()
        sessionTimerJob = null
        if (_uiState.value.isRecordingVoice) {
            stopVoiceRecording()
        }

        val current = _uiState.value
        val actualSeconds = current.elapsedSeconds
        val actualMinutes = maxOf(1, ceil(actualSeconds / 60.0).toInt())
        val lessonId = current.pendingPracticeItems.firstOrNull()?.id ?: "1"
        val taskTitle = current.upNextTitle
        val mainSkill = current.activeMainSkill
        val subcategoryId = current.activeSubcategoryName.lowercase().replace(" ", "_").replace("/", "_")

        val codeToSend = codeSnippet?.takeIf { it.isNotBlank() } ?: current.codeSnippetInput.takeIf { it.isNotBlank() }
        val transcriptToSend = transcript?.takeIf { it.isNotBlank() } ?: current.speechTranscriptInput.takeIf { it.isNotBlank() }
        val audioToSend = audioPath?.takeIf { it.isNotBlank() } ?: current.recordedAudioPath?.takeIf { it.isNotBlank() }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCompleting = true)

            val result = ApiClient.completeLesson(
                userId = UserSession.userId,
                lessonId = lessonId,
                taskTitle = taskTitle,
                durationSeconds = actualSeconds,
                durationMinutes = actualMinutes,
                xpPoints = 35,
                mainSkill = mainSkill,
                subcategoryId = subcategoryId,
                codeSnippet = codeToSend,
                transcript = transcriptToSend,
                quizAnswers = quizAnswers,
                audioPath = audioToSend,
                imagePath = imagePath
            )

            val scoreRes = result.data?.performanceScore?.let { ps ->
                PerformanceScoreResult(
                    score = ps.score,
                    scoreStatus = ps.scoreStatus,
                    scoreReason = ps.scoreReason,
                    strengths = ps.strengths,
                    weaknesses = ps.weaknesses,
                    aiFeedback = ps.aiFeedback,
                    metrics = ps.metrics,
                    assessmentType = ps.assessmentType,
                    taskTitle = ps.taskTitle.ifBlank { taskTitle },
                    durationMinutes = actualMinutes,
                    xpEarned = ps.xpEarned
                )
            }

            _uiState.value = _uiState.value.copy(
                isCompleting = false,
                isSessionActive = false,
                isSessionPaused = false,
                elapsedSeconds = 0,
                formattedElapsed = "00:00",
                lastSessionScoreResult = scoreRes,
                showSessionCompletionDialog = true,
                showEvaluationModal = false,
                showVocalReviewDialog = false,
                codeSnippetInput = "",
                speechTranscriptInput = "",
                recordedAudioPath = null,
                voiceRecordSeconds = 0,
                isPlayingVoice = false,
                completionCelebration = "🎉 Session completed in ${actualSeconds}s ($actualMinutes min)! +35 XP earned and recorded."
            )

            loadTraining()
        }
    }

    // Evaluation modal helpers
    fun openEvaluationModal() {
        _uiState.value = _uiState.value.copy(showEvaluationModal = true)
    }

    fun closeEvaluationModal() {
        stopVoicePlayback()
        if (_uiState.value.isAnalysisComplete) {
            finishEvaluationAndDismiss()
        } else {
            _uiState.value = _uiState.value.copy(showEvaluationModal = false)
        }
    }

    fun onCodeSnippetChange(code: String) {
        _uiState.value = _uiState.value.copy(codeSnippetInput = code, evaluationError = null)
    }

    fun onSpeechTranscriptChange(transcript: String) {
        _uiState.value = _uiState.value.copy(speechTranscriptInput = transcript, evaluationError = null)
    }

    fun onImageSelected(path: String?) {
        _uiState.value = _uiState.value.copy(selectedImagePath = path, evaluationError = null)
    }

    fun clearSelectedImage() {
        _uiState.value = _uiState.value.copy(selectedImagePath = null)
    }

    fun dismissCompletionDialog() {
        _uiState.value = _uiState.value.copy(showSessionCompletionDialog = false)
    }

    fun openItemDetail(item: PracticeItem) {
        _uiState.value = _uiState.value.copy(
            selectedItemForDetail = item,
            showItemDetailDialog = true
        )
    }

    fun closeItemDetail() {
        _uiState.value = _uiState.value.copy(
            selectedItemForDetail = null,
            showItemDetailDialog = false
        )
    }

    fun clearCelebration() {
        _uiState.value = _uiState.value.copy(completionCelebration = null)
    }

    fun onSwitchSubcategoryClick() {
        viewModelScope.launch {
            _eventChannel.send(DailyTrainingUiEvent.SwitchSubcategory)
        }
    }

    fun onCalendarClick() {
        viewModelScope.launch {
            _eventChannel.send(DailyTrainingUiEvent.OpenCalendar)
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _eventChannel.send(DailyTrainingUiEvent.NavigateBack)
        }
    }

    // ==========================================
    // Live Voice Recording (Music Vocal Practice)
    // ==========================================

    fun startVoiceRecording(context: Context) {
        try {
            stopVoicePlayback()
            val file = File(context.cacheDir, "training_voice_${System.currentTimeMillis()}.m4a")
            audioRecordingFile = file

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
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            mediaRecorder = recorder
            _uiState.value = _uiState.value.copy(
                isRecordingVoice = true,
                isRecordingPaused = false,
                voiceRecordSeconds = 0,
                audioRecordError = null,
                evaluationError = null,
                canRetryAnalysis = false,
                isAnalysisComplete = false,
                liveAmplitudeWaveforms = listOf(0.2f, 0.4f, 0.6f, 0.3f, 0.7f, 0.5f, 0.8f, 0.4f)
            )

            voiceTimerJob?.cancel()
            voiceTimerJob = viewModelScope.launch {
                var quarterTicks = 0
                while (_uiState.value.isRecordingVoice) {
                    delay(250L)
                    quarterTicks++
                    if (quarterTicks % 4 == 0 && !_uiState.value.isRecordingPaused) {
                        _uiState.value = _uiState.value.copy(
                            voiceRecordSeconds = _uiState.value.voiceRecordSeconds + 1
                        )
                    }
                    if (!_uiState.value.isRecordingPaused) {
                        val amp = try {
                            (mediaRecorder?.maxAmplitude ?: 0) / 32767f
                        } catch (_: Exception) {
                            0.25f
                        }
                        val oldList = _uiState.value.liveAmplitudeWaveforms
                        val newList = (oldList.drop(1) + listOf(amp.coerceIn(0.1f, 1.0f)))
                        _uiState.value = _uiState.value.copy(liveAmplitudeWaveforms = newList)
                    }
                }
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isRecordingVoice = false,
                audioRecordError = "Microphone error: ${e.message}. Please check audio permissions."
            )
        }
    }

    fun pauseVoiceRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                mediaRecorder?.pause()
                _uiState.value = _uiState.value.copy(isRecordingPaused = true)
            } catch (_: Exception) {}
        }
    }

    fun resumeVoiceRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                mediaRecorder?.resume()
                _uiState.value = _uiState.value.copy(isRecordingPaused = false)
            } catch (_: Exception) {}
        }
    }

    fun stopVoiceRecording() {
        try {
            voiceTimerJob?.cancel()
            mediaRecorder?.apply {
                try { stop() } catch (_: Exception) {}
                release()
            }
            mediaRecorder = null
            val file = audioRecordingFile
            if (file != null && file.exists() && file.length() >= 400) {
                _uiState.value = _uiState.value.copy(
                    isRecordingVoice = false,
                    isRecordingPaused = false,
                    recordedAudioPath = file.absolutePath,
                    audioRecordError = null,
                    evaluationError = null,
                    canRetryAnalysis = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isRecordingVoice = false,
                    isRecordingPaused = false,
                    recordedAudioPath = null,
                    audioRecordError = "Recording was too short or empty. Please sing clearly into your microphone."
                )
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isRecordingVoice = false,
                isRecordingPaused = false,
                audioRecordError = "Stop error: ${e.message}"
            )
        }
    }

    fun toggleVoicePlayback(context: Context) {
        val path = _uiState.value.recordedAudioPath ?: return
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            _uiState.value = _uiState.value.copy(isPlayingVoice = false)
        } else if (mediaPlayer != null) {
            mediaPlayer?.start()
            _uiState.value = _uiState.value.copy(isPlayingVoice = true)
        } else {
            try {
                val player = MediaPlayer().apply {
                    setDataSource(path)
                    prepare()
                    setOnCompletionListener {
                        _uiState.value = _uiState.value.copy(isPlayingVoice = false)
                    }
                    start()
                }
                mediaPlayer = player
                _uiState.value = _uiState.value.copy(isPlayingVoice = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(audioRecordError = "Playback error: ${e.message}")
            }
        }
    }

    fun stopVoicePlayback() {
        mediaPlayer?.apply {
            try { if (isPlaying) stop() } catch (_: Exception) {}
            release()
        }
        mediaPlayer = null
        _uiState.value = _uiState.value.copy(isPlayingVoice = false)
    }

    fun clearVoiceRecording() {
        stopVoicePlayback()
        audioRecordingFile?.delete()
        audioRecordingFile = null
        _uiState.value = _uiState.value.copy(
            isRecordingVoice = false,
            isRecordingPaused = false,
            recordedAudioPath = null,
            voiceRecordSeconds = 0,
            isPlayingVoice = false,
            audioRecordError = null,
            evaluationError = null,
            canRetryAnalysis = false,
            isAnalysisComplete = false,
            vocalAnalysisState = VocalAnalysisState.IDLE
        )
    }

    /**
     * Retake voice recording in review dialog
     */
    fun retakeVoiceRecording(context: Context) {
        stopVoicePlayback()
        clearVoiceRecording()
        _uiState.value = _uiState.value.copy(
            showVocalReviewDialog = false,
            isSessionActive = true,
            isSessionPaused = false
        )
        startVoiceRecording(context)
    }

    /**
     * Submit recorded singing voice for genuine AI Performance Evaluation (Requirements 1, 4-10)
     */
    fun submitSingingVoiceForAnalysis() {
        val current = _uiState.value
        if (current.isEvaluatingArtifact || current.vocalAnalysisState == VocalAnalysisState.ANALYSIS_PENDING) return

        val audioPath = current.recordedAudioPath
        if (audioPath.isNullOrBlank()) {
            _uiState.value = current.copy(
                vocalAnalysisState = VocalAnalysisState.ANALYSIS_FAILED,
                evaluationError = "No audio recording found. Please tap record to sing first.",
                canRetryAnalysis = false
            )
            return
        }

        val file = File(audioPath)
        if (!file.exists() || file.length() < 400) {
            val skillWord = if (current.isVocalSkill) "singing" else "${current.activeSubcategoryName.ifBlank { "instrument" }} practice"
            _uiState.value = current.copy(
                vocalAnalysisState = VocalAnalysisState.ANALYSIS_FAILED,
                evaluationError = "Recording is empty or too short. Please record at least 3 seconds of $skillWord.",
                canRetryAnalysis = false
            )
            return
        }

        val actualSeconds = current.elapsedSeconds
        val actualMinutes = maxOf(1, ceil(actualSeconds / 60.0).toInt())
        val lessonId = current.pendingPracticeItems.firstOrNull()?.id ?: "1"
        val taskTitle = current.upNextTitle
        val mainSkill = current.activeMainSkill
        val subcategoryId = current.activeSubcategoryName.lowercase().replace(" ", "_").replace("/", "_")

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isEvaluatingArtifact = true,
                vocalAnalysisState = VocalAnalysisState.ANALYSIS_PENDING,
                evaluationError = null,
                canRetryAnalysis = false
            )

            val result = ApiClient.completeLesson(
                userId = UserSession.userId,
                lessonId = lessonId,
                taskTitle = taskTitle,
                durationSeconds = actualSeconds,
                durationMinutes = actualMinutes,
                xpPoints = 35,
                mainSkill = mainSkill,
                subcategoryId = subcategoryId,
                audioPath = audioPath
            )

            if (result.isSuccess && result.data != null) {
                val scoreRes = result.data.performanceScore?.let { ps ->
                    PerformanceScoreResult(
                        score = ps.score,
                        scoreStatus = ps.scoreStatus,
                        scoreReason = ps.scoreReason,
                        strengths = ps.strengths,
                        weaknesses = ps.weaknesses,
                        aiFeedback = ps.aiFeedback,
                        metrics = ps.metrics,
                        assessmentType = ps.assessmentType,
                        taskTitle = ps.taskTitle.ifBlank { taskTitle },
                        durationMinutes = actualMinutes,
                        xpEarned = ps.xpEarned
                    )
                }

                if (scoreRes != null && scoreRes.scoreStatus == "available" && scoreRes.score != null) {
                    _uiState.value = _uiState.value.copy(
                        isEvaluatingArtifact = false,
                        vocalAnalysisState = VocalAnalysisState.ANALYSIS_SUCCESS,
                        lastSessionScoreResult = scoreRes,
                        isAnalysisComplete = true,
                        evaluationError = null,
                        canRetryAnalysis = false
                    )
                    // Refresh from database to immediately update "Completed Sessions" card with real score (Requirement 9)
                    loadTraining()
                } else {
                    val reason = scoreRes?.scoreReason
                        ?: scoreRes?.aiFeedback
                        ?: result.message
                        ?: "Performance score could not be calculated from this recording."
                    _uiState.value = _uiState.value.copy(
                        isEvaluatingArtifact = false,
                        vocalAnalysisState = VocalAnalysisState.ANALYSIS_FAILED,
                        evaluationError = reason,
                        canRetryAnalysis = true,
                        isAnalysisComplete = false,
                        lastSessionScoreResult = scoreRes
                    )
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    isEvaluatingArtifact = false,
                    vocalAnalysisState = VocalAnalysisState.ANALYSIS_FAILED,
                    evaluationError = result.message ?: "Failed to connect to AI evaluation backend. You can retry analysis with your recording.",
                    canRetryAnalysis = true,
                    isAnalysisComplete = false
                )
            }
        }
    }

    /**
     * Submit source code for AI compiler analysis and unit test assertion scoring
     */
    fun submitCodingPractice() {
        val current = _uiState.value
        val code = current.codeSnippetInput.ifBlank { current.codeEditorText }.trim()
        if (code.isBlank() || code.length < 10) {
            _uiState.value = current.copy(
                vocalAnalysisState = VocalAnalysisState.ANALYSIS_FAILED,
                evaluationError = "Please enter or paste your code solution (minimum 10 characters) to evaluate.",
                canRetryAnalysis = false
            )
            return
        }

        val actualSeconds = current.elapsedSeconds
        val actualMinutes = maxOf(1, ceil(actualSeconds / 60.0).toInt())
        val lessonId = current.pendingPracticeItems.firstOrNull()?.id ?: "1"
        val taskTitle = current.upNextTitle
        val mainSkill = current.activeMainSkill.ifBlank { "coding" }
        val subcategoryId = current.activeSubcategoryName.lowercase().replace(" ", "_").replace("/", "_")

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isEvaluatingArtifact = true,
                showEvaluationModal = true,
                vocalAnalysisState = VocalAnalysisState.ANALYSIS_PENDING,
                evaluationError = null,
                canRetryAnalysis = false
            )

            val result = ApiClient.completeLesson(
                userId = UserSession.userId,
                lessonId = lessonId,
                taskTitle = taskTitle,
                durationSeconds = actualSeconds,
                durationMinutes = actualMinutes,
                xpPoints = 35,
                mainSkill = mainSkill,
                subcategoryId = subcategoryId,
                codeSnippet = code
            )

            if (result.isSuccess && result.data != null) {
                val scoreRes = result.data.performanceScore?.let { ps ->
                    PerformanceScoreResult(
                        score = ps.score,
                        scoreStatus = ps.scoreStatus,
                        scoreReason = ps.scoreReason,
                        strengths = ps.strengths,
                        weaknesses = ps.weaknesses,
                        aiFeedback = ps.aiFeedback,
                        metrics = ps.metrics,
                        assessmentType = ps.assessmentType,
                        taskTitle = ps.taskTitle.ifBlank { taskTitle },
                        durationMinutes = actualMinutes,
                        xpEarned = ps.xpEarned
                    )
                }

                if (scoreRes != null && scoreRes.scoreStatus == "available" && scoreRes.score != null) {
                    _uiState.value = _uiState.value.copy(
                        isEvaluatingArtifact = false,
                        showEvaluationModal = true,
                        vocalAnalysisState = VocalAnalysisState.ANALYSIS_SUCCESS,
                        lastSessionScoreResult = scoreRes,
                        isAnalysisComplete = true,
                        evaluationError = null,
                        canRetryAnalysis = false
                    )
                    loadTraining()
                } else {
                    val reason = scoreRes?.scoreReason
                        ?: scoreRes?.aiFeedback
                        ?: result.message
                        ?: "Code performance score could not be calculated."
                    _uiState.value = _uiState.value.copy(
                        isEvaluatingArtifact = false,
                        showEvaluationModal = true,
                        vocalAnalysisState = VocalAnalysisState.ANALYSIS_FAILED,
                        evaluationError = reason,
                        canRetryAnalysis = true,
                        isAnalysisComplete = false,
                        lastSessionScoreResult = scoreRes
                    )
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    isEvaluatingArtifact = false,
                    showEvaluationModal = true,
                    vocalAnalysisState = VocalAnalysisState.ANALYSIS_FAILED,
                    evaluationError = result.message ?: "Failed to connect to AI evaluation backend. You can retry analysis with your code.",
                    canRetryAnalysis = true,
                    isAnalysisComplete = false
                )
            }
        }
    }

    /**
     * Submit artwork or photo for AI visual composition, lighting, and sharpness analysis
     */
    fun submitImagePractice(isDrawing: Boolean) {
        val current = _uiState.value
        val imagePath = current.capturedPhotoPath ?: current.selectedImagePath
        if (imagePath.isNullOrBlank()) {
            _uiState.value = current.copy(
                vocalAnalysisState = VocalAnalysisState.ANALYSIS_FAILED,
                evaluationError = "Please select or take a photo of your ${if (isDrawing) "drawing artwork" else "photograph"} first.",
                canRetryAnalysis = false
            )
            return
        }

        val file = File(imagePath)
        if (!file.exists() || file.length() < 200) {
            _uiState.value = current.copy(
                vocalAnalysisState = VocalAnalysisState.ANALYSIS_FAILED,
                evaluationError = "Image file is invalid or empty. Please select a valid picture file.",
                canRetryAnalysis = false
            )
            return
        }

        val actualSeconds = current.elapsedSeconds
        val actualMinutes = maxOf(1, ceil(actualSeconds / 60.0).toInt())
        val lessonId = current.pendingPracticeItems.firstOrNull()?.id ?: "1"
        val taskTitle = current.upNextTitle
        val mainSkill = if (isDrawing) "drawing" else "photography"
        val subcategoryId = current.activeSubcategoryName.lowercase().replace(" ", "_").replace("/", "_")

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isEvaluatingArtifact = true,
                showEvaluationModal = true,
                vocalAnalysisState = VocalAnalysisState.ANALYSIS_PENDING,
                evaluationError = null,
                canRetryAnalysis = false
            )

            val effectiveUserId = if (UserSession.userId > 0) UserSession.userId else 1
            val result = ApiClient.completeLesson(
                userId = effectiveUserId,
                lessonId = lessonId,
                taskTitle = taskTitle,
                durationSeconds = actualSeconds,
                durationMinutes = actualMinutes,
                xpPoints = 35,
                mainSkill = mainSkill,
                subcategoryId = subcategoryId,
                imagePath = imagePath
            )

            if (result.isSuccess && result.data != null) {
                val scoreRes = result.data.performanceScore?.let { ps ->
                    PerformanceScoreResult(
                        score = ps.score,
                        scoreStatus = ps.scoreStatus,
                        scoreReason = ps.scoreReason,
                        strengths = ps.strengths,
                        weaknesses = ps.weaknesses,
                        aiFeedback = ps.aiFeedback,
                        metrics = ps.metrics,
                        assessmentType = ps.assessmentType,
                        taskTitle = ps.taskTitle.ifBlank { taskTitle },
                        durationMinutes = actualMinutes,
                        xpEarned = ps.xpEarned
                    )
                }

                if (scoreRes != null && scoreRes.scoreStatus == "available" && scoreRes.score != null) {
                    _uiState.value = _uiState.value.copy(
                        isEvaluatingArtifact = false,
                        showEvaluationModal = true,
                        vocalAnalysisState = VocalAnalysisState.ANALYSIS_SUCCESS,
                        lastSessionScoreResult = scoreRes,
                        isAnalysisComplete = true,
                        evaluationError = null,
                        canRetryAnalysis = false
                    )
                    loadTraining()
                } else {
                    val reason = scoreRes?.scoreReason
                        ?: scoreRes?.aiFeedback
                        ?: result.message
                        ?: "Visual performance score could not be calculated."
                    _uiState.value = _uiState.value.copy(
                        isEvaluatingArtifact = false,
                        showEvaluationModal = true,
                        vocalAnalysisState = VocalAnalysisState.ANALYSIS_FAILED,
                        evaluationError = reason,
                        canRetryAnalysis = true,
                        isAnalysisComplete = false,
                        lastSessionScoreResult = scoreRes
                    )
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    isEvaluatingArtifact = false,
                    showEvaluationModal = true,
                    vocalAnalysisState = VocalAnalysisState.ANALYSIS_FAILED,
                    evaluationError = result.message ?: "Failed to connect to AI vision evaluation backend. You can retry analysis with your image.",
                    canRetryAnalysis = true,
                    isAnalysisComplete = false
                )
            }
        }
    }

    /**
     * Submit speech audio or transcript for AI pacing (WPM), clarity, and delivery analysis
     */
    fun submitSpeechPractice() {
        val current = _uiState.value
        val audioPath = current.recordedAudioPath
        val transcript = current.speechTranscriptInput.trim()

        if (audioPath.isNullOrBlank() && transcript.isBlank()) {
            _uiState.value = current.copy(
                vocalAnalysisState = VocalAnalysisState.ANALYSIS_FAILED,
                evaluationError = "Please record your speech or enter a speech transcript/notes to evaluate.",
                canRetryAnalysis = false
            )
            return
        }

        val actualSeconds = current.elapsedSeconds
        val actualMinutes = maxOf(1, ceil(actualSeconds / 60.0).toInt())
        val lessonId = current.pendingPracticeItems.firstOrNull()?.id ?: "1"
        val taskTitle = current.upNextTitle
        val mainSkill = "speaking"
        val subcategoryId = current.activeSubcategoryName.lowercase().replace(" ", "_").replace("/", "_")

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isEvaluatingArtifact = true,
                vocalAnalysisState = VocalAnalysisState.ANALYSIS_PENDING,
                evaluationError = null,
                canRetryAnalysis = false
            )

            val result = ApiClient.completeLesson(
                userId = UserSession.userId,
                lessonId = lessonId,
                taskTitle = taskTitle,
                durationSeconds = actualSeconds,
                durationMinutes = actualMinutes,
                xpPoints = 35,
                mainSkill = mainSkill,
                subcategoryId = subcategoryId,
                audioPath = audioPath,
                transcript = transcript.ifBlank { null }
            )

            if (result.isSuccess && result.data != null) {
                val scoreRes = result.data.performanceScore?.let { ps ->
                    PerformanceScoreResult(
                        score = ps.score,
                        scoreStatus = ps.scoreStatus,
                        scoreReason = ps.scoreReason,
                        strengths = ps.strengths,
                        weaknesses = ps.weaknesses,
                        aiFeedback = ps.aiFeedback,
                        metrics = ps.metrics,
                        assessmentType = ps.assessmentType,
                        taskTitle = ps.taskTitle.ifBlank { taskTitle },
                        durationMinutes = actualMinutes,
                        xpEarned = ps.xpEarned
                    )
                }

                if (scoreRes != null && scoreRes.scoreStatus == "available" && scoreRes.score != null) {
                    _uiState.value = _uiState.value.copy(
                        isEvaluatingArtifact = false,
                        vocalAnalysisState = VocalAnalysisState.ANALYSIS_SUCCESS,
                        lastSessionScoreResult = scoreRes,
                        isAnalysisComplete = true,
                        evaluationError = null,
                        canRetryAnalysis = false
                    )
                    loadTraining()
                } else {
                    val reason = scoreRes?.scoreReason
                        ?: scoreRes?.aiFeedback
                        ?: result.message
                        ?: "Speech performance score could not be calculated."
                    _uiState.value = _uiState.value.copy(
                        isEvaluatingArtifact = false,
                        vocalAnalysisState = VocalAnalysisState.ANALYSIS_FAILED,
                        evaluationError = reason,
                        canRetryAnalysis = true,
                        isAnalysisComplete = false,
                        lastSessionScoreResult = scoreRes
                    )
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    isEvaluatingArtifact = false,
                    vocalAnalysisState = VocalAnalysisState.ANALYSIS_FAILED,
                    evaluationError = result.message ?: "Failed to connect to AI speech evaluation backend. You can retry analysis.",
                    canRetryAnalysis = true,
                    isAnalysisComplete = false
                )
            }
        }
    }

    /**
     * Retry AI analysis for the active skill without asking user to re-record or re-upload
     */
    fun retryAnalysisForCurrentSkill() {
        val skill = _uiState.value.activeMainSkill.lowercase()
        when {
            skill.contains("coding") || skill.contains("prog") -> submitCodingPractice()
            skill.contains("draw") || skill.contains("art") -> submitImagePractice(isDrawing = true)
            skill.contains("photo") -> submitImagePractice(isDrawing = false)
            skill.contains("speak") -> submitSpeechPractice()
            else -> submitSingingVoiceForAnalysis()
        }
    }

    /**
     * Retry AI analysis using the existing recorded audio file without asking user to re-record (Requirement 10)
     */
    fun retryVoiceAnalysis() {
        submitSingingVoiceForAnalysis()
    }

    /**
     * Skip analysis and complete session, awarding XP without score (Requirement 11)
     */
    fun skipVocalAnalysisAndComplete() {
        stopVoicePlayback()
        _uiState.value = _uiState.value.copy(
            showVocalReviewDialog = false,
            vocalAnalysisState = VocalAnalysisState.IDLE
        )
        finishAndCompleteSession(audioPath = null)
    }

    /**
     * Dismiss vocal review dialog when complete
     */
    fun dismissVocalReview() {
        val current = _uiState.value
        val score = current.lastSessionScoreResult?.score
        val skillName = if (current.isVocalSkill) "Vocal" else current.activeSubcategoryName.ifBlank { "Instrument" }
        val celebration = if (score != null) {
            "🎉 $skillName practice evaluated! Score: $score/100 · +35 XP earned!"
        } else {
            "🎉 $skillName practice recorded! +35 XP earned."
        }

        stopVoicePlayback()
        _uiState.value = current.copy(
            showVocalReviewDialog = false,
            vocalAnalysisState = VocalAnalysisState.IDLE,
            isAnalysisComplete = false,
            isEvaluatingArtifact = false,
            evaluationError = null,
            canRetryAnalysis = false,
            recordedAudioPath = null,
            voiceRecordSeconds = 0,
            elapsedSeconds = 0,
            formattedElapsed = "00:00",
            completionCelebration = celebration
        )
        loadTraining()
    }

    /**
     * Finalizes practice session once evaluation modal is done
     */
    fun finishEvaluationAndDismiss() {
        val current = _uiState.value
        val score = current.lastSessionScoreResult?.score
        val celebration = if (score != null) {
            "🎉 Practice evaluated & recorded! Score: $score/100 · +35 XP earned!"
        } else {
            "🎉 Practice session completed! +35 XP earned and recorded."
        }

        stopVoicePlayback()
        sessionTimerJob?.cancel()
        sessionTimerJob = null

        _uiState.value = current.copy(
            showEvaluationModal = false,
            showSessionCompletionDialog = false,
            showVocalReviewDialog = false,
            isSessionActive = false,
            isSessionPaused = false,
            elapsedSeconds = 0,
            formattedElapsed = "00:00",
            isAnalysisComplete = false,
            isEvaluatingArtifact = false,
            evaluationError = null,
            canRetryAnalysis = false,
            recordedAudioPath = null,
            voiceRecordSeconds = 0,
            codeSnippetInput = "",
            speechTranscriptInput = "",
            selectedImagePath = null,
            capturedPhotoPath = null,
            codeMistake = null,
            codingOutput = "",
            codingError = "",
            completionCelebration = celebration
        )

        loadTraining()
    }

    // ==========================================
    // 1. Photography Practice Methods
    // ==========================================
    fun onPhotoCaptured(path: String) {
        _uiState.value = _uiState.value.copy(
            capturedPhotoPath = path,
            selectedImagePath = path,
            evaluationError = null
        )
    }

    fun retakePhoto() {
        _uiState.value = _uiState.value.copy(
            capturedPhotoPath = null,
            selectedImagePath = null,
            evaluationError = null
        )
    }

    fun submitCapturedPhoto() {
        val path = _uiState.value.capturedPhotoPath ?: _uiState.value.selectedImagePath
        if (path.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                evaluationError = "Please capture or select a photo first."
            )
            return
        }
        _uiState.value = _uiState.value.copy(
            capturedPhotoPath = path,
            selectedImagePath = path
        )
        submitImagePractice(isDrawing = false)
    }

    // ==========================================
    // 2. Drawing Practice Methods
    // ==========================================
    fun startDrawingStroke(x: Float, y: Float) {
        val point = DrawingStrokePoint(
            x = x,
            y = y,
            isEndOfStroke = false,
            color = if (_uiState.value.selectedDrawingTool == "ERASER") 0xFFFFFFFFL else _uiState.value.selectedDrawingColor,
            strokeWidth = if (_uiState.value.selectedDrawingTool == "BRUSH") _uiState.value.drawingStrokeWidth * 2f else _uiState.value.drawingStrokeWidth
        )
        _uiState.value = _uiState.value.copy(
            drawingStrokes = _uiState.value.drawingStrokes + point
        )
    }

    fun addDrawingStrokePoint(x: Float, y: Float) {
        val point = DrawingStrokePoint(
            x = x,
            y = y,
            isEndOfStroke = false,
            color = if (_uiState.value.selectedDrawingTool == "ERASER") 0xFFFFFFFFL else _uiState.value.selectedDrawingColor,
            strokeWidth = if (_uiState.value.selectedDrawingTool == "BRUSH") _uiState.value.drawingStrokeWidth * 2f else _uiState.value.drawingStrokeWidth
        )
        _uiState.value = _uiState.value.copy(
            drawingStrokes = _uiState.value.drawingStrokes + point
        )
    }

    fun endDrawingStroke() {
        val lastPoint = _uiState.value.drawingStrokes.lastOrNull() ?: return
        val endPoint = lastPoint.copy(isEndOfStroke = true)
        _uiState.value = _uiState.value.copy(
            drawingStrokes = _uiState.value.drawingStrokes.dropLast(1) + endPoint
        )
    }

    fun clearDrawing() {
        _uiState.value = _uiState.value.copy(
            drawingStrokes = emptyList(),
            savedDrawingPath = null,
            selectedImagePath = null,
            evaluationError = null
        )
    }

    fun undoDrawing() {
        val strokes = _uiState.value.drawingStrokes
        if (strokes.isEmpty()) return
        val lastEndIdx = strokes.indexOfLast { it.isEndOfStroke }
        if (lastEndIdx <= 0) {
            _uiState.value = _uiState.value.copy(drawingStrokes = emptyList())
        } else {
            val secondLastEndIdx = strokes.subList(0, lastEndIdx).indexOfLast { it.isEndOfStroke }
            val newStrokes = if (secondLastEndIdx >= 0) strokes.subList(0, secondLastEndIdx + 1) else emptyList()
            _uiState.value = _uiState.value.copy(drawingStrokes = newStrokes)
        }
    }

    fun selectDrawingTool(tool: String) {
        _uiState.value = _uiState.value.copy(selectedDrawingTool = tool)
    }

    fun selectDrawingColor(color: Long) {
        _uiState.value = _uiState.value.copy(selectedDrawingColor = color)
    }

    fun setDrawingStrokeWidth(width: Float) {
        _uiState.value = _uiState.value.copy(drawingStrokeWidth = width)
    }

    fun submitDrawingArt(context: Context, imageBytes: ByteArray? = null, filePath: String? = null) {
        val pathToUse = when {
            !filePath.isNullOrBlank() -> filePath
            imageBytes != null -> {
                val f = File(context.cacheDir, "drawing_${System.currentTimeMillis()}.jpg")
                f.writeBytes(imageBytes)
                f.absolutePath
            }
            else -> _uiState.value.selectedImagePath
        }

        if (pathToUse.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                evaluationError = "Canvas is empty. Draw on screen or upload a sketch photo first."
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            savedDrawingPath = pathToUse,
            selectedImagePath = pathToUse
        )
        submitImagePractice(isDrawing = true)
    }

    // ==========================================
    // 3. Coding Practice Methods
    // ==========================================
    fun onLanguageSelected(lang: String) {
        val starter = getStarterCodeForLanguage(lang, _uiState.value.activeSubcategoryName)
        _uiState.value = _uiState.value.copy(
            selectedLanguage = lang,
            codeEditorText = starter,
            codeSnippetInput = starter,
            codingOutput = "",
            codingError = "",
            codeTestCases = emptyList(),
            codeExplanation = "",
            codeMistake = null
        )
    }

    fun onCodeTextChanged(newCode: String) {
        _uiState.value = _uiState.value.copy(
            codeEditorText = newCode,
            codeSnippetInput = newCode,
            evaluationError = null,
            codingError = "",
            codeMistake = null
        )
    }

    fun runCode(userId: Int) {
        val code = _uiState.value.codeEditorText
        val lang = _uiState.value.selectedLanguage
        val task = _uiState.value.upNextTitle

        if (code.isBlank()) {
            _uiState.value = _uiState.value.copy(codingError = "Code cannot be empty.")
            return
        }

        val effectiveUserId = if (userId > 0) userId else (if (UserSession.userId > 0) UserSession.userId else 1)

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCodeRunning = true, codingError = "", codingOutput = "", codeMistake = null)
            val result = ApiClient.executeCode(effectiveUserId, lang, code, task)
            _uiState.value = _uiState.value.copy(isCodeRunning = false)
            if (result.isSuccess && result.data != null) {
                val d = result.data
                _uiState.value = _uiState.value.copy(
                    codingOutput = d.output,
                    codingError = d.error,
                    codeTestCases = d.testCases,
                    codeExplanation = d.explanation,
                    codeMistake = d.mistake
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    codingError = result.message ?: "Failed to execute code",
                    codeMistake = null
                )
            }
        }
    }

    // ==========================================
    // 4. Public Speaking Practice Methods
    // ==========================================
    fun startSpeechRecording(context: Context) {
        try {
            stopVoicePlayback()
            val file = File(context.cacheDir, "speech_practice_${System.currentTimeMillis()}.m4a")
            audioRecordingFile = file

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
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            mediaRecorder = recorder
            _uiState.value = _uiState.value.copy(
                isRecordingSpeech = true,
                speechRecordSeconds = 0,
                recordedSpeechAudioPath = null,
                recordedAudioPath = null,
                evaluationError = null
            )

            voiceTimerJob?.cancel()
            voiceTimerJob = viewModelScope.launch {
                while (_uiState.value.isRecordingSpeech) {
                    delay(1000L)
                    _uiState.value = _uiState.value.copy(
                        speechRecordSeconds = _uiState.value.speechRecordSeconds + 1
                    )
                }
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isRecordingSpeech = false,
                evaluationError = "Microphone error: ${e.message}"
            )
        }
    }

    fun stopSpeechRecording() {
        try {
            voiceTimerJob?.cancel()
            mediaRecorder?.apply {
                try { stop() } catch (_: Exception) {}
                release()
            }
            mediaRecorder = null
            val file = audioRecordingFile
            if (file != null && file.exists() && file.length() >= 400) {
                _uiState.value = _uiState.value.copy(
                    isRecordingSpeech = false,
                    recordedSpeechAudioPath = file.absolutePath,
                    recordedAudioPath = file.absolutePath,
                    evaluationError = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isRecordingSpeech = false,
                    recordedSpeechAudioPath = null,
                    evaluationError = "Recording was too short. Speak audibly into microphone."
                )
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isRecordingSpeech = false,
                evaluationError = "Failed to stop recording: ${e.message}"
            )
        }
    }

    fun retakeSpeechRecording(context: Context) {
        stopVoicePlayback()
        audioRecordingFile?.delete()
        audioRecordingFile = null
        _uiState.value = _uiState.value.copy(
            isRecordingSpeech = false,
            speechRecordSeconds = 0,
            recordedSpeechAudioPath = null,
            recordedAudioPath = null,
            evaluationError = null
        )
        startSpeechRecording(context)
    }

    // ==========================================
    // 5. Music Instrument Metronome Methods
    // ==========================================
    private var metronomeJob: Job? = null

    fun toggleMetronome() {
        val current = _uiState.value
        if (current.isMetronomeActive) {
            metronomeJob?.cancel()
            metronomeJob = null
            _uiState.value = current.copy(isMetronomeActive = false, currentBeat = 1)
        } else {
            _uiState.value = current.copy(isMetronomeActive = true, currentBeat = 1)
            metronomeJob = viewModelScope.launch {
                while (_uiState.value.isMetronomeActive) {
                    val bpm = _uiState.value.metronomeBpm.coerceIn(40, 240)
                    val intervalMs = (60_000L / bpm)
                    delay(intervalMs)
                    val nextBeat = (_uiState.value.currentBeat % 4) + 1
                    _uiState.value = _uiState.value.copy(currentBeat = nextBeat)
                }
            }
        }
    }

    fun setMetronomeBpm(bpm: Int) {
        _uiState.value = _uiState.value.copy(metronomeBpm = bpm.coerceIn(40, 240))
    }

    override fun onCleared() {
        super.onCleared()
        sessionTimerJob?.cancel()
        voiceTimerJob?.cancel()
        metronomeJob?.cancel()
        stopVoicePlayback()
        mediaRecorder?.release()
    }
}

fun getStarterCodeForLanguage(lang: String, subcat: String): String {
    return when (lang.lowercase()) {
        "python" -> """# Python Practice Solution
def solution():
    # Write your solution here
    print("Code running successfully!")
    return True

solution()
"""
        "java" -> """// Java Practice Solution
public class Main {
    public static void main(String[] args) {
        System.out.println("Code running successfully!");
    }
}
"""
        "javascript" -> """// JavaScript Practice Solution
function solution() {
    console.log("Code running successfully!");
    return true;
}

solution();
"""
        "c" -> """// C Practice Solution
#include <stdio.h>

int main() {
    printf("Code running successfully!\n");
    return 0;
}
"""
        "cpp" -> """// C++ Practice Solution
#include <iostream>

int main() {
    std::cout << "Code running successfully!" << std::endl;
    return 0;
}
"""
        else -> """# Practice Solution
print("Code running successfully!")
"""
    }
}
