package com.simats.aiskillmirror.data.repository

import com.simats.aiskillmirror.data.network.ApiClient
import com.simats.aiskillmirror.data.network.NetworkResult
import com.simats.aiskillmirror.data.network.ProfileData
import com.simats.aiskillmirror.data.network.UserSession
import com.simats.aiskillmirror.network.ProfileEditRequest
import com.simats.aiskillmirror.network.RetrofitClient

class ProfileRepository {
    suspend fun getProfile(userId: Int = UserSession.userId): NetworkResult<ProfileData> {
        return try {
            val response = RetrofitClient.apiService.getProfile(userId)
            if (response.isSuccessful && response.body()?.status == "success" && response.body()?.profile != null) {
                val p = response.body()!!.profile!!
                val data = ProfileData(
                    userId = p.userId,
                    name = p.name,
                    email = p.email,
                    phone = p.phone.orEmpty(),
                    education = p.education.orEmpty(),
                    skills = p.skills.orEmpty(),
                    careerGoal = p.careerGoal.orEmpty(),
                    bio = p.bio.orEmpty(),
                    profilePicture = p.profilePicture.orEmpty()
                )
                UserSession.userName = data.name
                UserSession.userEmail = data.email
                NetworkResult(true, response.body()?.message, data)
            } else {
                ApiClient.fetchProfile(userId)
            }
        } catch (e: Exception) {
            ApiClient.fetchProfile(userId)
        }
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
        return try {
            val req = ProfileEditRequest(
                userId = userId,
                name = name,
                phone = phone,
                education = education,
                skills = skills,
                careerGoal = careerGoal,
                bio = bio
            )
            val response = RetrofitClient.apiService.editProfile(req)
            if (response.isSuccessful && response.body()?.status == "success") {
                if (!name.isNullOrBlank()) {
                    UserSession.userName = name
                }
                NetworkResult(true, response.body()?.message ?: "Profile updated successfully", true)
            } else {
                ApiClient.editProfile(userId, phone, education, skills, careerGoal, bio, name)
            }
        } catch (e: Exception) {
            ApiClient.editProfile(userId, phone, education, skills, careerGoal, bio, name)
        }
    }

    suspend fun uploadPicture(userId: Int = UserSession.userId, bytes: ByteArray, fileName: String): NetworkResult<String> {
        return ApiClient.uploadProfilePicture(userId, bytes, fileName)
    }
}
