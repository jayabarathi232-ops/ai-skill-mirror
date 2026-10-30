package com.simats.aiskillmirror.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.aiskillmirror.data.network.UserSession
import com.simats.aiskillmirror.data.repository.ProfileRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository = ProfileRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<ProfileUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = profileRepository.getProfile(UserSession.userId)
            _uiState.value = _uiState.value.copy(isLoading = false)

            if (result.isSuccess && result.data != null) {
                val p = result.data
                val initials = if (p.name.isNotBlank()) {
                    p.name.trim().split(" ")
                        .filter { it.isNotBlank() }
                        .map { it.first().uppercaseChar() }
                        .take(2)
                        .joinToString("")
                } else "TS"

                _uiState.value = _uiState.value.copy(
                    userName = if (p.name.isNotBlank()) p.name else _uiState.value.userName,
                    email = if (p.email.isNotBlank()) p.email else _uiState.value.email,
                    phone = if (p.phone.isNotBlank()) p.phone else _uiState.value.phone,
                    userBio = if (p.bio.isNotBlank()) p.bio else _uiState.value.userBio,
                    education = if (p.education.isNotBlank()) p.education else _uiState.value.education,
                    skills = if (p.skills.isNotBlank()) p.skills else _uiState.value.skills,
                    careerGoal = if (p.careerGoal.isNotBlank()) p.careerGoal else _uiState.value.careerGoal,
                    profilePictureUrl = p.profilePicture,
                    initials = initials,
                    errorMessage = null
                )
            }
        }
    }

    fun saveProfile(
        name: String,
        phone: String,
        education: String,
        skills: String,
        careerGoal: String,
        bio: String,
        imageBytes: ByteArray? = null,
        fileName: String? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            // 1. If an image was selected, upload it first
            if (imageBytes != null && !fileName.isNullOrBlank()) {
                val uploadRes = profileRepository.uploadPicture(UserSession.userId, imageBytes, fileName)
                if (uploadRes.isSuccess && uploadRes.data != null) {
                    _uiState.value = _uiState.value.copy(profilePictureUrl = uploadRes.data)
                }
            }

            // 2. Save profile fields
            val updateRes = profileRepository.updateProfile(
                userId = UserSession.userId,
                phone = phone,
                education = education,
                skills = skills,
                careerGoal = careerGoal,
                bio = bio,
                name = name
            )

            _uiState.value = _uiState.value.copy(isLoading = false)

            if (updateRes.isSuccess) {
                loadProfile()
                _eventChannel.send(ProfileUiEvent.ShowToast("Profile updated successfully"))
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(errorMessage = updateRes.message ?: "Failed to update profile")
                _eventChannel.send(ProfileUiEvent.ShowToast(updateRes.message ?: "Update failed"))
            }
        }
    }

    fun onEditPersonalInfoClick() {
        viewModelScope.launch {
            _eventChannel.send(ProfileUiEvent.EditPersonalInfo)
        }
    }

    fun onNextMilestoneClick() {
        viewModelScope.launch {
            _eventChannel.send(ProfileUiEvent.OpenNextMilestone)
        }
    }
}
