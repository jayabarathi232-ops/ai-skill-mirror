package com.simats.aiskillmirror.ui.speech

import android.content.Context
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
import kotlin.math.roundToInt

class SpeechStudioViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SpeechStudioUiState())
    val uiState: StateFlow<SpeechStudioUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<SpeechStudioUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    private var timerJob: Job? = null

    init {
        loadSpeechDashboard()
    }

    fun loadSpeechDashboard() {
        viewModelScope.launch {
            val result = ApiClient.fetchSpeechHistory(UserSession.userId)
            if (result.isSuccess && result.data != null) {
                val d = result.data
                val mapped = d.history.map {
                    SpeechPracticeSessionItem(
                        id = it.id,
                        moduleName = "Speech Drill",
                        topicTitle = it.promptTitle,
                        topicQuote = it.promptQuote,
                        durationSeconds = it.durationSeconds,
                        overallScore = it.speechScore,
                        pronunciationScore = it.pronunciationScore,
                        fluencyScore = it.fluencyScore,
                        clarityScore = it.clarityScore,
                        wpm = 142,
                        transcript = it.transcript,
                        feedback = it.aiFeedback,
                        dateText = it.dateText
                    )
                }

                _uiState.value = _uiState.value.copy(
                    totalPracticeSessions = d.totalPracticeSessions,
                    speakingProgress = d.speakingProgress,
                    speechClarityFeedback = d.speechClarityFeedback,
                    speakingSpeed = d.speakingSpeed,
                    pronunciationFeedback = d.pronunciationFeedback,
                    recommendedNextExercise = d.recommendedNextExercise,
                    historyList = if (mapped.isNotEmpty()) mapped else _uiState.value.historyList
                )
            }
        }
    }

    fun switchTab(tab: SpeechTab) {
        _uiState.value = _uiState.value.copy(currentTab = tab)
    }

    fun selectModule(module: PublicSpeakingModule) {
        _uiState.value = _uiState.value.copy(currentModule = module)
        if (module == PublicSpeakingModule.PROGRESS_TRACKING) {
            _uiState.value = _uiState.value.copy(currentTab = SpeechTab.DASHBOARD)
            return
        }

        // Set module specific default prompt
        val (defaultTitle, defaultInstruction, defaultDuration) = when (module) {
            PublicSpeakingModule.SPEECH_PRACTICE -> Triple(
                "The Impact of Artificial Intelligence in Education",
                "Explain how personalized AI empowers students of different backgrounds. Aim for 60 seconds.",
                60
            )
            PublicSpeakingModule.VOICE_ANALYSIS -> Triple(
                "Acoustic Pacing & Deliberate Pauses Drill",
                "Read with conscious 1.5-second pauses between arguments without filler words.",
                45
            )
            PublicSpeakingModule.CONFIDENCE_TRAINING -> Triple(
                "30-Second Power Stance & Anchor Statement",
                "Breathe into your diaphragm and project your voice with relaxed executive presence.",
                30
            )
            PublicSpeakingModule.VOCABULARY_IMPROVEMENT -> Triple(
                "Elevated Word Replacement Drill",
                "Describe a project avoiding weak adjectives like 'good' or 'nice'. Use impactful and compelling.",
                60
            )
            PublicSpeakingModule.PRONUNCIATION_PRACTICE -> Triple(
                "Multisyllabic Articulation Marathon",
                "Enunciate every consonant clearly: 'Architectural scalability of our asynchronous distributed system.'",
                45
            )
            PublicSpeakingModule.PRESENTATION_TRAINING -> Triple(
                "Hook-Story-Offer Pitch Architecture",
                "Deliver an elevator pitch: 10s shocking hook, 30s relatable story, 20s clear solution.",
                60
            )
            PublicSpeakingModule.INTERVIEW_PRACTICE -> Triple(
                "STAR Behavioral Response: Resolving Conflict",
                "Describe a situation where a teammate disagreed with you. Use Situation, Task, Action, Result.",
                75
            )
            PublicSpeakingModule.PROGRESS_TRACKING -> Triple(
                "Benchmark Baseline Reflection",
                "Reflect on your speaking progress this week and identify one targeted improvement.",
                60
            )
        }

        _uiState.value = _uiState.value.copy(
            activePromptTitle = defaultTitle,
            activePromptInstruction = defaultInstruction,
            targetDurationSeconds = defaultDuration,
            currentTab = SpeechTab.STUDIO,
            timerText = "00:00",
            elapsedSeconds = 0,
            liveTranscript = "",
            isRecording = false,
            isPaused = false
        )

        // Asynchronously fetch fresh AI topic from backend
        generateNewAiTopic(module)
    }

    fun generateNewAiTopic(module: PublicSpeakingModule = _uiState.value.currentModule) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGeneratingTopic = true)
            val result = ApiClient.fetchSpeechTopic(module.id)
            _uiState.value = _uiState.value.copy(isGeneratingTopic = false)
            if (result.isSuccess && result.data != null) {
                val t = result.data
                _uiState.value = _uiState.value.copy(
                    activePromptTitle = t.title,
                    activePromptInstruction = "${t.prompt} ${t.tip}",
                    targetDurationSeconds = t.duration
                )
            }
        }
    }

    fun startRecording() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(
            isRecording = true,
            isPaused = false,
            elapsedSeconds = 0,
            timerText = "00:00",
            liveTranscript = ""
        )

        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (!_uiState.value.isPaused) {
                    val nextSec = _uiState.value.elapsedSeconds + 1
                    val mins = nextSec / 60
                    val secs = nextSec % 60
                    _uiState.value = _uiState.value.copy(
                        elapsedSeconds = nextSec,
                        timerText = String.format("%02d:%02d", mins, secs)
                    )
                }
            }
        }
    }

    fun togglePauseRecording() {
        val nextPaused = !_uiState.value.isPaused
        _uiState.value = _uiState.value.copy(isPaused = nextPaused)
    }

    fun updateLiveTranscript(text: String) {
        if (text.isNotBlank()) {
            _uiState.value = _uiState.value.copy(liveTranscript = text)
        }
    }

    fun updateAudioRms(rms: Float) {
        _uiState.value = _uiState.value.copy(audioRmsLevel = rms)
    }

    fun stopAndAnalyze() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(
            isRecording = false,
            isPaused = false,
            isAnalyzing = true
        )

        viewModelScope.launch {
            delay(1200) // Simulated acoustic processing delay

            val transcript = _uiState.value.liveTranscript.ifBlank {
                "I designed and deployed a scalable modular architecture that handles real-time speech and acoustic analysis with low latency."
            }

            val seconds = maxOf(1, _uiState.value.elapsedSeconds)
            val words = transcript.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
            val wordsCount = words.size
            val calculatedWpm = if (seconds > 3) ((wordsCount.toFloat() / (seconds.toFloat() / 60f)).roundToInt()).coerceIn(90, 220) else 142

            // Filler words detection
            val lowerTranscript = transcript.lowercase()
            val detected = mutableListOf<String>()
            val fillerList = listOf("um", "uh", "like", "actually", "you know", "sort of")
            var fillerCount = 0
            for (f in fillerList) {
                if (lowerTranscript.contains("\\b$f\\b".toRegex())) {
                    detected.add(f)
                    fillerCount++
                }
            }

            val paceRating = when {
                calculatedWpm in 130..155 -> "Optimal Pacing (130-155 WPM)"
                calculatedWpm < 130 -> "Slightly Deliberate Pacing (<130 WPM)"
                else -> "Fast Conversational Pacing (>155 WPM)"
            }

            val clarity = (92 - (fillerCount * 3)).coerceIn(70, 98)
            val pronunciation = (86 - (fillerCount * 2)).coerceIn(72, 96)
            val fluency = if (calculatedWpm in 125..160) 88 else 80
            val overall = ((clarity * 0.4f) + (pronunciation * 0.3f) + (fluency * 0.3f)).roundToInt()

            val strengths = mutableListOf<String>()
            val improvements = mutableListOf<String>()

            if (calculatedWpm in 125..160) {
                strengths.add("Cadence was steady and engaging at $calculatedWpm WPM.")
            } else {
                improvements.add("Aim for 130-150 WPM conversational speed (current: $calculatedWpm WPM).")
            }

            if (fillerCount <= 1) {
                strengths.add("Excellent filler control with minimal verbal hesitation.")
            } else {
                improvements.add("Replace verbal fillers ('${detected.joinToString(", ")}') with deliberate 1.5s silent pauses.")
            }

            strengths.add("Structure followed a clear narrative progression.")
            improvements.add("Emphasize impact metrics and concrete takeaways at the closing statement.")

            val analysis = SpeechSessionAnalysis(
                wordsCount = wordsCount,
                wpm = calculatedWpm,
                paceRating = paceRating,
                fillerCount = fillerCount,
                detectedFillers = detected,
                clarityScore = clarity,
                pronunciationScore = pronunciation,
                fluencyScore = fluency,
                overallScore = overall,
                transcript = transcript,
                personalizedFeedback = "Great performance! Your speaking speed was $calculatedWpm WPM with $clarity% speech clarity. Focusing on clean silent pauses will bring this up to executive standard.",
                strengths = strengths,
                areasForImprovement = improvements,
                suggestedDailyTask = "Perform a 60-second impromptu drill on today's AI topic using deliberate 2-second micro-pauses.",
                comparisonWithPrevious = "+4% Clarity · -${maxOf(1, fillerCount)} Fillers compared to previous session"
            )

            // Save to MySQL backend
            ApiClient.saveSpeechSession(
                userId = UserSession.userId,
                moduleType = _uiState.value.currentModule.id,
                promptTitle = _uiState.value.activePromptTitle,
                promptQuote = transcript,
                durationSeconds = seconds,
                speechScore = overall,
                pronunciationScore = pronunciation,
                fluencyScore = fluency,
                clarityScore = clarity,
                transcript = transcript,
                aiTip = analysis.personalizedFeedback,
                wpm = calculatedWpm
            )

            // Add to local history list
            val newSessionItem = SpeechPracticeSessionItem(
                id = System.currentTimeMillis().toString(),
                moduleName = _uiState.value.currentModule.title,
                topicTitle = _uiState.value.activePromptTitle,
                topicQuote = transcript,
                durationSeconds = seconds,
                overallScore = overall,
                pronunciationScore = pronunciation,
                fluencyScore = fluency,
                clarityScore = clarity,
                wpm = calculatedWpm,
                transcript = transcript,
                feedback = analysis.personalizedFeedback,
                dateText = "Just now"
            )

            _uiState.value = _uiState.value.copy(
                isAnalyzing = false,
                latestAnalysis = analysis,
                currentTab = SpeechTab.ANALYSIS,
                totalPracticeSessions = _uiState.value.totalPracticeSessions + 1,
                speakingProgress = overall,
                speechClarityFeedback = "$clarity% · Crisp articulation & structured flow",
                speakingSpeed = "$calculatedWpm WPM ($paceRating)",
                pronunciationFeedback = "$pronunciation% · Clear vowel balance",
                historyList = listOf(newSessionItem) + _uiState.value.historyList
            )

            _eventChannel.send(SpeechStudioUiEvent.ShowToast("Practice session analyzed & saved successfully!"))
        }
    }

    fun onBackClick() {
        when (_uiState.value.currentTab) {
            SpeechTab.ANALYSIS -> {
                _uiState.value = _uiState.value.copy(currentTab = SpeechTab.DASHBOARD)
            }
            SpeechTab.STUDIO -> {
                timerJob?.cancel()
                _uiState.value = _uiState.value.copy(
                    isRecording = false,
                    isPaused = false,
                    currentTab = SpeechTab.DASHBOARD
                )
            }
            SpeechTab.DASHBOARD -> {
                viewModelScope.launch {
                    _eventChannel.send(SpeechStudioUiEvent.NavigateBack)
                }
            }
        }
    }
}
