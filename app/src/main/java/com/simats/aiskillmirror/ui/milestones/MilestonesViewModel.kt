package com.simats.aiskillmirror.ui.milestones

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

class MilestonesViewModel(
    private val progressRepository: ProgressRepository = ProgressRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MilestonesUiState())
    val uiState: StateFlow<MilestonesUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<MilestonesUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        loadMilestones()
    }

    fun loadMilestones() {
        viewModelScope.launch {
            val result = progressRepository.getMilestones(UserSession.userId)
            if (result.isSuccess && result.data != null) {
                val d = result.data
                val achs = d.achievements.map {
                    val icon = when (it.iconType.uppercase()) {
                        "WARRIOR" -> AchievementIconType.WARRIOR
                        "VOICE" -> AchievementIconType.VOICE
                        else -> AchievementIconType.REFLECTION
                    }
                    AchievementItem(it.id, it.title, it.earnedDateText, icon)
                }
                _uiState.value = _uiState.value.copy(
                    mainGoalTitle = d.mainGoalTitle,
                    currentScore = d.currentScore,
                    targetScore = d.targetScore,
                    statusText = d.statusText,
                    targetDateText = d.targetDateText,
                    streakTitle = d.streakTitle,
                    streakSubtitle = d.streakSubtitle,
                    streakProgressText = d.streakProgressText,
                    earnedCountText = d.earnedCountText,
                    achievements = achs,
                    nextRewardTitle = d.nextRewardTitle,
                    nextRewardSubtitle = d.nextRewardSubtitle,
                    nextRewardTarget = d.nextRewardTarget,
                    totalXpText = d.totalXpText
                )
            }
        }
    }

    fun onAddGoalClick() {
        viewModelScope.launch {
            _eventChannel.send(MilestonesUiEvent.AddCustomGoal)
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _eventChannel.send(MilestonesUiEvent.NavigateBack)
        }
    }
}
