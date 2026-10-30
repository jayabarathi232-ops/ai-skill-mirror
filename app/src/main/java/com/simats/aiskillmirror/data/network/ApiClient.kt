package com.simats.aiskillmirror.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

data class NetworkResult<T>(
    val isSuccess: Boolean,
    val message: String?,
    val data: T? = null
)

data class UserData(
    val id: Int,
    val name: String,
    val email: String
)

data class ProfileData(
    val userId: Int,
    val name: String,
    val email: String,
    val phone: String,
    val education: String,
    val skills: String,
    val careerGoal: String,
    val bio: String,
    val profilePicture: String = ""
)

data class DashboardData(
    val userName: String,
    val skillScore: Int,
    val monthlyPtsGain: Int,
    val currentStreakDays: Int,
    val minutesToday: Int,
    val goalMinutesToday: Int,
    val assessedSkillsCount: Int,
    val learnerStatus: String,
    val currentLessonTitle: String,
    val currentLessonCategory: String,
    val currentLessonDuration: String,
    val currentLessonProgress: Double
)

data class SkillItemData(
    val id: String,
    val name: String,
    val description: String,
    val lastScore: Int,
    val maxScore: Int,
    val iconType: String
)

data class QuestionOptionData(
    val key: String,
    val text: String,
    val points: Int
)

data class QuestionData(
    val id: Int,
    val topicBadge: String,
    val headline: String,
    val scenario: String,
    val options: List<QuestionOptionData>
)

data class AssessmentSubmitResult(
    val skillId: String,
    val score: Int,
    val overallScore: Int,
    val pointsGained: Int
)

data class SkillMirrorScoreData(
    val name: String,
    val score: Int,
    val maxScore: Int,
    val isTopTier: Boolean
)

data class SkillMirrorData(
    val overallScore: Int,
    val statusBadge: String,
    val pointsGainSubtitle: String,
    val skillCountText: String,
    val skillScores: List<SkillMirrorScoreData>,
    val nextMoveTitle: String,
    val nextMoveDescription: String,
    val lastUpdatedText: String
)

data class RoadmapStepData(
    val id: String,
    val title: String,
    val subtitle: String,
    val lessonCountText: String,
    val iconType: String
)

data class GrowthRoadmapData(
    val currentWeekBadgeText: String,
    val planTitle: String,
    val planSubtitle: String,
    val planProgress: Double,
    val planProgressText: String,
    val activeStepTitle: String,
    val activeStepSubtitle: String,
    val activeStepProgressPercentage: String,
    val activeStepDescription: String,
    val activeStepTags: List<String>,
    val upcomingSteps: List<RoadmapStepData>,
    val continueButtonText: String
)

data class TrainingLessonItem(
    val id: String,
    val title: String,
    val category: String,
    val durationText: String,
    val xpText: String,
    val isCompleted: Boolean
)

data class DailyTrainingData(
    val headerDateText: String,
    val streakBadgeText: String,
    val currentMinutes: Int,
    val targetMinutes: Int,
    val practiceProgressText: String,
    val completedPracticeItems: List<TrainingLessonItem>,
    val upNextTitle: String,
    val upNextCategory: String,
    val upNextMetaText: String,
    val upNextDescription: String,
    val startButtonText: String
)

data class SpeechAnalysisData(
    val speechScore: Int,
    val baselineGainText: String,
    val headlineLine1: String,
    val headlineLine2: String,
    val sessionDurationText: String,
    val pronunciationScore: Int,
    val fluencyScore: Int,
    val clarityScore: Int,
    val transcriptPart1: String,
    val transcriptHighlightPronunciation: String,
    val transcriptPart2: String,
    val transcriptHighlightFiller: String,
    val transcriptPart3: String,
    val aiTipText: String
)

data class MilestoneAchievementData(
    val id: String,
    val title: String,
    val earnedDateText: String,
    val iconType: String
)

data class MilestonesData(
    val mainGoalTitle: String,
    val currentScore: Int,
    val targetScore: Int,
    val statusText: String,
    val targetDateText: String,
    val streakTitle: String,
    val streakSubtitle: String,
    val streakProgressText: String,
    val earnedCountText: String,
    val achievements: List<MilestoneAchievementData>,
    val nextRewardTitle: String,
    val nextRewardSubtitle: String,
    val nextRewardTarget: Int,
    val totalXpText: String
)

data class NotificationItemData(
    val id: String,
    val title: String,
    val description: String,
    val timestampText: String,
    val badgeText: String?,
    val isGreenBadge: Boolean,
    val iconType: String,
    val category: String
)

data class NotificationsData(
    val isDailyReminderEnabled: Boolean,
    val reminderTimeText: String,
    val reminderNoticeText: String,
    val todayNotifications: List<NotificationItemData>,
    val yesterdayNotifications: List<NotificationItemData>
)

data class ChatMessageData(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: String
)

data class CoachChatData(
    val coachName: String,
    val statusText: String,
    val bannerNoticeText: String,
    val timestampHeader: String,
    val messages: List<ChatMessageData>,
    val suggestionChips: List<String>
)

data class UserSettingsData(
    val isLearningRemindersEnabled: Boolean,
    val isPersonalizedAiEnabled: Boolean,
    val isSaveVoiceRecordingsEnabled: Boolean,
    val currentAppearanceText: String,
    val currentLanguageText: String,
    val appVersionText: String
)

object ApiClient {
    // 10.0.2.2 reaches localhost on Android Emulator.
    // For a physical device on the same Wi-Fi, change to your PC's IP, e.g. "http://172.23.49.166/skillmirrorbackend"
    var BASE_URL = "http://10.0.2.2/skillmirrorbackend"

    private fun postJson(endpoint: String, bodyJson: JSONObject): Pair<Int, String> {
        val url = URL("$BASE_URL/$endpoint")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            setRequestProperty("Accept", "application/json")
            doOutput = true
            doInput = true
            connectTimeout = 8000
            readTimeout = 8000
        }
        OutputStreamWriter(conn.outputStream).use { writer ->
            writer.write(bodyJson.toString())
            writer.flush()
        }
        val code = conn.responseCode
        val stream = if (code in 200..299) conn.inputStream else conn.errorStream
        val text = stream?.bufferedReader()?.use(BufferedReader::readText) ?: ""
        conn.disconnect()
        return Pair(code, text)
    }

    private fun getJson(endpointWithQuery: String): Pair<Int, String> {
        val url = URL("$BASE_URL/$endpointWithQuery")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("Accept", "application/json")
            connectTimeout = 8000
            readTimeout = 8000
        }
        val code = conn.responseCode
        val stream = if (code in 200..299) conn.inputStream else conn.errorStream
        val text = stream?.bufferedReader()?.use(BufferedReader::readText) ?: ""
        conn.disconnect()
        return Pair(code, text)
    }

    suspend fun login(email: String, password: String): NetworkResult<UserData> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("email", email.trim())
                put("password", password)
            }
            val (code, text) = postJson("auth/login.php", body)
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            val status = json.optString("status")
            val message = json.optString("message", "Login response received")

            if (code == 200 && status == "success") {
                val userObj = json.optJSONObject("user")
                val id = userObj?.optInt("id") ?: userObj?.optInt("user_id") ?: 0
                val name = userObj?.optString("name").orEmpty()
                val userEmail = userObj?.optString("email").orEmpty()
                NetworkResult(true, message, UserData(id, name, userEmail))
            } else {
                NetworkResult(false, message)
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage ?: "Unable to connect to server"}")
        }
    }

    suspend fun signup(name: String, email: String, password: String): NetworkResult<UserData> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("name", name.trim())
                put("email", email.trim())
                put("password", password)
            }
            val (code, text) = postJson("auth/signup.php", body)
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            val status = json.optString("status")
            val message = json.optString("message", "Signup response received")

            if ((code == 200 || code == 201) && status == "success") {
                val userObj = json.optJSONObject("user")
                val id = userObj?.optInt("id") ?: json.optInt("user_id", 0)
                val userName = userObj?.optString("name") ?: name
                val userEmail = userObj?.optString("email") ?: email
                NetworkResult(true, message, UserData(id, userName, userEmail))
            } else {
                NetworkResult(false, message)
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage ?: "Unable to connect to server"}")
        }
    }

    suspend fun requestPasswordRecovery(email: String): NetworkResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply { put("email", email.trim()) }
            val (code, text) = postJson("auth/recovery.php", body)
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            val status = json.optString("status")
            val message = json.optString("message", "Password recovery requested")
            NetworkResult(code == 200 && status == "success", message, true)
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}", false)
        }
    }

    suspend fun fetchProfile(userId: Int): NetworkResult<ProfileData> = withContext(Dispatchers.IO) {
        try {
            val (code, text) = getJson("profile/profile_fetch.php?user_id=$userId")
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            val status = json.optString("status")
            val message = json.optString("message", "Profile fetched")

            if (code == 200 && status == "success") {
                val p = json.optJSONObject("profile")
                if (p != null) {
                    val profile = ProfileData(
                        userId = p.optInt("user_id", userId),
                        name = p.optString("name", ""),
                        email = p.optString("email", ""),
                        phone = p.optString("phone", ""),
                        education = p.optString("education", ""),
                        skills = p.optString("skills", ""),
                        careerGoal = p.optString("career_goal", ""),
                        bio = p.optString("bio", ""),
                        profilePicture = p.optString("profile_picture", "")
                    )
                    NetworkResult(true, message, profile)
                } else {
                    NetworkResult(false, "No profile data in response")
                }
            } else {
                NetworkResult(false, message)
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage ?: "Unable to connect to server"}")
        }
    }

    suspend fun editProfile(
        userId: Int,
        phone: String? = null,
        education: String? = null,
        skills: String? = null,
        careerGoal: String? = null,
        bio: String? = null,
        name: String? = null
    ): NetworkResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("user_id", userId)
                phone?.let { put("phone", it) }
                education?.let { put("education", it) }
                skills?.let { put("skills", it) }
                careerGoal?.let { put("career_goal", it) }
                bio?.let { put("bio", it) }
                name?.let { put("name", it) }
            }
            val (code, text) = postJson("profile/profile_edit.php", body)
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            val status = json.optString("status")
            val message = json.optString("message", "Profile updated")
            NetworkResult(code == 200 && status == "success", message, true)
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage ?: "Unable to connect to server"}", false)
        }
    }

    suspend fun uploadProfilePicture(userId: Int, imageBytes: ByteArray, fileName: String): NetworkResult<String> = withContext(Dispatchers.IO) {
        try {
            val boundary = "==Boundary_${System.currentTimeMillis()}=="
            val url = URL("$BASE_URL/profile/profile_upload_picture.php")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
                doOutput = true
                doInput = true
                connectTimeout = 15000
                readTimeout = 15000
            }

            conn.outputStream.use { os ->
                // Write user_id
                os.write("--$boundary\r\n".toByteArray())
                os.write("Content-Disposition: form-data; name=\"user_id\"\r\n\r\n".toByteArray())
                os.write("$userId\r\n".toByteArray())

                // Write file
                os.write("--$boundary\r\n".toByteArray())
                os.write("Content-Disposition: form-data; name=\"profile_picture\"; filename=\"$fileName\"\r\n".toByteArray())
                os.write("Content-Type: image/jpeg\r\n\r\n".toByteArray())
                os.write(imageBytes)
                os.write("\r\n".toByteArray())

                // Close boundary
                os.write("--$boundary--\r\n".toByteArray())
                os.flush()
            }

            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val text = stream?.bufferedReader()?.use(BufferedReader::readText) ?: ""
            conn.disconnect()

            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            val status = json.optString("status")
            val message = json.optString("message")

            if (code == 200 && status == "success") {
                val dataObj = json.optJSONObject("data")
                val path = dataObj?.optString("profile_picture") ?: ""
                NetworkResult(true, message, path)
            } else {
                NetworkResult(false, message)
            }
        } catch (e: Exception) {
            NetworkResult(false, "Upload error: ${e.localizedMessage}")
        }
    }

    suspend fun fetchDashboard(userId: Int): NetworkResult<DashboardData> = withContext(Dispatchers.IO) {
        try {
            val (code, text) = getJson("dashboard/dashboard_fetch.php?user_id=$userId")
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            val status = json.optString("status")
            val message = json.optString("message")

            if (code == 200 && status == "success") {
                val d = json.getJSONObject("data")
                val data = DashboardData(
                    userName = d.optString("userName", "Learner"),
                    skillScore = d.optInt("skillScore", 78),
                    monthlyPtsGain = d.optInt("monthlyPtsGain", 14),
                    currentStreakDays = d.optInt("currentStreakDays", 7),
                    minutesToday = d.optInt("minutesToday", 14),
                    goalMinutesToday = d.optInt("goalMinutesToday", 20),
                    assessedSkillsCount = d.optInt("assessedSkillsCount", 4),
                    learnerStatus = d.optString("learnerStatus", "Advanced learner"),
                    currentLessonTitle = d.optString("currentLessonTitle", "Speak with confidence"),
                    currentLessonCategory = d.optString("currentLessonCategory", "Communication"),
                    currentLessonDuration = d.optString("currentLessonDuration", "6 min"),
                    currentLessonProgress = d.optDouble("currentLessonProgress", 0.6)
                )
                NetworkResult(true, message, data)
            } else {
                NetworkResult(false, message)
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}")
        }
    }

    suspend fun fetchSkills(userId: Int): NetworkResult<List<SkillItemData>> = withContext(Dispatchers.IO) {
        try {
            val (code, text) = getJson("assessment/skills_fetch.php?user_id=$userId")
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            val status = json.optString("status")

            if (code == 200 && status == "success") {
                val arr = json.getJSONObject("data").getJSONArray("skills")
                val list = mutableListOf<SkillItemData>()
                for (i in 0 until arr.length()) {
                    val s = arr.getJSONObject(i)
                    list.add(
                        SkillItemData(
                            id = s.optString("id"),
                            name = s.optString("name"),
                            description = s.optString("description"),
                            lastScore = s.optInt("lastScore", 70),
                            maxScore = s.optInt("maxScore", 100),
                            iconType = s.optString("iconType", "TECHNICAL")
                        )
                    )
                }
                NetworkResult(true, "Skills fetched", list)
            } else {
                NetworkResult(false, json.optString("message"))
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}")
        }
    }

    suspend fun fetchQuestions(skillId: String): NetworkResult<List<QuestionData>> = withContext(Dispatchers.IO) {
        try {
            val (code, text) = getJson("assessment/questions_fetch.php?skill_id=$skillId")
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            val status = json.optString("status")

            if (code == 200 && status == "success") {
                val arr = json.getJSONObject("data").getJSONArray("questions")
                val list = mutableListOf<QuestionData>()
                for (i in 0 until arr.length()) {
                    val q = arr.getJSONObject(i)
                    val optsArr = q.getJSONArray("options")
                    val optsList = mutableListOf<QuestionOptionData>()
                    for (j in 0 until optsArr.length()) {
                        val o = optsArr.getJSONObject(j)
                        optsList.add(QuestionOptionData(o.optString("key"), o.optString("text"), o.optInt("points")))
                    }
                    list.add(
                        QuestionData(
                            id = q.optInt("id"),
                            topicBadge = q.optString("topicBadge"),
                            headline = q.optString("headline"),
                            scenario = q.optString("scenario"),
                            options = optsList
                        )
                    )
                }
                NetworkResult(true, "Questions fetched", list)
            } else {
                NetworkResult(false, json.optString("message"))
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}")
        }
    }

    suspend fun submitAssessment(userId: Int, skillId: String, answers: List<Pair<Int, String>>): NetworkResult<AssessmentSubmitResult> = withContext(Dispatchers.IO) {
        try {
            val ansArr = JSONArray()
            answers.forEach { (qId, key) ->
                ansArr.put(JSONObject().apply {
                    put("question_id", qId)
                    put("selected_key", key)
                })
            }
            val body = JSONObject().apply {
                put("user_id", userId)
                put("skill_id", skillId)
                put("answers", ansArr)
            }
            val (code, text) = postJson("assessment/submit_assessment.php", body)
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            if (code == 200 && json.optString("status") == "success") {
                val d = json.getJSONObject("data")
                val res = AssessmentSubmitResult(
                    skillId = d.optString("skillId"),
                    score = d.optInt("score"),
                    overallScore = d.optInt("overallScore"),
                    pointsGained = d.optInt("pointsGained")
                )
                NetworkResult(true, "Assessment evaluated", res)
            } else {
                NetworkResult(false, json.optString("message"))
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}")
        }
    }

    suspend fun fetchSkillMirror(userId: Int): NetworkResult<SkillMirrorData> = withContext(Dispatchers.IO) {
        try {
            val (code, text) = getJson("progress/skill_mirror_fetch.php?user_id=$userId")
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            if (code == 200 && json.optString("status") == "success") {
                val d = json.getJSONObject("data")
                val arr = d.getJSONArray("skillScores")
                val scores = mutableListOf<SkillMirrorScoreData>()
                for (i in 0 until arr.length()) {
                    val s = arr.getJSONObject(i)
                    scores.add(
                        SkillMirrorScoreData(
                            name = s.optString("name"),
                            score = s.optInt("score"),
                            maxScore = s.optInt("maxScore", 100),
                            isTopTier = s.optBoolean("isTopTier")
                        )
                    )
                }
                val data = SkillMirrorData(
                    overallScore = d.optInt("overallScore"),
                    statusBadge = d.optString("statusBadge"),
                    pointsGainSubtitle = d.optString("pointsGainSubtitle"),
                    skillCountText = d.optString("skillCountText"),
                    skillScores = scores,
                    nextMoveTitle = d.optString("nextMoveTitle"),
                    nextMoveDescription = d.optString("nextMoveDescription"),
                    lastUpdatedText = d.optString("lastUpdatedText")
                )
                NetworkResult(true, "Skill mirror fetched", data)
            } else {
                NetworkResult(false, json.optString("message"))
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}")
        }
    }

    suspend fun fetchRoadmap(userId: Int): NetworkResult<GrowthRoadmapData> = withContext(Dispatchers.IO) {
        try {
            val (code, text) = getJson("roadmap/roadmap_fetch.php?user_id=$userId")
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            if (code == 200 && json.optString("status") == "success") {
                val d = json.getJSONObject("data")
                val arr = d.getJSONArray("upcomingSteps")
                val steps = mutableListOf<RoadmapStepData>()
                for (i in 0 until arr.length()) {
                    val s = arr.getJSONObject(i)
                    steps.add(
                        RoadmapStepData(
                            id = s.optString("id"),
                            title = s.optString("title"),
                            subtitle = s.optString("subtitle"),
                            lessonCountText = s.optString("lessonCountText"),
                            iconType = s.optString("iconType")
                        )
                    )
                }
                val tagsArr = d.optJSONArray("activeStepTags")
                val tags = mutableListOf<String>()
                if (tagsArr != null) {
                    for (i in 0 until tagsArr.length()) tags.add(tagsArr.getString(i))
                }
                val data = GrowthRoadmapData(
                    currentWeekBadgeText = d.optString("currentWeekBadgeText"),
                    planTitle = d.optString("planTitle"),
                    planSubtitle = d.optString("planSubtitle"),
                    planProgress = d.optDouble("planProgress", 0.3),
                    planProgressText = d.optString("planProgressText"),
                    activeStepTitle = d.optString("activeStepTitle"),
                    activeStepSubtitle = d.optString("activeStepSubtitle"),
                    activeStepProgressPercentage = d.optString("activeStepProgressPercentage"),
                    activeStepDescription = d.optString("activeStepDescription"),
                    activeStepTags = tags,
                    upcomingSteps = steps,
                    continueButtonText = d.optString("continueButtonText")
                )
                NetworkResult(true, "Roadmap fetched", data)
            } else {
                NetworkResult(false, json.optString("message"))
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}")
        }
    }

    suspend fun fetchTraining(userId: Int): NetworkResult<DailyTrainingData> = withContext(Dispatchers.IO) {
        try {
            val (code, text) = getJson("training/training_fetch.php?user_id=$userId")
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            if (code == 200 && json.optString("status") == "success") {
                val d = json.getJSONObject("data")
                val arr = d.getJSONArray("completedPracticeItems")
                val completed = mutableListOf<TrainingLessonItem>()
                for (i in 0 until arr.length()) {
                    val it = arr.getJSONObject(i)
                    completed.add(
                        TrainingLessonItem(
                            id = it.optString("id"),
                            title = it.optString("title"),
                            category = it.optString("category"),
                            durationText = it.optString("durationText"),
                            xpText = it.optString("xpText"),
                            isCompleted = it.optBoolean("isCompleted", true)
                        )
                    )
                }
                val data = DailyTrainingData(
                    headerDateText = d.optString("headerDateText"),
                    streakBadgeText = d.optString("streakBadgeText"),
                    currentMinutes = d.optInt("currentMinutes"),
                    targetMinutes = d.optInt("targetMinutes"),
                    practiceProgressText = d.optString("practiceProgressText"),
                    completedPracticeItems = completed,
                    upNextTitle = d.optString("upNextTitle"),
                    upNextCategory = d.optString("upNextCategory"),
                    upNextMetaText = d.optString("upNextMetaText"),
                    upNextDescription = d.optString("upNextDescription"),
                    startButtonText = d.optString("startButtonText")
                )
                NetworkResult(true, "Training fetched", data)
            } else {
                NetworkResult(false, json.optString("message"))
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}")
        }
    }

    suspend fun fetchMilestones(userId: Int): NetworkResult<MilestonesData> = withContext(Dispatchers.IO) {
        try {
            val (code, text) = getJson("milestones/milestones_fetch.php?user_id=$userId")
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            if (code == 200 && json.optString("status") == "success") {
                val d = json.getJSONObject("data")
                val arr = d.getJSONArray("achievements")
                val achs = mutableListOf<MilestoneAchievementData>()
                for (i in 0 until arr.length()) {
                    val a = arr.getJSONObject(i)
                    achs.add(
                        MilestoneAchievementData(
                            id = a.optString("id"),
                            title = a.optString("title"),
                            earnedDateText = a.optString("earnedDateText"),
                            iconType = a.optString("iconType")
                        )
                    )
                }
                val data = MilestonesData(
                    mainGoalTitle = d.optString("mainGoalTitle"),
                    currentScore = d.optInt("currentScore"),
                    targetScore = d.optInt("targetScore"),
                    statusText = d.optString("statusText"),
                    targetDateText = d.optString("targetDateText"),
                    streakTitle = d.optString("streakTitle"),
                    streakSubtitle = d.optString("streakSubtitle"),
                    streakProgressText = d.optString("streakProgressText"),
                    earnedCountText = d.optString("earnedCountText"),
                    achievements = achs,
                    nextRewardTitle = d.optString("nextRewardTitle"),
                    nextRewardSubtitle = d.optString("nextRewardSubtitle"),
                    nextRewardTarget = d.optInt("nextRewardTarget"),
                    totalXpText = d.optString("totalXpText")
                )
                NetworkResult(true, "Milestones fetched", data)
            } else {
                NetworkResult(false, json.optString("message"))
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}")
        }
    }

    suspend fun fetchNotifications(userId: Int): NetworkResult<NotificationsData> = withContext(Dispatchers.IO) {
        try {
            val (code, text) = getJson("notifications/notifications_fetch.php?user_id=$userId")
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            if (code == 200 && json.optString("status") == "success") {
                val d = json.getJSONObject("data")
                fun parseArr(arr: JSONArray): List<NotificationItemData> {
                    val list = mutableListOf<NotificationItemData>()
                    for (i in 0 until arr.length()) {
                        val n = arr.getJSONObject(i)
                        list.add(
                            NotificationItemData(
                                id = n.optString("id"),
                                title = n.optString("title"),
                                description = n.optString("description"),
                                timestampText = n.optString("timestampText"),
                                badgeText = if (n.isNull("badgeText")) null else n.optString("badgeText"),
                                isGreenBadge = n.optBoolean("isGreenBadge"),
                                iconType = n.optString("iconType"),
                                category = n.optString("category")
                            )
                        )
                    }
                    return list
                }
                val today = parseArr(d.getJSONArray("todayNotifications"))
                val yesterday = parseArr(d.getJSONArray("yesterdayNotifications"))
                val data = NotificationsData(
                    isDailyReminderEnabled = d.optBoolean("isDailyReminderEnabled", true),
                    reminderTimeText = d.optString("reminderTimeText"),
                    reminderNoticeText = d.optString("reminderNoticeText"),
                    todayNotifications = today,
                    yesterdayNotifications = yesterday
                )
                NetworkResult(true, "Notifications fetched", data)
            } else {
                NetworkResult(false, json.optString("message"))
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}")
        }
    }

    suspend fun fetchCoachChat(userId: Int): NetworkResult<CoachChatData> = withContext(Dispatchers.IO) {
        try {
            val (code, text) = getJson("coach/coach_chat_fetch.php?user_id=$userId")
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            if (code == 200 && json.optString("status") == "success") {
                val d = json.getJSONObject("data")
                val arr = d.getJSONArray("messages")
                val msgs = mutableListOf<ChatMessageData>()
                for (i in 0 until arr.length()) {
                    val m = arr.getJSONObject(i)
                    msgs.add(
                        ChatMessageData(
                            id = m.optString("id"),
                            text = m.optString("text"),
                            isUser = m.optBoolean("isUser"),
                            timestamp = m.optString("timestamp")
                        )
                    )
                }
                val chipsArr = d.optJSONArray("suggestionChips")
                val chips = mutableListOf<String>()
                if (chipsArr != null) {
                    for (i in 0 until chipsArr.length()) chips.add(chipsArr.getString(i))
                }
                val data = CoachChatData(
                    coachName = d.optString("coachName"),
                    statusText = d.optString("statusText"),
                    bannerNoticeText = d.optString("bannerNoticeText"),
                    timestampHeader = d.optString("timestampHeader"),
                    messages = msgs,
                    suggestionChips = chips
                )
                NetworkResult(true, "Coach chat fetched", data)
            } else {
                NetworkResult(false, json.optString("message"))
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}")
        }
    }

    suspend fun sendCoachMessage(userId: Int, message: String): NetworkResult<Pair<ChatMessageData, ChatMessageData>> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("user_id", userId)
                put("message", message)
            }
            val (code, text) = postJson("coach/coach_send_message.php", body)
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            if (code == 200 && json.optString("status") == "success") {
                val d = json.getJSONObject("data")
                val u = d.getJSONObject("userMessage")
                val c = d.getJSONObject("coachReply")
                val userMsg = ChatMessageData(u.optString("id"), u.optString("text"), true, u.optString("timestamp"))
                val coachMsg = ChatMessageData(c.optString("id"), c.optString("text"), false, c.optString("timestamp"))
                NetworkResult(true, "Sent", Pair(userMsg, coachMsg))
            } else {
                NetworkResult(false, json.optString("message"))
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}")
        }
    }

    suspend fun fetchSettings(userId: Int): NetworkResult<UserSettingsData> = withContext(Dispatchers.IO) {
        try {
            val (code, text) = getJson("settings/settings_fetch.php?user_id=$userId")
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            if (code == 200 && json.optString("status") == "success") {
                val d = json.getJSONObject("data")
                val data = UserSettingsData(
                    isLearningRemindersEnabled = d.optBoolean("isLearningRemindersEnabled", true),
                    isPersonalizedAiEnabled = d.optBoolean("isPersonalizedAiEnabled", true),
                    isSaveVoiceRecordingsEnabled = d.optBoolean("isSaveVoiceRecordingsEnabled", false),
                    currentAppearanceText = d.optString("currentAppearanceText", "Dark"),
                    currentLanguageText = d.optString("currentLanguageText", "English"),
                    appVersionText = d.optString("appVersionText", "AI Skills Mirror · Version 1.0.0")
                )
                NetworkResult(true, "Settings fetched", data)
            } else {
                NetworkResult(false, json.optString("message"))
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}")
        }
    }

    suspend fun updateSettings(
        userId: Int,
        reminders: Boolean? = null,
        ai: Boolean? = null,
        voice: Boolean? = null,
        appearance: String? = null,
        language: String? = null
    ): NetworkResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("user_id", userId)
                reminders?.let { put("isLearningRemindersEnabled", it) }
                ai?.let { put("isPersonalizedAiEnabled", it) }
                voice?.let { put("isSaveVoiceRecordingsEnabled", it) }
                appearance?.let { put("currentAppearanceText", it) }
                language?.let { put("currentLanguageText", it) }
            }
            val (code, text) = postJson("settings/settings_update.php", body)
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            NetworkResult(code == 200 && json.optString("status") == "success", json.optString("message"), true)
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}", false)
        }
    }
}
