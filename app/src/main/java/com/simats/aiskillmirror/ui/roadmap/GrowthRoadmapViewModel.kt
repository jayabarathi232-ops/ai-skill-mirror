package com.simats.aiskillmirror.ui.roadmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun loadRoadmap() {
        viewModelScope.launch {
            val result = progressRepository.getRoadmap(UserSession.userId)
            if (result.isSuccess && result.data != null) {
                val d = result.data
                val steps = d.upcomingSteps.map {
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
                    upcomingSteps = steps,
                    continueButtonText = d.continueButtonText
                )
            }
        }
    }

    fun onContinueActiveWeekClick() {
        viewModelScope.launch {
            _eventChannel.send(GrowthRoadmapUiEvent.ContinueActiveWeek)
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
