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

    fun loadTraining(subcategoryId: String? = null) {
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
                        aiFeedback = it.aiFeedback
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
                _uiState.value = _uiState.value.copy(
                    headerDateText = d.headerDateText,
                    streakBadgeText = d.streakBadgeText,
                    currentMinutes = d.currentMinutes,
                    targetMinutes = d.targetMinutes,
                    totalXp = d.totalXp,
                    practiceProgressText = d.practiceProgressText,
                    completedPracticeItems = completed,
                    pendingPracticeItems = pending,
                    upNextTitle = d.upNextTitle,
                    upNextCategory = d.upNextCategory,
                    upNextMetaText = d.upNextMetaText,
                    upNextDescription = d.upNextDescription,
                    startButtonText = d.startButtonText,
                    activeSubcategoryName = sub?.subcategoryName ?: _uiState.value.activeSubcategoryName,
                    activeMainSkill = sub?.mainSkill ?: _uiState.value.activeMainSkill,
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

    fun startPracticeSession() {
        sessionTimerJob?.cancel()
        _uiState.value = _uiState.value.copy(
            isSessionActive = true,
            isSessionPaused = false,
            elapsedSeconds = 0,
            formattedElapsed = "00:00",
            completionCelebration = null,
            lastSessionScoreResult = null,
            showSessionCompletionDialog = false
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
    }

    fun pausePracticeSession() {
        _uiState.value = _uiState.value.copy(isSessionPaused = true)
    }

    fun resumePracticeSession() {
        _uiState.value = _uiState.value.copy(isSessionPaused = false)
    }

    fun cancelPracticeSession() {
        sessionTimerJob?.cancel()
        sessionTimerJob = null
        _uiState.value = _uiState.value.copy(
            isSessionActive = false,
            isSessionPaused = false,
            elapsedSeconds = 0,
            formattedElapsed = "00:00"
        )
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

        val current = _uiState.value
        val actualSeconds = current.elapsedSeconds
        // Count actual minutes: at least 1 min for completed session
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

            // Submit actual recorded duration & optional evaluation artifact to backend database
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
                codeSnippetInput = "",
                speechTranscriptInput = "",
                recordedAudioPath = null,
                voiceRecordSeconds = 0,
                isPlayingVoice = false,
                completionCelebration = "🎉 Session completed in ${actualSeconds}s ($actualMinutes min)! +35 XP earned and recorded."
            )

            // Refresh from database to load real saved metrics
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
        _uiState.value = _uiState.value.copy(codeSnippetInput = code)
    }

    fun onSpeechTranscriptChange(transcript: String) {
        _uiState.value = _uiState.value.copy(speechTranscriptInput = transcript)
    }

    fun dismissCompletionDialog() {
        _uiState.value = _uiState.value.copy(showSessionCompletionDialog = false)
    }

    fun openItemDetail(item: PracticeItem) {
        _uiState.value = _uiState.value.copy(selectedItemForDetail = item)
    }

    fun closeItemDetail() {
        _uiState.value = _uiState.value.copy(selectedItemForDetail = null)
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

    // Microphone Voice Recording for Singing / Practice (Requirement 4)
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
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            mediaRecorder = recorder
            _uiState.value = _uiState.value.copy(
                isRecordingVoice = true,
                voiceRecordSeconds = 0,
                audioRecordError = null,
                evaluationError = null,
                canRetryAnalysis = false,
                isAnalysisComplete = false
            )
            voiceTimerJob?.cancel()
            voiceTimerJob = viewModelScope.launch {
                while (_uiState.value.isRecordingVoice) {
                    delay(1000)
                    _uiState.value = _uiState.value.copy(
                        voiceRecordSeconds = _uiState.value.voiceRecordSeconds + 1
                    )
                }
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(audioRecordError = "Microphone error: ${e.message}. Please check audio permissions.")
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
                    recordedAudioPath = file.absolutePath,
                    audioRecordError = null,
                    evaluationError = null,
                    canRetryAnalysis = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isRecordingVoice = false,
                    recordedAudioPath = null,
                    audioRecordError = "Recording was too short or empty. Please sing clearly into your microphone."
                )
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isRecordingVoice = false,
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
            recordedAudioPath = null,
            voiceRecordSeconds = 0,
            isPlayingVoice = false,
            audioRecordError = null,
            evaluationError = null,
            canRetryAnalysis = false,
            isAnalysisComplete = false
        )
    }

    /**
     * Submit recorded singing voice for genuine AI Performance Evaluation (Requirements 1, 5, 6, 10, 11, 13)
     * - Evaluates the recording only ONCE
     * - Shows loading spinner while evaluating
     * - Prevents duplicate clicks while processing
     * - Saves result to database
     * - Leaves the audio path intact so the user does NOT have to re-record if a network retry is needed
     */
    fun submitSingingVoiceForAnalysis() {
        val current = _uiState.value
        if (current.isEvaluatingArtifact) return // Prevent duplicate submissions

        val audioPath = current.recordedAudioPath
        if (audioPath.isNullOrBlank()) {
            _uiState.value = current.copy(
                evaluationError = "No audio recording found. Please tap record to sing first.",
                canRetryAnalysis = false
            )
            return
        }

        val file = File(audioPath)
        if (!file.exists() || file.length() < 400) {
            _uiState.value = current.copy(
                evaluationError = "Recording is empty or too short. Please record at least 3 seconds of singing.",
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
                        lastSessionScoreResult = scoreRes,
                        isAnalysisComplete = true,
                        evaluationError = null,
                        canRetryAnalysis = false
                    )
                } else {
                    val reason = scoreRes?.scoreReason
                        ?: scoreRes?.aiFeedback
                        ?: result.message
                        ?: "Performance score could not be calculated from this recording."
                    _uiState.value = _uiState.value.copy(
                        isEvaluatingArtifact = false,
                        evaluationError = reason,
                        canRetryAnalysis = true,
                        isAnalysisComplete = false,
                        lastSessionScoreResult = scoreRes
                    )
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    isEvaluatingArtifact = false,
                    evaluationError = result.message ?: "Failed to connect to AI evaluation backend. You can retry without recording again.",
                    canRetryAnalysis = true,
                    isAnalysisComplete = false
                )
            }
        }
    }

    /**
     * Retry AI analysis using the existing recorded audio file without asking user to re-record (Requirement 12)
     */
    fun retryVoiceAnalysis() {
        submitSingingVoiceForAnalysis()
    }

    /**
     * Finalizes practice session once evaluation is verified and user clicks Done (Requirement 14)
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
            completionCelebration = celebration
        )

        loadTraining()
    }

    override fun onCleared() {
        super.onCleared()
        sessionTimerJob?.cancel()
        voiceTimerJob?.cancel()
        stopVoicePlayback()
        mediaRecorder?.release()
    }
}
