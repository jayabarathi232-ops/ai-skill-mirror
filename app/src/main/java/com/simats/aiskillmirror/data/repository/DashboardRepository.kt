package com.simats.aiskillmirror.data.repository

import com.simats.aiskillmirror.data.network.ApiClient
import com.simats.aiskillmirror.data.network.DashboardData
import com.simats.aiskillmirror.data.network.DashboardHistoryItemData
import com.simats.aiskillmirror.data.network.DashboardSkillItemData
import com.simats.aiskillmirror.data.network.NetworkResult
import com.simats.aiskillmirror.data.network.UserSession
import com.simats.aiskillmirror.network.PerformanceUploadItemDto
import com.simats.aiskillmirror.network.RetrofitClient
import java.io.File

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
                    profilePicture = ApiClient.resolveImageUrl(dto.profilePicture),
                    aiCoachTitle = dto.aiCoachTitle.orEmpty(),
                    aiCoachSubtitle = dto.aiCoachSubtitle.orEmpty(),
                    mySkills = dto.mySkills.map {
                        DashboardSkillItemData(
                            id = it.id,
                            name = it.name,
                            category = it.category,
                            score = it.score,
                            level = it.level,
                            iconEmoji = it.iconEmoji,
                            accentColor = it.accentColor,
                            progress = it.progress
                        )
                    },
                    recentHistory = dto.recentHistory.map {
                        DashboardHistoryItemData(
                            id = it.id,
                            skillName = it.skillName,
                            activity = it.activity,
                            score = it.score,
                            dateText = it.dateText,
                            iconEmoji = it.iconEmoji,
                            feedback = it.feedback
                        )
                    }
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

    private val performanceUploadRepository = PerformanceUploadRepository()

    suspend fun uploadMedia(
        userId: Int = UserSession.userId,
        type: String,
        filename: String
    ): NetworkResult<PerformanceUploadItemDto> {
        val cat = when (type.lowercase()) {
            "video" -> "speaking"
            "photo", "image" -> "photography"
            "audio" -> "music"
            else -> "general"
        }
        val title = "${type.replaceFirstChar { it.uppercase() }} Practice Session"
        val mime = when (type.lowercase()) {
            "video" -> "video/mp4"
            "photo", "image" -> "image/jpeg"
            else -> "audio/wav"
        }
        val dummyFile = File(filename.ifBlank { "practice_file" })
        return performanceUploadRepository.uploadPerformance(
            userId = userId,
            mediaType = if (type == "image") "photo" else type,
            skillCategory = cat,
            title = title,
            file = dummyFile,
            mimeType = mime
        )
    }
}
