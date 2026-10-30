package com.simats.aiskillmirror.data.repository

import com.simats.aiskillmirror.data.network.ApiClient
import com.simats.aiskillmirror.data.network.GrowthRoadmapData
import com.simats.aiskillmirror.data.network.MilestonesData
import com.simats.aiskillmirror.data.network.NetworkResult
import com.simats.aiskillmirror.data.network.NotificationsData
import com.simats.aiskillmirror.data.network.SkillMirrorData
import com.simats.aiskillmirror.data.network.UserSession

class ProgressRepository {
    suspend fun getSkillMirror(userId: Int = UserSession.userId): NetworkResult<SkillMirrorData> {
        return ApiClient.fetchSkillMirror(userId)
    }

    suspend fun getRoadmap(userId: Int = UserSession.userId): NetworkResult<GrowthRoadmapData> {
        return ApiClient.fetchRoadmap(userId)
    }

    suspend fun getMilestones(userId: Int = UserSession.userId): NetworkResult<MilestonesData> {
        return ApiClient.fetchMilestones(userId)
    }

    suspend fun getNotifications(userId: Int = UserSession.userId): NetworkResult<NotificationsData> {
        return ApiClient.fetchNotifications(userId)
    }
}

