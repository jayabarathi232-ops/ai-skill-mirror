package com.simats.aiskillmirror.data.repository

import com.simats.aiskillmirror.data.network.ApiClient
import com.simats.aiskillmirror.data.network.NetworkResult
import com.simats.aiskillmirror.data.network.ProfileData
import com.simats.aiskillmirror.data.network.UserSession

class ProfileRepository {
    suspend fun getProfile(userId: Int = UserSession.userId): NetworkResult<ProfileData> {
        val result = ApiClient.fetchProfile(userId)
        if (result.isSuccess && result.data != null) {
            UserSession.userName = result.data.name
            UserSession.userEmail = result.data.email
        }
        return result
    }

    suspend fun updateProfile(
        userId: Int = UserSession.userId,
        phone: String? = null,
        education: String? = null,
        skills: String? = null,
        careerGoal: String? = null,
        bio: String? = null,
        name: String? = null
    ): NetworkResult<Boolean> {
        val result = ApiClient.editProfile(userId, phone, education, skills, careerGoal, bio, name)
        if (result.isSuccess && !name.isNullOrBlank()) {
            UserSession.userName = name
        }
        return result
    }

    suspend fun uploadPicture(userId: Int = UserSession.userId, bytes: ByteArray, fileName: String): NetworkResult<String> {
        return ApiClient.uploadProfilePicture(userId, bytes, fileName)
    }
}

