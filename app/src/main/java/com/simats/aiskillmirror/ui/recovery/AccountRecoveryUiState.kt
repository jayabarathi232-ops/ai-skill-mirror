package com.simats.aiskillmirror.ui.recovery

data class AccountRecoveryUiState(
    val email: String = "maya.chen@university.edu",
    val isLoading: Boolean = false,
    val isLinkSent: Boolean = false,
    val infoMessageTitle: String = "Check your inbox",
    val infoMessageSubtitle: String = "The link will be valid for 15 minutes. Check your spam folder if you don't see it.",
    val supportEmail: String = "support@aiskillmirror.app"
)

sealed interface AccountRecoveryUiEvent {
    object NavigateBackToLogin : AccountRecoveryUiEvent
    object ResetLinkSent : AccountRecoveryUiEvent
}
