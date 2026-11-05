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
                val resolvedPic = ApiClient.resolveImageUrl(p.profilePicture)
                val data = ProfileData(
                    userId = p.userId,
                    name = p.name,
                    email = p.email,
                    phone = p.phone.orEmpty(),
                    education = p.education.orEmpty(),
                    skills = p.skills.orEmpty(),
                    careerGoal = p.careerGoal.orEmpty(),
                    bio = p.bio.orEmpty(),
                    profilePicture = resolvedPic
                )
                UserSession.userName = data.name
                UserSession.userEmail = data.email
                if (resolvedPic.isNotBlank()) {
                    UserSession.profilePictureUrl = resolvedPic
                }
                NetworkResult(true, response.body()?.message, data)
            } else {
                val apiRes = ApiClient.fetchProfile(userId)
                if (apiRes.isSuccess && apiRes.data != null) {
                    UserSession.userName = apiRes.data.name
                    UserSession.userEmail = apiRes.data.email
                    if (apiRes.data.profilePicture.isNotBlank()) {
                        UserSession.profilePictureUrl = apiRes.data.profilePicture
                    }
                }
                apiRes
            }
        } catch (e: Exception) {
            val apiRes = ApiClient.fetchProfile(userId)
            if (apiRes.isSuccess && apiRes.data != null) {
                UserSession.userName = apiRes.data.name
                UserSession.userEmail = apiRes.data.email
                if (apiRes.data.profilePicture.isNotBlank()) {
                    UserSession.profilePictureUrl = apiRes.data.profilePicture
                }
            }
            apiRes
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
        val res = ApiClient.uploadProfilePicture(userId, bytes, fileName)
        if (res.isSuccess && !res.data.isNullOrBlank()) {
            UserSession.profilePictureUrl = res.data
        }
        return res
    }

    suspend fun removePicture(userId: Int = UserSession.userId): NetworkResult<Boolean> {
        val res = ApiClient.removeProfilePicture(userId)
        if (res.isSuccess) {
            UserSession.profilePictureUrl = ""
        }
        return res
    }
}
