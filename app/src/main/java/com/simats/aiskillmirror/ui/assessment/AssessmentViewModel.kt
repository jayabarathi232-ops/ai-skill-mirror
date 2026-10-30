package com.simats.aiskillmirror.ui.assessment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.aiskillmirror.data.network.QuestionData
import com.simats.aiskillmirror.data.network.UserSession
import com.simats.aiskillmirror.data.repository.AssessmentRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AssessmentViewModel(
    private val assessmentRepository: AssessmentRepository = AssessmentRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssessmentUiState())
    val uiState: StateFlow<AssessmentUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<AssessmentUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    private var loadedQuestions: List<QuestionData> = emptyList()
    private val selectedAnswers = mutableMapOf<Int, String>()
    private var currentSkillId: String = "comm"

    init {
        loadQuestions("comm")
    }

    fun loadQuestions(skillId: String = "comm") {
        currentSkillId = skillId
        viewModelScope.launch {
            val result = assessmentRepository.getQuestions(skillId)
            if (result.isSuccess && !result.data.isNullOrEmpty()) {
                loadedQuestions = result.data
                displayQuestion(0)
            }
        }
    }

    private fun displayQuestion(index: Int) {
        if (index in loadedQuestions.indices) {
            val q = loadedQuestions[index]
            val total = loadedQuestions.size
            val progress = (((index + 1).toFloat() / total) * 100).toInt()
            val mappedOptions = q.options.map { AssessmentOption(it.key, it.text) }

            _uiState.value = _uiState.value.copy(
                currentQuestionIndex = index + 1,
                totalQuestions = total,
                progressPercentage = progress,
                topicBadgeText = q.topicBadge,
                questionHeadline = q.headline,
                questionScenario = q.scenario,
                options = mappedOptions,
                selectedOptionKey = selectedAnswers[q.id] ?: mappedOptions.firstOrNull()?.key ?: "A",
                isLastQuestion = index == total - 1
            )
        }
    }

    fun onSelectOption(optionKey: String) {
        _uiState.value = _uiState.value.copy(selectedOptionKey = optionKey)
        val currentIndex = _uiState.value.currentQuestionIndex - 1
        if (currentIndex in loadedQuestions.indices) {
            val qId = loadedQuestions[currentIndex].id
            selectedAnswers[qId] = optionKey
        }
    }

    fun onNextQuestionClick() {
        val currentIndex = _uiState.value.currentQuestionIndex - 1
        if (currentIndex in loadedQuestions.indices) {
            val qId = loadedQuestions[currentIndex].id
            selectedAnswers[qId] = _uiState.value.selectedOptionKey
        }

        val total = _uiState.value.totalQuestions
        if (currentIndex + 1 < total) {
            displayQuestion(currentIndex + 1)
            viewModelScope.launch {
                _eventChannel.send(AssessmentUiEvent.NextQuestion)
            }
        } else {
            // Finished: submit to MySQL via repository
            viewModelScope.launch {
                val answersList = selectedAnswers.map { Pair(it.key, it.value) }
                assessmentRepository.submitAssessment(UserSession.userId, currentSkillId, answersList)
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
