package com.simats.aiskillmirror.ui.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.aiskillmirror.data.network.ApiClient
import com.simats.aiskillmirror.data.network.UserSession
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DailyTrainingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DailyTrainingUiState())
    val uiState: StateFlow<DailyTrainingUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<DailyTrainingUiEvent>()
    val events = _eventChannel.receiveAsFlow()

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
                        isCompleted = it.isCompleted
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
                    subcatXp = sub?.xpEarned ?: _uiState.value.subcatXp,
                    subcatLessonsCount = sub?.lessonsCompleted ?: _uiState.value.subcatLessonsCount,
                    subcatMinutes = sub?.practiceMinutes ?: _uiState.value.subcatMinutes,
                    stepByStepInstructions = if (d.stepByStepInstructions.isNotEmpty()) d.stepByStepInstructions else _uiState.value.stepByStepInstructions,
                    beginnerExercises = if (d.beginnerExercises.isNotEmpty()) d.beginnerExercises else _uiState.value.beginnerExercises,
                    practiceChallenges = if (d.practiceChallenges.isNotEmpty()) d.practiceChallenges else _uiState.value.practiceChallenges,
                    weeklyLearningPlan = if (weekly.isNotEmpty()) weekly else _uiState.value.weeklyLearningPlan,
                    aiTip = if (d.aiTip.isNotBlank()) d.aiTip else _uiState.value.aiTip
                )
            }
        }
    }

    fun completeCurrentLesson(lessonId: String = "1", durationMinutes: Int = 10, xp: Int = 35) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCompleting = true)
            val result = ApiClient.completeLesson(
                userId = UserSession.userId,
                lessonId = lessonId,
                durationMinutes = durationMinutes,
                xpPoints = xp
            )
            _uiState.value = _uiState.value.copy(
                isCompleting = false,
                completionCelebration = "Great job! +$xp XP earned & session recorded."
            )
            // Reload training stats
            loadTraining()
        }
    }

    fun clearCelebration() {
        _uiState.value = _uiState.value.copy(completionCelebration = null)
    }

    fun onStartPracticeClick() {
        // Complete practice lesson and trigger event
        completeCurrentLesson(
            lessonId = "1",
            durationMinutes = 10,
            xp = 35
        )
        viewModelScope.launch {
            _eventChannel.send(DailyTrainingUiEvent.StartPracticeSession)
        }
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
}
