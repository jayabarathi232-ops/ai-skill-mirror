package com.simats.aiskillmirror.data.repository

import com.simats.aiskillmirror.data.network.ApiClient
import com.simats.aiskillmirror.data.network.NetworkResult
import com.simats.aiskillmirror.data.network.UserData
import com.simats.aiskillmirror.data.network.UserSession
import com.simats.aiskillmirror.network.LoginRequest
import com.simats.aiskillmirror.network.RecoveryRequest
import com.simats.aiskillmirror.network.RetrofitClient
import com.simats.aiskillmirror.network.SignupRequest

class AuthRepository {
    suspend fun login(email: String, pass: String): NetworkResult<UserData> {
        return try {
            val response = RetrofitClient.apiService.login(LoginRequest(email.trim(), pass))
            if (response.isSuccessful && response.body()?.status == "success" && response.body()?.user != null) {
                val u = response.body()!!.user!!
                val user = UserData(u.getResolvedId(), u.name, u.email)
                UserSession.update(user.id, user.name, user.email)
                NetworkResult(true, response.body()?.message ?: "Login successful", user)
            } else {
                val fallback = ApiClient.login(email, pass)
                if (fallback.isSuccess && fallback.data != null) {
                    UserSession.update(fallback.data.id, fallback.data.name, fallback.data.email)
                }
                fallback
            }
        } catch (e: Exception) {
            val fallback = ApiClient.login(email, pass)
            if (fallback.isSuccess && fallback.data != null) {
                UserSession.update(fallback.data.id, fallback.data.name, fallback.data.email)
            }
            fallback
        }
    }

    suspend fun signup(name: String, email: String, pass: String): NetworkResult<UserData> {
        return try {
            val response = RetrofitClient.apiService.signup(SignupRequest(name.trim(), email.trim(), pass))
            if (response.isSuccessful && response.body()?.status == "success" && response.body()?.user != null) {
                val u = response.body()!!.user!!
                val user = UserData(u.getResolvedId(), u.name, u.email)
                UserSession.update(user.id, user.name, user.email)
                NetworkResult(true, response.body()?.message ?: "Signup successful", user)
            } else {
                val fallback = ApiClient.signup(name, email, pass)
                if (fallback.isSuccess && fallback.data != null) {
                    UserSession.update(fallback.data.id, fallback.data.name, fallback.data.email)
                }
                fallback
            }
        } catch (e: Exception) {
            val fallback = ApiClient.signup(name, email, pass)
            if (fallback.isSuccess && fallback.data != null) {
                UserSession.update(fallback.data.id, fallback.data.name, fallback.data.email)
            }
            fallback
        }
    }

    suspend fun recoverPassword(email: String): NetworkResult<Boolean> {
        return try {
            val response = RetrofitClient.apiService.recoverPassword(RecoveryRequest(email.trim()))
            if (response.isSuccessful && response.body()?.status == "success") {
                NetworkResult(true, response.body()?.message ?: "Reset link sent", true)
            } else {
                ApiClient.requestPasswordRecovery(email)
            }
        } catch (e: Exception) {
            ApiClient.requestPasswordRecovery(email)
        }
    }

    fun logout() {
        UserSession.update(0, "", "")
    }
}
