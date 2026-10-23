package com.simats.aiskillmirror.ui.profile

data class ProfileUiState(
    val initials: String = "MC",
    val userName: String = "Maya Chen",
    val userBio: String = "Final-year Computer Science student",
    val learnerBadgeText: String = "ADVANCED LEARNER",
    val skillScore: Int = 78,
    val totalSessions: Int = 24,
    val dayStreak: Int = 7,
    val email: String = "maya.chen@university.edu",
    val education: String = "B.Sc. Computer Science · Class of 2027",
    val careerGoal: String = "Graduate Software Engineer",
    val nextMilestoneTitle: String = "Your next milestone",
    val nextMilestoneSubtitle: String = "Reach a skill score of 85 by Nov 15",
    val memberSinceText: String = "Growing with AI Skills Mirror since September 2026"
)

sealed interface ProfileUiEvent {
    object EditPersonalInfo : ProfileUiEvent
    object OpenNextMilestone : ProfileUiEvent
}
