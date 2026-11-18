package com.simats.aiskillmirror.data.repository

import com.simats.aiskillmirror.data.network.ApiClient
import com.simats.aiskillmirror.data.network.GrowthRoadmapData
import com.simats.aiskillmirror.data.network.MilestoneAchievementData
import com.simats.aiskillmirror.data.network.MilestonesData
import com.simats.aiskillmirror.data.network.NetworkResult
import com.simats.aiskillmirror.data.network.NotificationItemData
import com.simats.aiskillmirror.data.network.NotificationsData
import com.simats.aiskillmirror.data.network.RoadmapStepData
import com.simats.aiskillmirror.data.network.SkillMirrorData
import com.simats.aiskillmirror.data.network.SkillMirrorScoreData
import com.simats.aiskillmirror.data.network.UserSession
import com.simats.aiskillmirror.network.RetrofitClient

class ProgressRepository {
    suspend fun getSkillMirror(userId: Int = UserSession.userId): NetworkResult<SkillMirrorData> {
        return try {
            val response = RetrofitClient.apiService.getSkillMirror(userId)
            if (response.isSuccessful && response.body()?.status == "success" && response.body()?.data != null) {
                val d = response.body()!!.data!!
                val scores = d.skillScores.map {
                    SkillMirrorScoreData(it.name, it.score, it.maxScore, it.isTopTier)
                }
                val data = SkillMirrorData(
                    overallScore = d.overallScore,
                    statusBadge = d.statusBadge,
                    pointsGainSubtitle = d.pointsGainSubtitle,
                    skillCountText = d.skillCountText,
                    skillScores = scores,
                    nextMoveTitle = d.nextMoveTitle,
                    nextMoveDescription = d.nextMoveDescription,
                    lastUpdatedText = d.lastUpdatedText
                )
                NetworkResult(true, response.body()?.message, data)
            } else {
                ApiClient.fetchSkillMirror(userId)
            }
        } catch (e: Exception) {
            ApiClient.fetchSkillMirror(userId)
        }
    }

    suspend fun getRoadmap(userId: Int = UserSession.userId, subcategoryId: String? = null): NetworkResult<GrowthRoadmapData> {
        return ApiClient.fetchRoadmap(userId, subcategoryId)
    }

    suspend fun completeRoadmapTopic(
        userId: Int = UserSession.userId,
        stageId: Int,
        score: Int,
        answers: Map<Int, Int>? = null
    ): NetworkResult<com.simats.aiskillmirror.data.network.CompleteTopicResultData> {
        return ApiClient.completeRoadmapTopic(userId, stageId, score, answers)
    }

    suspend fun toggleRoadmapTask(
        userId: Int = UserSession.userId,
        stageId: Int,
        taskId: String,
        isDone: Boolean
    ): NetworkResult<Boolean> {
        return ApiClient.toggleRoadmapTask(userId, stageId, taskId, isDone)
    }

    suspend fun getMilestones(userId: Int = UserSession.userId): NetworkResult<MilestonesData> {
        return try {
            val response = RetrofitClient.apiService.getMilestones(userId)
            if (response.isSuccessful && response.body()?.status == "success" && response.body()?.data != null) {
                val d = response.body()!!.data!!
                val items = d.achievements.map {
                    MilestoneAchievementData(it.id, it.title, it.earnedDateText, it.iconType)
                }
                val data = MilestonesData(
                    mainGoalTitle = d.mainGoalTitle,
                    currentScore = d.currentScore,
                    targetScore = d.targetScore,
                    statusText = d.statusText,
                    targetDateText = d.targetDateText,
                    streakTitle = d.streakTitle,
                    streakSubtitle = d.streakSubtitle,
                    streakProgressText = d.streakProgressText,
                    earnedCountText = d.earnedCountText,
                    achievements = items,
                    nextRewardTitle = d.nextRewardTitle,
                    nextRewardSubtitle = d.nextRewardSubtitle,
                    nextRewardTarget = d.nextRewardTarget,
                    totalXpText = d.totalXpText
                )
                NetworkResult(true, response.body()?.message, data)
            } else {
                ApiClient.fetchMilestones(userId)
            }
        } catch (e: Exception) {
            ApiClient.fetchMilestones(userId)
        }
    }

    suspend fun getNotifications(userId: Int = UserSession.userId): NetworkResult<NotificationsData> {
        return try {
            val response = RetrofitClient.apiService.getNotifications(userId)
            if (response.isSuccessful && response.body()?.status == "success" && response.body()?.data != null) {
                val d = response.body()!!.data!!
                val today = d.todayNotifications.map {
                    NotificationItemData(it.id, it.title, it.description, it.timestampText, it.badgeText, it.isGreenBadge, it.iconType, it.category, it.isRead)
                }
                val yest = d.yesterdayNotifications.map {
                    NotificationItemData(it.id, it.title, it.description, it.timestampText, it.badgeText, it.isGreenBadge, it.iconType, it.category, it.isRead)
                }
                val older = d.olderNotifications.map {
                    NotificationItemData(it.id, it.title, it.description, it.timestampText, it.badgeText, it.isGreenBadge, it.iconType, it.category, it.isRead)
                }
                val data = NotificationsData(
                    unreadCount = d.unreadCount,
                    isDailyReminderEnabled = d.isDailyReminderEnabled,
                    reminderTimeText = d.reminderTimeText,
                    reminderDaysText = d.reminderDaysText ?: "Every day",
                    reminderNoticeText = d.reminderNoticeText,
                    todayNotifications = today,
                    yesterdayNotifications = yest,
                    olderNotifications = older
                )
                NetworkResult(true, response.body()?.message, data)
            } else {
                ApiClient.fetchNotifications(userId)
            }
        } catch (e: Exception) {
            ApiClient.fetchNotifications(userId)
        }
    }

    suspend fun markNotificationRead(userId: Int = UserSession.userId, notificationId: String = "all"): NetworkResult<Int> {
        return ApiClient.markNotificationRead(userId, notificationId)
    }

    suspend fun clearNotifications(userId: Int = UserSession.userId): NetworkResult<Boolean> {
        return ApiClient.clearNotifications(userId)
    }

    suspend fun createNotification(
        userId: Int = UserSession.userId,
        title: String,
        description: String,
        badgeText: String = "REMINDER",
        iconType: String = "ALARM",
        category: String = "PRACTICE",
        isGreenBadge: Boolean = false
    ): NetworkResult<Int> {
        return ApiClient.createNotification(userId, title, description, badgeText, iconType, category, isGreenBadge)
    }

    suspend fun saveReminder(
        userId: Int = UserSession.userId,
        title: String,
        time: String,
        days: String,
        frequency: String = "Daily",
        skill: String = "Public Speaking",
        isEnabled: Boolean = true
    ): NetworkResult<Boolean> {
        return ApiClient.saveReminder(userId, title, time, days, frequency, skill, isEnabled)
    }
}
