package com.simats.aiskillmirror.ui.profile

data class ProfileUiState(
    val initials: String = "TS",
    val userName: String = "Test Student",
    val userBio: String = "Passionate about mobile apps.",
    val learnerBadgeText: String = "ADVANCED LEARNER",
    val skillScore: Int = 78,
    val totalSessions: Int = 24,
    val dayStreak: Int = 7,
    val email: String = "teststudent@example.com",
    val phone: String = "9876543210",
    val education: String = "B.Tech Information Technology",
    val skills: String = "Java, Kotlin, Python",
    val careerGoal: String = "Android Developer",
    val profilePictureUrl: String = "",
    val nextMilestoneTitle: String = "Your next milestone",
    val nextMilestoneSubtitle: String = "Reach a skill score of 85 by Nov 15",
    val memberSinceText: String = "Growing with AI Skills Mirror since September 2026",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface ProfileUiEvent {
    object EditPersonalInfo : ProfileUiEvent
    object OpenNextMilestone : ProfileUiEvent
    data class ShowToast(val message: String) : ProfileUiEvent
}
