package com.simats.aiskillmirror.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.aiskillmirror.data.network.ApiClient
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

    fun loadProfile(context: Context? = null) {
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

                val resolvedUrl = ApiClient.resolveImageUrl(p.profilePicture)
                if (resolvedUrl.isNotBlank()) {
                    UserSession.setProfilePicture(context, resolvedUrl)
                }

                val wp = p.weeklyProgress
                val todayIdx = if (wp.days.isNotEmpty()) {
                    val found = wp.days.indexOfFirst { it.isToday }
                    if (found >= 0) found else 0
                } else -1
                val selDay = if (todayIdx in wp.days.indices) wp.days[todayIdx] else null

                _uiState.value = _uiState.value.copy(
                    userName = if (p.name.isNotBlank()) p.name else _uiState.value.userName,
                    email = if (p.email.isNotBlank()) p.email else _uiState.value.email,
                    phone = if (p.phone.isNotBlank()) p.phone else _uiState.value.phone,
                    userBio = if (p.bio.isNotBlank()) p.bio else _uiState.value.userBio,
                    education = if (p.education.isNotBlank()) p.education else _uiState.value.education,
                    skills = if (p.skills.isNotBlank()) p.skills else _uiState.value.skills,
                    careerGoal = if (p.careerGoal.isNotBlank()) p.careerGoal else _uiState.value.careerGoal,
                    profilePictureUrl = resolvedUrl,
                    initials = initials,
                    skillScore = p.skillScore,
                    totalSessions = p.totalSessions,
                    dayStreak = p.dayStreak,
                    totalXp = p.totalXp,
                    memberSinceText = if (p.memberSinceText.isNotBlank()) p.memberSinceText else _uiState.value.memberSinceText,
                    weeklyProgress = wp,
                    selectedDayIndex = todayIdx,
                    selectedDay = selDay,
                    currentWeekOffset = wp.weekOffset,
                    achievedBadges = p.achievedBadges,
                    lockedBadges = p.lockedBadges,
                    errorMessage = null
                )

                // Fetch real weekly comparison
                try {
                    val compRes = ApiClient.fetchWeeklyComparison(UserSession.userId, 0, includeAi = false)
                    if (compRes.isSuccess && compRes.data != null) {
                        _uiState.value = _uiState.value.copy(weeklyComparison = compRes.data)
                    }
                } catch (ignored: Exception) {}
            } else if (context != null) {
                // Restore locally persisted profile picture if offline
                val localPic = UserSession.getPersistedProfilePicture(context, UserSession.userId)
                if (localPic.isNotBlank()) {
                    _uiState.value = _uiState.value.copy(profilePictureUrl = localPic)
                }
            }
        }
    }

    fun uploadPhotoDirectly(
        context: Context,
        imageBytes: ByteArray,
        fileName: String,
        onSuccess: () -> Unit = {}
    ) {
        val ext = fileName.substringAfterLast('.', "").lowercase()
        if (ext !in listOf("jpg", "jpeg", "png", "webp")) {
            viewModelScope.launch {
                _eventChannel.send(ProfileUiEvent.ShowToast("Invalid format. Please select a JPG, PNG, or WEBP image."))
            }
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploadingPhoto = true, errorMessage = null)
            val uploadRes = profileRepository.uploadPicture(UserSession.userId, imageBytes, fileName)
            _uiState.value = _uiState.value.copy(isUploadingPhoto = false)

            if (uploadRes.isSuccess && !uploadRes.data.isNullOrBlank()) {
                val fullUrl = ApiClient.resolveImageUrl(uploadRes.data)
                UserSession.setProfilePicture(context, fullUrl)
                _uiState.value = _uiState.value.copy(profilePictureUrl = fullUrl)
                _eventChannel.send(ProfileUiEvent.ShowToast("Profile photo updated successfully!"))
                onSuccess()
            } else {
                val err = uploadRes.message ?: "Failed to upload profile photo"
                _uiState.value = _uiState.value.copy(errorMessage = err)
                _eventChannel.send(ProfileUiEvent.ShowToast(err))
            }
        }
    }

    fun removePhoto(
        context: Context,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploadingPhoto = true, errorMessage = null)
            val res = profileRepository.removePicture(UserSession.userId)
            _uiState.value = _uiState.value.copy(isUploadingPhoto = false)

            if (res.isSuccess) {
                UserSession.setProfilePicture(context, "")
                _uiState.value = _uiState.value.copy(profilePictureUrl = "")
                _eventChannel.send(ProfileUiEvent.ShowToast("Profile photo removed"))
                onSuccess()
            } else {
                _eventChannel.send(ProfileUiEvent.ShowToast(res.message ?: "Failed to remove photo"))
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
        context: Context? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            // 1. If an image was selected, upload it first
            var newProfilePic = _uiState.value.profilePictureUrl
            if (imageBytes != null && !fileName.isNullOrBlank()) {
                val uploadRes = profileRepository.uploadPicture(UserSession.userId, imageBytes, fileName)
                if (uploadRes.isSuccess && !uploadRes.data.isNullOrBlank()) {
                    newProfilePic = ApiClient.resolveImageUrl(uploadRes.data)
                    UserSession.setProfilePicture(context, newProfilePic)
                    _uiState.value = _uiState.value.copy(profilePictureUrl = newProfilePic)
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
                loadProfile(context)
                _eventChannel.send(ProfileUiEvent.ShowToast("Profile updated successfully"))
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(errorMessage = updateRes.message ?: "Failed to update profile")
                _eventChannel.send(ProfileUiEvent.ShowToast(updateRes.message ?: "Update failed"))
            }
        }
    }

    fun selectDay(index: Int) {
        val days = if (_uiState.value.weeklyProgress.days.size == 7) {
            _uiState.value.weeklyProgress.days
        } else {
            com.simats.aiskillmirror.data.network.createDefaultWeekDays(_uiState.value.currentWeekOffset)
        }
        if (index in days.indices) {
            val selected = days[index]
            val updatedWp = if (_uiState.value.weeklyProgress.days.size != 7) {
                _uiState.value.weeklyProgress.copy(days = days)
            } else {
                _uiState.value.weeklyProgress
            }
            _uiState.value = _uiState.value.copy(
                weeklyProgress = updatedWp,
                selectedDayIndex = index,
                selectedDay = selected
            )
        }
    }

    fun changeWeekOffset(delta: Int) {
        val newOffset = _uiState.value.currentWeekOffset + delta
        val initialDays = com.simats.aiskillmirror.data.network.createDefaultWeekDays(newOffset)
        val todayIdx = initialDays.indexOfFirst { it.isToday }.let { if (it >= 0) it else 0 }
        _uiState.value = _uiState.value.copy(
            currentWeekOffset = newOffset,
            weeklyProgress = _uiState.value.weeklyProgress.copy(
                weekOffset = newOffset,
                days = initialDays
            ),
            selectedDayIndex = todayIdx,
            selectedDay = initialDays.getOrNull(todayIdx),
            isLoadingWeeklyProgress = true
        )
        viewModelScope.launch {
            val res = ApiClient.fetchWeeklyProgress(UserSession.userId, newOffset)
            _uiState.value = _uiState.value.copy(isLoadingWeeklyProgress = false)
            if (res.isSuccess && res.data != null) {
                val wp = res.data
                val resolvedIdx = if (wp.days.isNotEmpty()) {
                    val found = wp.days.indexOfFirst { it.isToday }
                    if (found >= 0) found else 0
                } else 0
                val selDay = wp.days.getOrNull(resolvedIdx) ?: initialDays.getOrNull(resolvedIdx)
                _uiState.value = _uiState.value.copy(
                    weeklyProgress = wp,
                    selectedDayIndex = resolvedIdx,
                    selectedDay = selDay
                )
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
