package com.simats.aiskillmirror.ui.profile

import com.simats.aiskillmirror.data.network.DailyProgressItem
import com.simats.aiskillmirror.data.network.ProfileBadgeItem
import com.simats.aiskillmirror.data.network.ProfileWeeklyProgressData

data class ProfileUiState(
    val initials: String = "TS",
    val userName: String = "Test Student",
    val userBio: String = "Passionate about mobile apps.",
    val learnerBadgeText: String = "ADVANCED LEARNER",
    val skillScore: Int = 0,
    val totalSessions: Int = 0,
    val dayStreak: Int = 0,
    val totalXp: Int = 0,
    val email: String = "teststudent@example.com",
    val phone: String = "9876543210",
    val education: String = "B.Tech Information Technology",
    val skills: String = "Java, Kotlin, Python",
    val careerGoal: String = "Android Developer",
    val profilePictureUrl: String = "",
    val nextMilestoneTitle: String = "Your next milestone",
    val nextMilestoneSubtitle: String = "Reach a skill score of 85",
    val memberSinceText: String = "Growing with AI Skills Mirror",
    val weeklyProgress: ProfileWeeklyProgressData = ProfileWeeklyProgressData(),
    val weeklyComparison: com.simats.aiskillmirror.data.network.WeeklyComparisonData = com.simats.aiskillmirror.data.network.WeeklyComparisonData(),
    val selectedDayIndex: Int = -1,
    val selectedDay: DailyProgressItem? = null,
    val currentWeekOffset: Int = 0,
    val isLoadingWeeklyProgress: Boolean = false,
    val achievedBadges: List<ProfileBadgeItem> = emptyList(),
    val lockedBadges: List<ProfileBadgeItem> = emptyList(),
    val isLoading: Boolean = false,
    val isUploadingPhoto: Boolean = false,
    val errorMessage: String? = null
)

sealed interface ProfileUiEvent {
    object EditPersonalInfo : ProfileUiEvent
    object OpenNextMilestone : ProfileUiEvent
    data class ShowToast(val message: String) : ProfileUiEvent
}
