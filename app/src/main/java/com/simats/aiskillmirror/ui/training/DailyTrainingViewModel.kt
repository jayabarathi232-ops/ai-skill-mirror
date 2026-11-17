package com.simats.aiskillmirror.ui.training

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
import kotlin.math.ceil

class DailyTrainingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DailyTrainingUiState())
    val uiState: StateFlow<DailyTrainingUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<DailyTrainingUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    private var sessionTimerJob: Job? = null

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
                audioPath = audioPath,
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
        _uiState.value = _uiState.value.copy(showEvaluationModal = false)
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

    override fun onCleared() {
        super.onCleared()
        sessionTimerJob?.cancel()
    }
}
