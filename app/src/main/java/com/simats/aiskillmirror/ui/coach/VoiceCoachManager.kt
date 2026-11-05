package com.simats.aiskillmirror.ui.coach

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

enum class VoiceCoachState {
    IDLE,
    LISTENING,
    PROCESSING,
    SPEAKING,
    PAUSED,
    ERROR
}

enum class VoiceLanguage(val code: String, val displayName: String, val speechTag: String) {
    ENGLISH("en", "English", "en-US"),
    TAMIL("ta", "தமிழ்", "ta-IN")
}

class VoiceCoachManager(
    private val context: Context,
    private val onStateChanged: (VoiceCoachState) -> Unit,
    private val onPartialText: (String) -> Unit,
    private val onFinalText: (String) -> Unit,
    private val onRmsChanged: (Float) -> Unit,
    private val onError: (String) -> Unit,
    private val onTtsFinished: () -> Unit
) : RecognitionListener, TextToSpeech.OnInitListener {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsInitialized = false
    private var currentState = VoiceCoachState.IDLE
    var selectedLanguage: VoiceLanguage = VoiceLanguage.ENGLISH
    var isContinuousMode: Boolean = true
    var isMuted: Boolean = false

    init {
        initSpeechRecognizer()
        initTextToSpeech()
    }

    private fun initSpeechRecognizer() {
        mainHandler.post {
            try {
                if (SpeechRecognizer.isRecognitionAvailable(context)) {
                    speechRecognizer?.destroy()
                    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                        setRecognitionListener(this@VoiceCoachManager)
                    }
                }
            } catch (e: Exception) {
                onError("Failed to initialize speech recognition: ${e.message}")
            }
        }
    }

    private fun initTextToSpeech() {
        try {
            textToSpeech = TextToSpeech(context, this)
        } catch (e: Exception) {
            onError("Failed to initialize text to speech: ${e.message}")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true
            applyTtsLanguage()
            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    updateState(VoiceCoachState.SPEAKING)
                }

                override fun onDone(utteranceId: String?) {
                    updateState(VoiceCoachState.IDLE)
                    mainHandler.post {
                        onTtsFinished()
                        if (isContinuousMode) {
                            mainHandler.postDelayed({
                                if (currentState != VoiceCoachState.PAUSED) {
                                    startListening()
                                }
                            }, 500)
                        }
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    updateState(VoiceCoachState.IDLE)
                }
            })
        }
    }

    private fun applyTtsLanguage() {
        if (!isTtsInitialized) return
        val targetLocale = when (selectedLanguage) {
            VoiceLanguage.TAMIL -> Locale("ta", "IN")
            VoiceLanguage.ENGLISH -> Locale.US
        }
        val availability = textToSpeech?.isLanguageAvailable(targetLocale)
        if (availability == TextToSpeech.LANG_AVAILABLE ||
            availability == TextToSpeech.LANG_COUNTRY_AVAILABLE ||
            availability == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE
        ) {
            textToSpeech?.language = targetLocale
        } else {
            // Fallback gracefully
            textToSpeech?.language = Locale.US
        }
    }

    fun setLanguage(language: VoiceLanguage) {
        selectedLanguage = language
        applyTtsLanguage()
    }

    fun startListening() {
        stopSpeaking()
        mainHandler.post {
            try {
                if (speechRecognizer == null) {
                    initSpeechRecognizer()
                }
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguage.speechTag)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, selectedLanguage.speechTag)
                    putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, selectedLanguage.speechTag)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                }
                speechRecognizer?.startListening(intent)
                updateState(VoiceCoachState.LISTENING)
            } catch (e: Exception) {
                updateState(VoiceCoachState.ERROR)
                onError("Failed to start listening: ${e.localizedMessage}")
            }
        }
    }

    fun stopListening() {
        mainHandler.post {
            try {
                speechRecognizer?.stopListening()
            } catch (_: Exception) {}
        }
    }

    fun cancelListening() {
        mainHandler.post {
            try {
                speechRecognizer?.cancel()
                if (currentState == VoiceCoachState.LISTENING) {
                    updateState(VoiceCoachState.IDLE)
                }
            } catch (_: Exception) {}
        }
    }

    fun speak(text: String) {
        if (isMuted || text.isBlank() || !isTtsInitialized) {
            updateState(VoiceCoachState.IDLE)
            if (isContinuousMode) {
                mainHandler.postDelayed({ startListening() }, 500)
            }
            return
        }

        stopListening()
        applyTtsLanguage()
        updateState(VoiceCoachState.SPEAKING)

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "mira_voice_${System.currentTimeMillis()}")
        }
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "mira_voice_${System.currentTimeMillis()}")
    }

    fun stopSpeaking() {
        try {
            if (textToSpeech?.isSpeaking == true) {
                textToSpeech?.stop()
            }
        } catch (_: Exception) {}
        if (currentState == VoiceCoachState.SPEAKING) {
            updateState(VoiceCoachState.IDLE)
        }
    }

    fun pauseConversation() {
        stopSpeaking()
        cancelListening()
        updateState(VoiceCoachState.PAUSED)
    }

    fun resumeConversation() {
        startListening()
    }

    private fun updateState(newState: VoiceCoachState) {
        currentState = newState
        mainHandler.post {
            onStateChanged(newState)
        }
    }

    // RecognitionListener Callbacks
    override fun onReadyForSpeech(params: Bundle?) {
        updateState(VoiceCoachState.LISTENING)
    }

    override fun onBeginningOfSpeech() {
        updateState(VoiceCoachState.LISTENING)
    }

    override fun onRmsChanged(rmsdB: Float) {
        mainHandler.post {
            onRmsChanged(rmsdB)
        }
    }

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {
        updateState(VoiceCoachState.PROCESSING)
    }

    override fun onError(error: Int) {
        val errorMessage = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required"
            SpeechRecognizer.ERROR_NETWORK -> "Network connection error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Please try again."
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
            SpeechRecognizer.ERROR_SERVER -> "Server recognition error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected"
            else -> "Speech recognition error ($error)"
        }

        updateState(VoiceCoachState.IDLE)
        mainHandler.post {
            onError(errorMessage)
        }
    }

    override fun onResults(results: Bundle?) {
        updateState(VoiceCoachState.PROCESSING)
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val finalTranscript = matches?.firstOrNull() ?: ""
        mainHandler.post {
            onFinalText(finalTranscript)
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val partialTranscript = matches?.firstOrNull() ?: ""
        if (partialTranscript.isNotBlank()) {
            mainHandler.post {
                onPartialText(partialTranscript)
            }
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}

    fun destroy() {
        try {
            speechRecognizer?.destroy()
            speechRecognizer = null
        } catch (_: Exception) {}

        try {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            textToSpeech = null
        } catch (_: Exception) {}
    }
}
