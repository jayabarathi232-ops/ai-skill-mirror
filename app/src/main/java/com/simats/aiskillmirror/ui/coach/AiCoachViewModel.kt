package com.simats.aiskillmirror.ui.coach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.aiskillmirror.data.network.ApiClient
import com.simats.aiskillmirror.data.network.CompletedSessionItem
import com.simats.aiskillmirror.data.network.UserSession
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AiCoachViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AiCoachUiState())
    val uiState: StateFlow<AiCoachUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<AiCoachUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        loadChat()
    }

    fun loadChat() {
        viewModelScope.launch {
            val result = ApiClient.fetchCoachChat(UserSession.userId)
            if (result.isSuccess && result.data != null) {
                val d = result.data
                val mapped = d.messages.map {
                    ChatMessage(
                        id = it.id,
                        text = it.text,
                        isUser = it.isUser,
                        timestamp = it.timestamp
                    )
                }
                val lastCoachMsg = mapped.lastOrNull { !it.isUser }?.text
                    ?: _uiState.value.latestAiVoiceResponse

                _uiState.value = _uiState.value.copy(
                    coachName = d.coachName,
                    statusText = d.statusText,
                    bannerNoticeText = d.bannerNoticeText,
                    timestampHeader = d.timestampHeader,
                    messages = mapped,
                    suggestionChips = d.suggestionChips,
                    completedSessions = d.completedSessions,
                    latestAiVoiceResponse = lastCoachMsg
                )
            }
        }
    }

    fun onInputChange(text: String) {
        _uiState.value = _uiState.value.copy(currentInputText = text)
    }

    fun onSendMessage() {
        val text = _uiState.value.currentInputText.trim()
        if (text.isNotEmpty()) {
            val optimisticMsg = ChatMessage(
                id = System.currentTimeMillis().toString(),
                text = text,
                isUser = true,
                timestamp = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(java.util.Date())
            )
            val updatedMessages = _uiState.value.messages + optimisticMsg
            _uiState.value = _uiState.value.copy(
                messages = updatedMessages,
                currentInputText = "",
                isTyping = true,
                errorMessage = null,
                lastFailedMessage = null
            )

            viewModelScope.launch {
                val result = ApiClient.sendCoachMessage(
                    UserSession.userId,
                    text,
                    _uiState.value.voiceLanguage.code
                )
                if (result.isSuccess && result.data != null) {
                    val coachReply = ChatMessage(
                        id = result.data.second.id,
                        text = result.data.second.text,
                        isUser = false,
                        timestamp = result.data.second.timestamp
                    )
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + coachReply,
                        latestAiVoiceResponse = coachReply.text,
                        isTyping = false,
                        errorMessage = null,
                        lastFailedMessage = null
                    )
                } else {
                    val errMsg = result.message?.ifBlank { "Could not connect to AI Coach" } ?: "Could not connect to AI Coach"
                    _uiState.value = _uiState.value.copy(
                        isTyping = false,
                        errorMessage = errMsg,
                        lastFailedMessage = text
                    )
                    _eventChannel.send(AiCoachUiEvent.ShowToast(errMsg))
                }
                _eventChannel.send(AiCoachUiEvent.SendMessage)
            }
        }
    }

    fun onRetryLastMessage() {
        val failedText = _uiState.value.lastFailedMessage ?: return
        _uiState.value = _uiState.value.copy(
            currentInputText = failedText,
            errorMessage = null,
            lastFailedMessage = null
        )
        onSendMessage()
    }

    // Voice Chat Controls
    fun setVoiceMode(active: Boolean) {
        _uiState.value = _uiState.value.copy(
            isVoiceModeActive = active,
            voiceErrorMessage = null
        )
    }

    fun setVoiceLanguage(lang: VoiceLanguage) {
        _uiState.value = _uiState.value.copy(voiceLanguage = lang)
    }

    fun setVoiceState(state: VoiceCoachState) {
        _uiState.value = _uiState.value.copy(voiceState = state)
    }

    fun setUserSpokenText(text: String) {
        _uiState.value = _uiState.value.copy(userSpokenText = text)
    }

    fun setEditingSpokenText(editing: Boolean) {
        _uiState.value = _uiState.value.copy(isEditingSpokenText = editing)
    }

    fun setAudioRmsLevel(level: Float) {
        _uiState.value = _uiState.value.copy(audioRmsLevel = level)
    }

    fun setContinuousMode(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isContinuousMode = enabled)
    }

    fun setAudioMuted(muted: Boolean) {
        _uiState.value = _uiState.value.copy(isAudioMuted = muted)
    }

    fun setVoiceErrorMessage(msg: String?) {
        _uiState.value = _uiState.value.copy(voiceErrorMessage = msg)
    }

    fun sendVoiceMessage(spokenText: String, onAiReply: (String) -> Unit) {
        val trimmed = spokenText.trim()
        if (trimmed.isEmpty()) return

        val optimisticMsg = ChatMessage(
            id = System.currentTimeMillis().toString(),
            text = trimmed,
            isUser = true
        )
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + optimisticMsg,
            isSendingVoiceMessage = true,
            voiceState = VoiceCoachState.PROCESSING,
            voiceErrorMessage = null,
            isEditingSpokenText = false
        )

        viewModelScope.launch {
            val result = ApiClient.sendCoachMessage(
                UserSession.userId,
                trimmed,
                _uiState.value.voiceLanguage.code
            )
            _uiState.value = _uiState.value.copy(isSendingVoiceMessage = false)

            if (result.isSuccess && result.data != null) {
                val coachReply = ChatMessage(
                    id = result.data.second.id,
                    text = result.data.second.text,
                    isUser = false,
                    timestamp = result.data.second.timestamp
                )
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + coachReply,
                    latestAiVoiceResponse = coachReply.text,
                    userSpokenText = ""
                )
                onAiReply(coachReply.text)
            } else {
                val err = result.message?.ifBlank { "Voice coaching failed" } ?: "Voice coaching failed".ifBlank { "Could not connect to AI Coach" }
                _uiState.value = _uiState.value.copy(
                    voiceState = VoiceCoachState.IDLE,
                    voiceErrorMessage = err
                )
                _eventChannel.send(AiCoachUiEvent.ShowToast(err))
            }
        }
    }

    fun clearConversation() {
        viewModelScope.launch {
            val result = ApiClient.clearCoachChat(UserSession.userId, _uiState.value.voiceLanguage.code)
            if (result.isSuccess && result.data != null) {
                val welcome = ChatMessage(
                    id = result.data.id,
                    text = result.data.text,
                    isUser = false,
                    timestamp = result.data.timestamp
                )
                _uiState.value = _uiState.value.copy(
                    messages = listOf(welcome),
                    latestAiVoiceResponse = welcome.text,
                    userSpokenText = "",
                    voiceErrorMessage = null
                )
                _eventChannel.send(AiCoachUiEvent.ShowToast("Conversation cleared"))
            } else {
                _eventChannel.send(AiCoachUiEvent.ShowToast("Failed to clear chat"))
            }
        }
    }

    fun onSuggestionChipClick(chipText: String) {
        if (chipText.contains("practice", ignoreCase = true)) {
            viewModelScope.launch {
                _eventChannel.send(AiCoachUiEvent.StartPracticeFromCoach)
            }
        } else if (chipText.contains("tamil", ignoreCase = true)) {
            setVoiceLanguage(VoiceLanguage.TAMIL)
            onInputChange("வணக்கம் மீரா! எனக்கு வழிகாட்ட முடியுமா?")
            onSendMessage()
        } else {
            onInputChange(chipText)
            onSendMessage()
        }
    }

    fun openCompletedSessionsSheet() {
        _uiState.value = _uiState.value.copy(isCompletedSessionsSheetOpen = true)
        loadCompletedSessions()
    }

    fun closeCompletedSessionsSheet() {
        _uiState.value = _uiState.value.copy(isCompletedSessionsSheetOpen = false)
    }

    fun selectCompletedSkillFilter(filter: String) {
        _uiState.value = _uiState.value.copy(selectedCompletedSkillFilter = filter)
    }

    fun loadCompletedSessions() {
        viewModelScope.launch {
            val result = ApiClient.fetchCompletedSessions(UserSession.userId)
            if (result.isSuccess && result.data != null) {
                _uiState.value = _uiState.value.copy(completedSessions = result.data)
            }
        }
    }

    fun askCoachAboutSession(session: CompletedSessionItem) {
        val prompt = if (session.score != null) {
            "Coach Mira, how can I improve further on my completed practice session '${session.title}'? (Score: ${session.score}% in ${session.category})"
        } else {
            "Coach Mira, how can I improve my technique on my completed practice session '${session.title}' in ${session.category}?"
        }
        _uiState.value = _uiState.value.copy(
            isCompletedSessionsSheetOpen = false,
            currentInputText = prompt
        )
        onSendMessage()
    }

    fun onBackClick() {
        viewModelScope.launch {
            _eventChannel.send(AiCoachUiEvent.NavigateBack)
        }
    }
}
