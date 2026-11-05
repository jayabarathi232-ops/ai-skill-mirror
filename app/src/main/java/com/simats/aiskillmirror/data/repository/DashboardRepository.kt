package com.simats.aiskillmirror.data.repository

import com.simats.aiskillmirror.data.network.ApiClient
import com.simats.aiskillmirror.data.network.DashboardData
import com.simats.aiskillmirror.data.network.NetworkResult
import com.simats.aiskillmirror.data.network.UserSession
import com.simats.aiskillmirror.network.RetrofitClient

class DashboardRepository {
    suspend fun getDashboard(userId: Int = UserSession.userId): NetworkResult<DashboardData> {
        return try {
            val response = RetrofitClient.apiService.getDashboard(userId)
            if (response.isSuccessful && response.body()?.status == "success" && response.body()?.data != null) {
                val dto = response.body()!!.data!!
                val data = DashboardData(
                    userName = dto.userName.ifBlank { "Learner" },
                    skillScore = dto.skillScore,
                    monthlyPtsGain = dto.monthlyPtsGain,
                    currentStreakDays = dto.currentStreakDays,
                    minutesToday = dto.minutesToday,
                    goalMinutesToday = dto.goalMinutesToday,
                    assessedSkillsCount = dto.assessedSkillsCount,
                    learnerStatus = dto.learnerStatus,
                    currentLessonTitle = dto.currentLessonTitle,
                    currentLessonCategory = dto.currentLessonCategory,
                    currentLessonDuration = dto.currentLessonDuration,
                    currentLessonProgress = dto.currentLessonProgress,
                    greeting = dto.greeting ?: "Good morning,",
                    currentDateText = dto.currentDateText ?: "",
                    headline = dto.headline ?: "Your future is taking shape.",
                    streakSubtitle = dto.streakSubtitle ?: "Your best week yet",
                    minutesSubtitle = dto.minutesSubtitle ?: "to your goal",
                    currentPlanTitle = dto.currentPlanTitle ?: "",
                    profilePicture = ApiClient.resolveImageUrl(dto.profilePicture)
                )
                if (data.profilePicture.isNotBlank()) {
                    UserSession.profilePictureUrl = data.profilePicture
                }
                NetworkResult(true, response.body()?.message, data)
            } else {
                ApiClient.fetchDashboard(userId)
            }
        } catch (e: Exception) {
            ApiClient.fetchDashboard(userId)
        }
    }
}
