package com.simats.aiskillmirror.ui.assessment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AssessmentViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AssessmentUiState())
    val uiState: StateFlow<AssessmentUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<AssessmentUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onSelectOption(optionKey: String) {
        _uiState.value = _uiState.value.copy(selectedOptionKey = optionKey)
    }

    fun onNextQuestionClick() {
        val current = _uiState.value.currentQuestionIndex
        val total = _uiState.value.totalQuestions

        if (current < total) {
            val nextIndex = current + 1
            val nextProgress = ((nextIndex.toFloat() / total) * 100).toInt()
            _uiState.value = _uiState.value.copy(
                currentQuestionIndex = nextIndex,
                progressPercentage = nextProgress,
                isLastQuestion = nextIndex == total
            )
            viewModelScope.launch {
                _eventChannel.send(AssessmentUiEvent.NextQuestion)
            }
        } else {
            viewModelScope.launch {
                _eventChannel.send(AssessmentUiEvent.FinishAssessment)
            }
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _eventChannel.send(AssessmentUiEvent.NavigateBack)
        }
    }
}
