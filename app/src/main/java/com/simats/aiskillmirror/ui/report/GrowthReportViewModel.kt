package com.simats.aiskillmirror.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class GrowthReportViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GrowthReportUiState())
    val uiState: StateFlow<GrowthReportUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<GrowthReportUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onExportPdfClick() {
        viewModelScope.launch {
            _eventChannel.send(GrowthReportUiEvent.ExportPdf)
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _eventChannel.send(GrowthReportUiEvent.NavigateBack)
        }
    }
}
