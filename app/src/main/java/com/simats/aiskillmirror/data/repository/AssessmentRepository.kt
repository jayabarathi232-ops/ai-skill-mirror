package com.simats.aiskillmirror.data.repository

import com.simats.aiskillmirror.data.network.ApiClient
import com.simats.aiskillmirror.data.network.AssessmentSubmitResult
import com.simats.aiskillmirror.data.network.NetworkResult
import com.simats.aiskillmirror.data.network.QuestionData
import com.simats.aiskillmirror.data.network.SkillItemData
import com.simats.aiskillmirror.data.network.UserSession

class AssessmentRepository {
    suspend fun getSkills(userId: Int = UserSession.userId): NetworkResult<List<SkillItemData>> {
        return ApiClient.fetchSkills(userId)
    }

    suspend fun getQuestions(skillId: String): NetworkResult<List<QuestionData>> {
        return ApiClient.fetchQuestions(skillId)
    }

    suspend fun submitAssessment(
        userId: Int = UserSession.userId,
        skillId: String,
        answers: List<Pair<Int, String>>
    ): NetworkResult<AssessmentSubmitResult> {
        return ApiClient.submitAssessment(userId, skillId, answers)
    }
}

