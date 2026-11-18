package com.simats.aiskillmirror.ui.roadmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.aiskillmirror.data.network.RoadmapStageData
import com.simats.aiskillmirror.data.network.UserSession
import com.simats.aiskillmirror.data.repository.ProgressRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class GrowthRoadmapViewModel(
    private val progressRepository: ProgressRepository = ProgressRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(GrowthRoadmapUiState())
    val uiState: StateFlow<GrowthRoadmapUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<GrowthRoadmapUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        loadRoadmap()
    }

    fun loadRoadmap(subcategoryId: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, statusMessage = null)
            val result = progressRepository.getRoadmap(UserSession.userId, subcategoryId)
            if (result.isSuccess && result.data != null) {
                val d = result.data

                // Determine active stage (first unlocked & incomplete stage)
                val activeSt = d.stages.firstOrNull { it.isUnlocked && !it.isCompleted }
                    ?: d.stages.lastOrNull()

                val legacySteps = d.upcomingSteps.map {
                    val icon = when (it.iconType.uppercase()) {
                        "INTERVIEW" -> RoadmapIconType.INTERVIEW
                        "SPEECH" -> RoadmapIconType.SPEECH
                        else -> RoadmapIconType.LOGIC
                    }
                    UpcomingStepItem(
                        id = it.id,
                        title = it.title,
                        subtitle = it.subtitle,
                        lessonCountText = it.lessonCountText,
                        iconType = icon
                    )
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    activeSubcategory = d.activeSubcategory,
                    availableSubcategories = d.availableSubcategories,
                    stages = d.stages,
                    activeStage = activeSt,
                    completedStagesCount = d.completedCount,
                    totalStagesCount = d.totalCount,
                    roadmapProgressFraction = d.roadmapProgress.toFloat(),
                    roadmapProgressPercentage = d.roadmapProgressPercentage,
                    roadmapProgressText = d.roadmapProgressText,
                    totalPracticeMinutes = d.totalPracticeMinutes,
                    aiFeedback = d.aiFeedback,

                    // Legacy mappings
                    currentWeekBadgeText = d.currentWeekBadgeText,
                    planTitle = d.planTitle,
                    planSubtitle = d.planSubtitle,
                    planProgress = d.planProgress.toFloat(),
                    planProgressText = d.planProgressText,
                    activeStepTitle = d.activeStepTitle,
                    activeStepSubtitle = d.activeStepSubtitle,
                    activeStepProgressPercentage = d.activeStepProgressPercentage,
                    activeStepDescription = d.activeStepDescription,
                    activeStepTags = d.activeStepTags,
                    upcomingSteps = legacySteps,
                    continueButtonText = d.continueButtonText
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    statusMessage = result.message ?: "Failed to load roadmap"
                )
            }
        }
    }

    fun switchSubcategory(subcategoryId: String) {
        loadRoadmap(subcategoryId)
    }

    fun openStageQuiz(stage: RoadmapStageData) {
        _uiState.value = _uiState.value.copy(
            isQuizDialogOpen = true,
            quizStage = stage,
            currentQuestionIndex = 0,
            isReviewMode = false,
            quizAnswers = emptyMap(),
            quizResult = null,
            quizError = null,
            isSubmittingQuiz = false
        )
    }

    fun selectQuizAnswer(questionIndex: Int, optionIndex: Int) {
        val updated = _uiState.value.quizAnswers.toMutableMap()
        updated[questionIndex] = optionIndex
        _uiState.value = _uiState.value.copy(
            quizAnswers = updated,
            quizError = null
        )
    }

    fun nextQuestion() {
        val stage = _uiState.value.quizStage ?: return
        val total = stage.assessmentQuestions.size
        val cur = _uiState.value.currentQuestionIndex
        if (cur < total - 1) {
            _uiState.value = _uiState.value.copy(
                currentQuestionIndex = cur + 1,
                quizError = null
            )
        }
    }

    fun previousQuestion() {
        val cur = _uiState.value.currentQuestionIndex
        if (cur > 0) {
            _uiState.value = _uiState.value.copy(
                currentQuestionIndex = cur - 1,
                quizError = null
            )
        }
    }

    fun jumpToQuestion(index: Int) {
        val stage = _uiState.value.quizStage ?: return
        if (index in stage.assessmentQuestions.indices) {
            _uiState.value = _uiState.value.copy(
                currentQuestionIndex = index,
                quizError = null
            )
        }
    }

    fun openReviewMode() {
        _uiState.value = _uiState.value.copy(isReviewMode = true)
    }

    fun closeReviewMode() {
        _uiState.value = _uiState.value.copy(isReviewMode = false)
    }

    fun retakeQuiz() {
        _uiState.value = _uiState.value.copy(
            currentQuestionIndex = 0,
            isReviewMode = false,
            quizAnswers = emptyMap(),
            quizResult = null,
            quizError = null,
            isSubmittingQuiz = false
        )
    }

    fun closeQuizDialog() {
        _uiState.value = _uiState.value.copy(
            isQuizDialogOpen = false,
            quizStage = null,
            currentQuestionIndex = 0,
            isReviewMode = false,
            quizAnswers = emptyMap(),
            quizResult = null,
            quizError = null,
            isSubmittingQuiz = false
        )
    }

    fun submitStageQuiz() {
        val stage = _uiState.value.quizStage ?: return
        val questions = stage.assessmentQuestions

        if (questions.isEmpty()) {
            _uiState.value = _uiState.value.copy(quizError = "No assessment questions found for this stage.")
            return
        }

        if (_uiState.value.quizAnswers.size < questions.size) {
            val unansweredIndex = (0 until questions.size).firstOrNull { !_uiState.value.quizAnswers.containsKey(it) } ?: 0
            _uiState.value = _uiState.value.copy(
                currentQuestionIndex = unansweredIndex,
                quizError = "Please answer Question ${unansweredIndex + 1} before submitting."
            )
            return
        }

        var correctCount = 0
        questions.forEachIndexed { index, q ->
            val userChoice = _uiState.value.quizAnswers[index]
            if (userChoice != null && userChoice == q.correct) {
                correctCount++
            }
        }

        val calculatedScore = ((correctCount.toDouble() / questions.size) * 100).toInt()

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmittingQuiz = true, quizError = null)

            val result = progressRepository.completeRoadmapTopic(
                userId = UserSession.userId,
                stageId = stage.id,
                score = calculatedScore,
                answers = _uiState.value.quizAnswers
            )

            _uiState.value = _uiState.value.copy(isSubmittingQuiz = false)

            if (result.isSuccess && result.data != null) {
                val data = result.data
                _uiState.value = _uiState.value.copy(quizResult = data)

                if (data.passed) {
                    _eventChannel.send(GrowthRoadmapUiEvent.ShowToast("Stage cleared! +${data.xpAwarded} XP awarded."))
                    // Reload roadmap to reflect unlocked stages and updated level
                    loadRoadmap(_uiState.value.activeSubcategory?.subcategoryId)
                } else {
                    _eventChannel.send(GrowthRoadmapUiEvent.ShowToast("Score: ${data.score}%. Minimum 70% required to unlock next stage."))
                    // Reload roadmap to refresh remedial feedback
                    loadRoadmap(_uiState.value.activeSubcategory?.subcategoryId)
                }
            } else {
                _uiState.value = _uiState.value.copy(quizError = result.message ?: "Failed to submit assessment")
            }
        }
    }

    fun toggleTask(stageId: Int, taskId: String, isDone: Boolean) {
        val updatedStages = _uiState.value.stages.map { stage ->
            if (stage.id == stageId) {
                val updatedTasks = stage.practiceTasks.map { task ->
                    if (task.id == taskId) task.copy(isDone = isDone) else task
                }
                stage.copy(practiceTasks = updatedTasks)
            } else {
                stage
            }
        }
        _uiState.value = _uiState.value.copy(stages = updatedStages)

        viewModelScope.launch {
            progressRepository.toggleRoadmapTask(UserSession.userId, stageId, taskId, isDone)
        }
    }



    fun startStagePractice(stage: RoadmapStageData) {
        viewModelScope.launch {
            _eventChannel.send(GrowthRoadmapUiEvent.StartPracticeStage(stage))
        }
    }

    fun onContinueActiveWeekClick() {
        val currentStage = _uiState.value.activeStage
        if (currentStage != null) {
            startStagePractice(currentStage)
        } else {
            viewModelScope.launch {
                _eventChannel.send(GrowthRoadmapUiEvent.ContinueActiveWeek)
            }
        }
    }

    fun onFilterClick() {
        viewModelScope.launch {
            _eventChannel.send(GrowthRoadmapUiEvent.OpenFilterSettings)
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _eventChannel.send(GrowthRoadmapUiEvent.NavigateBack)
        }
    }
}
