package com.simats.aiskillmirror.data.repository

import com.simats.aiskillmirror.data.network.ApiClient
import com.simats.aiskillmirror.data.network.AssessmentSubmitResult
import com.simats.aiskillmirror.data.network.NetworkResult
import com.simats.aiskillmirror.data.network.QuestionData
import com.simats.aiskillmirror.data.network.QuestionOptionData
import com.simats.aiskillmirror.data.network.SkillItemData
import com.simats.aiskillmirror.data.network.UserSession
import com.simats.aiskillmirror.network.RetrofitClient
import com.simats.aiskillmirror.network.SubmitAnswerDto
import com.simats.aiskillmirror.network.SubmitAssessmentRequest

class AssessmentRepository {
    suspend fun getSkills(userId: Int = UserSession.userId): NetworkResult<List<SkillItemData>> {
        return try {
            val response = RetrofitClient.apiService.getSkills(userId)
            if (response.isSuccessful && response.body()?.status == "success" && response.body()?.data != null) {
                val list = response.body()!!.data!!.map { s ->
                    SkillItemData(
                        id = s.id,
                        name = s.name,
                        description = s.description,
                        lastScore = s.lastScore,
                        maxScore = s.maxScore,
                        iconType = s.iconType
                    )
                }
                NetworkResult(true, response.body()?.message, list)
            } else {
                ApiClient.fetchSkills(userId)
            }
        } catch (e: Exception) {
            ApiClient.fetchSkills(userId)
        }
    }

    suspend fun getQuestions(skillId: String): NetworkResult<List<QuestionData>> {
        return try {
            val response = RetrofitClient.apiService.getQuestions(skillId)
            if (response.isSuccessful && response.body()?.status == "success" && response.body()?.data != null) {
                val list = response.body()!!.data!!.map { q ->
                    QuestionData(
                        id = q.id,
                        topicBadge = q.topicBadge,
                        headline = q.headline,
                        scenario = q.scenario,
                        options = q.options.map { o ->
                            QuestionOptionData(
                                key = o.key,
                                text = o.text,
                                points = o.points
                            )
                        }
                    )
                }
                NetworkResult(true, response.body()?.message, list)
            } else {
                ApiClient.fetchQuestions(skillId)
            }
        } catch (e: Exception) {
            ApiClient.fetchQuestions(skillId)
        }
    }

    suspend fun submitAssessment(
        userId: Int = UserSession.userId,
        skillId: String,
        answers: List<Pair<Int, String>>
    ): NetworkResult<AssessmentSubmitResult> {
        return try {
            val dtoList = answers.map { SubmitAnswerDto(it.first, it.second) }
            val response = RetrofitClient.apiService.submitAssessment(
                SubmitAssessmentRequest(userId, skillId, dtoList)
            )
            if (response.isSuccessful && response.body()?.status == "success" && response.body()?.data != null) {
                val d = response.body()!!.data!!
                val result = AssessmentSubmitResult(
                    skillId = d.skillId,
                    score = d.score,
                    overallScore = d.overallScore,
                    pointsGained = d.pointsGained
                )
                NetworkResult(true, response.body()?.message, result)
            } else {
                ApiClient.submitAssessment(userId, skillId, answers)
            }
        } catch (e: Exception) {
            ApiClient.submitAssessment(userId, skillId, answers)
        }
    }
}
