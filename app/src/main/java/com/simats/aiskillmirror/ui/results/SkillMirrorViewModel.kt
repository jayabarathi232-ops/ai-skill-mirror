package com.simats.aiskillmirror.ui.results

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

class SkillMirrorViewModel(
    private val progressRepository: ProgressRepository = ProgressRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SkillMirrorUiState())
    val uiState: StateFlow<SkillMirrorUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<SkillMirrorUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        loadSkillMirror()
    }

    fun loadSkillMirror() {
        viewModelScope.launch {
            val result = progressRepository.getSkillMirror(UserSession.userId)
            if (result.isSuccess && result.data != null) {
                val d = result.data
                val scores = d.skillScores.map {
                    SkillScoreItem(name = it.name, score = it.score, maxScore = it.maxScore, isTopTier = it.isTopTier)
                }
                _uiState.value = _uiState.value.copy(
                    overallScore = d.overallScore,
                    statusBadge = d.statusBadge,
                    pointsGainSubtitle = d.pointsGainSubtitle,
                    skillCountText = d.skillCountText,
                    skillScores = scores,
                    nextMoveTitle = d.nextMoveTitle,
                    nextMoveDescription = d.nextMoveDescription,
                    lastUpdatedText = d.lastUpdatedText
                )
            }
        }
    }

    fun onViewLearningPlanClick() {
        viewModelScope.launch {
            _eventChannel.send(SkillMirrorUiEvent.ViewLearningPlan)
        }
    }

    fun onShareResultsClick() {
        viewModelScope.launch {
            _eventChannel.send(SkillMirrorUiEvent.ShareResults)
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _eventChannel.send(SkillMirrorUiEvent.NavigateBack)
        }
    }
}
