package com.simats.aiskillmirror.data.repository

import com.simats.aiskillmirror.data.network.ApiClient
import com.simats.aiskillmirror.data.network.DashboardData
import com.simats.aiskillmirror.data.network.NetworkResult
import com.simats.aiskillmirror.data.network.UserSession

class DashboardRepository {
    suspend fun getDashboard(userId: Int = UserSession.userId): NetworkResult<DashboardData> {
        return ApiClient.fetchDashboard(userId)
    }
}

