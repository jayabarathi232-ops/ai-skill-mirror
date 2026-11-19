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
    val email: String,
    val profilePicture: String = ""
)

data class PracticeTaskDetail(
    val id: Int = 0,
    val type: String = "PRACTICE",
    val title: String = "",
    val skill: String = "",
    val durationMinutes: Int = 0,
    val xpAwarded: Int = 0,
    val score: Int? = null
)

data class DailyProgressItem(
    val day: String = "Mon",
    val dayFull: String = "Monday",
    val date: String = "",
    val formattedDate: String = "",
    val minutes: Int = 0,
    val tasks: Int = 0,
    val xp: Int = 0,
    val isToday: Boolean = false,
    val hasActivity: Boolean = false,
    val skillsPracticed: List<String> = emptyList(),
    val tasksDetail: List<PracticeTaskDetail> = emptyList()
)

fun createDefaultWeekDays(weekOffset: Int = 0): List<DailyProgressItem> {
    val dayNamesShort = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val dayNamesFull = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    val cal = java.util.Calendar.getInstance()
    val todayCal = java.util.Calendar.getInstance()

    val dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK)
    val daysFromMonday = if (dayOfWeek == java.util.Calendar.SUNDAY) 6 else (dayOfWeek - java.util.Calendar.MONDAY)
    cal.add(java.util.Calendar.DAY_OF_YEAR, -daysFromMonday + (weekOffset * 7))

    val sdfDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
    val sdfFormat = java.text.SimpleDateFormat("MMM d", java.util.Locale.US)
    val todayDateStr = sdfDate.format(todayCal.time)

    return (0..6).map { i ->
        val dateStr = sdfDate.format(cal.time)
        val formattedDate = sdfFormat.format(cal.time)
        val isToday = (dateStr == todayDateStr)
        val item = DailyProgressItem(
            day = dayNamesShort[i],
            dayFull = dayNamesFull[i],
            date = dateStr,
            formattedDate = formattedDate,
            minutes = 0,
            tasks = 0,
            xp = 0,
            isToday = isToday,
            hasActivity = false,
            skillsPracticed = emptyList(),
            tasksDetail = emptyList()
        )
        cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
        item
    }
}

data class ProfileWeeklyProgressData(
    val weekOffset: Int = 0,
    val weekLabel: String = "This Week",
    val mondayDate: String = "",
    val sundayDate: String = "",
    val days: List<DailyProgressItem> = createDefaultWeekDays(0),
    val totalWeeklyMinutes: Int = 0,
    val weeklyCompletedTasks: Int = 0,
    val weeklyXpEarned: Int = 0,
    val weeklyGoalMinutes: Int = 60,
    val weeklyGoalPercentage: Int = 0
)

data class ProfileBadgeItem(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val iconType: String = "TROPHY",
    val isUnlocked: Boolean = false,
    val earnedDate: String? = null
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
    val profilePicture: String = "",
    val skillScore: Int = 0,
    val totalSessions: Int = 0,
    val dayStreak: Int = 0,
    val totalXp: Int = 0,
    val memberSinceText: String = "Growing with AI Skills Mirror",
    val weeklyProgress: ProfileWeeklyProgressData = ProfileWeeklyProgressData(),
    val achievedBadges: List<ProfileBadgeItem> = emptyList(),
    val lockedBadges: List<ProfileBadgeItem> = emptyList()
)

data class CodeTestCaseResult(
    val name: String,
    val passed: Boolean,
    val input: String,
    val expected: String,
    val actual: String
)

data class CodeExecutionResult(
    val language: String,
    val output: String,
    val error: String,
    val hasError: Boolean,
    val testCases: List<CodeTestCaseResult>,
    val allTestsPassed: Boolean,
    val executionTimeMs: Int,
    val explanation: String
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
    val currentLessonProgress: Double,
    val greeting: String = "Good morning,",
    val currentDateText: String = "",
    val headline: String = "Your future is taking shape.",
    val streakSubtitle: String = "Your best week yet",
    val minutesSubtitle: String = "to your goal",
    val currentPlanTitle: String = "",
    val profilePicture: String = ""
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

data class RoadmapTaskData(
    val id: String,
    val title: String,
    val durationText: String = "10 mins",
    val isDone: Boolean = false
)

data class RoadmapQuestionData(
    val id: Int,
    val question: String,
    val options: List<String> = emptyList(),
    val correct: Int = 0,
    val explanation: String = ""
)

data class RoadmapStageData(
    val id: Int,
    val stageOrder: Int,
    val stageLevel: String,
    val topicName: String,
    val topicDescription: String,
    val learningObjectives: List<String> = emptyList(),
    val practiceInstructions: String = "",
    val estimatedDuration: String = "15 mins",
    val difficulty: String = "Beginner",
    val isCompleted: Boolean = false,
    val isUnlocked: Boolean = false,
    val scoreAchieved: Int? = null,
    val needsRemedial: Boolean = false,
    val remedialFeedback: String = "",
    val practiceTasks: List<RoadmapTaskData> = emptyList(),
    val assessmentQuestions: List<RoadmapQuestionData> = emptyList()
)

data class RoadmapAiFeedbackData(
    val coachAdvice: String = "",
    val hasRemedialAlert: Boolean = false,
    val remedialFeedback: String = "",
    val strengths: List<String> = emptyList(),
    val nextGoal: String = ""
)

data class QuizReviewItem(
    val id: Int = 0,
    val question: String = "",
    val options: List<String> = emptyList(),
    val userChoice: Int = -1,
    val correctChoice: Int = 0,
    val explanation: String = "",
    val isCorrect: Boolean = false
)

data class CompleteTopicResultData(
    val passed: Boolean,
    val score: Int,
    val xpAwarded: Int,
    val message: String,
    val totalQuestions: Int = 5,
    val correctAnswers: Int = 0,
    val incorrectAnswers: Int = 0,
    val reviewQuestions: List<QuizReviewItem> = emptyList(),
    val nextTopicUnlocked: String? = null,
    val remedialAdvice: String? = null,
    val remedialFeedback: String? = remedialAdvice,
    val promotedLevel: String? = null
)

data class GrowthRoadmapData(
    val userId: Int = 1,
    val activeSubcategory: SubcategoryItemData? = null,
    val availableSubcategories: List<SubcategoryItemData> = emptyList(),
    val roadmapProgress: Float = 0.0f,
    val roadmapProgressPercentage: Int = 0,
    val roadmapProgressText: String = "",
    val completedCount: Int = 0,
    val totalCount: Int = 5,
    val totalPracticeMinutes: Int = 0,
    val currentStageTitle: String = "",
    val currentStageOrder: Int = 1,
    val stages: List<RoadmapStageData> = emptyList(),
    val aiFeedback: RoadmapAiFeedbackData? = null,

    // Legacy fields
    val currentWeekBadgeText: String = "",
    val planTitle: String = "",
    val planSubtitle: String = "",
    val planProgress: Double = 0.0,
    val planProgressText: String = "",
    val activeStepTitle: String = "",
    val activeStepSubtitle: String = "",
    val activeStepProgressPercentage: String = "",
    val activeStepDescription: String = "",
    val activeStepTags: List<String> = emptyList(),
    val upcomingSteps: List<RoadmapStepData> = emptyList(),
    val continueButtonText: String = ""
)

data class TrainingLessonItem(
    val id: String,
    val title: String,
    val category: String,
    val durationText: String,
    val xpText: String,
    val isCompleted: Boolean,
    val performanceScore: Int? = null,
    val scoreStatus: String = "not_available",
    val scoreReason: String? = null,
    val strengths: List<String> = emptyList(),
    val weaknesses: List<String> = emptyList(),
    val aiFeedback: String = "",
    val metrics: Map<String, Any> = emptyMap(),
    val assessmentType: String = ""
)

data class PerformanceScoreData(
    val score: Int? = null,
    val scoreStatus: String = "not_available",
    val scoreReason: String? = null,
    val strengths: List<String> = emptyList(),
    val weaknesses: List<String> = emptyList(),
    val aiFeedback: String = "",
    val metrics: Map<String, Any> = emptyMap(),
    val assessmentType: String = "none",
    val taskTitle: String = "",
    val durationMinutes: Int = 0,
    val xpEarned: Int = 35
)

data class LessonCompleteResultData(
    val lessonId: String?,
    val minutesToday: Int,
    val goalMinutesToday: Int,
    val streakDays: Int,
    val xpPoints: Int,
    val activeSubcategory: SubcategoryItemData?,
    val performanceScore: PerformanceScoreData,
    val trainingData: DailyTrainingData? = null
)

data class RecentPracticeScoreData(
    val id: Int,
    val taskTitle: String,
    val mainSkill: String,
    val subcategoryName: String,
    val durationMinutes: Int,
    val durationText: String,
    val xpAwarded: Int,
    val score: Int?,
    val scoreStatus: String,
    val scoreReason: String?,
    val strengths: List<String> = emptyList(),
    val weaknesses: List<String> = emptyList(),
    val aiFeedback: String = "",
    val metrics: Map<String, Any> = emptyMap(),
    val dateText: String
)

data class ProgressSkillItemData(
    val name: String,
    val score: Int,
    val maxScore: Int = 100,
    val isTopTier: Boolean = false
)

data class ProgressAnalyticsData(
    val overallScore: Int,
    val pointsGainText: String,
    val scoreRangeText: String,
    val totalSessions: Int,
    val sessionsWeeklyGainText: String,
    val timeInvestedText: String,
    val timeWeeklyGainText: String,
    val skillItems: List<ProgressSkillItemData>,
    val recentPracticeScores: List<RecentPracticeScoreData> = emptyList()
)

data class SubcategoryItemData(
    val id: Int = 0,
    val mainSkill: String = "",
    val subcategoryId: String = "",
    val subcategoryName: String = "",
    val proficiencyLevel: String = "Beginner",
    val learningGoal: String = "Master Core Fundamentals",
    val isActive: Boolean = false,
    val xpEarned: Int = 0,
    val lessonsCompleted: Int = 0,
    val practiceMinutes: Int = 0
)

data class WeeklyPlanDayData(
    val day: String = "",
    val focus: String = ""
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
    val startButtonText: String,
    val activeSubcategory: SubcategoryItemData? = null,
    val stepByStepInstructions: List<String> = emptyList(),
    val beginnerExercises: List<String> = emptyList(),
    val practiceChallenges: List<String> = emptyList(),
    val weeklyLearningPlan: List<WeeklyPlanDayData> = emptyList(),
    val aiTip: String = "",
    val totalXp: Int = 1240,
    val pendingPracticeItems: List<TrainingLessonItem> = emptyList()
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

data class SpeechHistoryItemData(
    val id: String,
    val promptTitle: String,
    val promptQuote: String,
    val durationSeconds: Int,
    val speechScore: Int,
    val pronunciationScore: Int,
    val fluencyScore: Int,
    val clarityScore: Int,
    val transcript: String,
    val aiFeedback: String,
    val dateText: String
)

data class SpeechDashboardData(
    val totalPracticeSessions: Int,
    val speakingProgress: Int,
    val speechClarityFeedback: String,
    val speakingSpeed: String,
    val pronunciationFeedback: String,
    val recommendedNextExercise: String,
    val history: List<SpeechHistoryItemData>
)

data class SpeechTopicData(
    val title: String,
    val prompt: String,
    val duration: Int,
    val tip: String
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
    val category: String,
    val isRead: Boolean = false
)

data class NotificationsData(
    val unreadCount: Int = 0,
    val isDailyReminderEnabled: Boolean,
    val reminderTimeText: String,
    val reminderDaysText: String = "Every day",
    val reminderNoticeText: String,
    val todayNotifications: List<NotificationItemData>,
    val yesterdayNotifications: List<NotificationItemData>,
    val olderNotifications: List<NotificationItemData> = emptyList()
)

data class ReminderItemData(
    val id: Int,
    val title: String,
    val reminderTime: String,
    val reminderDays: String,
    val isEnabled: Boolean
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
    val isLearningRemindersEnabled: Boolean = true,
    val isPersonalizedAiEnabled: Boolean = true,
    val isSaveVoiceRecordingsEnabled: Boolean = false,
    val currentAppearanceText: String = "Light",
    val reminderTimeText: String = "07:00 PM",
    val themePaletteText: String = "Lavender",
    val isSoundEffectsEnabled: Boolean = true,
    val isAudioGuidanceEnabled: Boolean = true,
    val currentLanguageText: String = "English",
    val appVersionText: String = "AI Skills Mirror · Version 1.0.0"
)

data class SkillAreaItem(
    val name: String,
    val score: Int = 0,
    val gapDelta: Int = 0,
    val status: String = "",
    val detail: String = ""
)

data class MissingTopicItem(
    val topic: String,
    val reason: String = ""
)

data class SkillRecommendationItem(
    val title: String,
    val description: String,
    val actionType: String = "",
    val actionLabel: String = "Start"
)

data class SkillCompetencyItem(
    val key: String,
    val name: String,
    val description: String,
    val score: Int,
    val gapDelta: Int,
    val targetScore: Int = 100
)

data class SkillGapComparison(
    val previousScore: Int,
    val scoreDelta: Int,
    val previousDate: String,
    val summary: String
)

data class SkillGapReportData(
    val reportId: Int? = null,
    val status: String = "success",
    val dataSufficiency: String = "sufficient",
    val userId: Int = 1,
    val mainSkill: String = "music",
    val subcategoryId: String = "singing",
    val subcategoryName: String = "Singing / Vocal Training",
    val proficiencyLevel: String = "Beginner",
    val overallCompetencyScore: Int = 0,
    val gapSeverity: String = "Moderate Gap",
    val assessmentCount: Int = 0,
    val aiSummary: String = "",
    val strongAreas: List<SkillAreaItem> = emptyList(),
    val weakAreas: List<SkillAreaItem> = emptyList(),
    val needsImprovement: List<SkillAreaItem> = emptyList(),
    val missingTopics: List<MissingTopicItem> = emptyList(),
    val recommendations: List<SkillRecommendationItem> = emptyList(),
    val competencies: List<SkillCompetencyItem> = emptyList(),
    val previousComparison: SkillGapComparison? = null
)

data class AvailableSkillItem(
    val id: Int,
    val mainSkill: String,
    val subcategoryId: String,
    val subcategoryName: String,
    val proficiencyLevel: String,
    val isActive: Boolean = false,
    val lessonsCompleted: Int = 0,
    val xpEarned: Int = 0
)

data class SkillGapHistoryItem(
    val reportId: Int,
    val score: Int,
    val gapSeverity: String,
    val assessmentCount: Int,
    val dateFormatted: String
)

data class SkillGapResponseData(
    val currentReport: SkillGapReportData,
    val availableSkills: List<AvailableSkillItem> = emptyList(),
    val history: List<SkillGapHistoryItem> = emptyList()
)

object ApiClient {
    // 1. ngrok tunnel reaches backend from any network (mobile data / Wi-Fi)
    // 2. 172.23.52.187 reaches backend over local Wi-Fi
    // 3. 10.0.2.2 reaches localhost from Android Emulator
    var BASE_URL = "https://throat-unused-repeated.ngrok-free.dev/skillmirrorbackend"
    var FALLBACK_URL = "http://172.23.52.187/skillmirrorbackend"
    var EMULATOR_URL = "http://10.0.2.2/skillmirrorbackend"

    fun createDefaultWeekDays(weekOffset: Int = 0): List<DailyProgressItem> {
        val dayNamesShort = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val dayNamesFull = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

        val cal = java.util.Calendar.getInstance()
        val todayCal = java.util.Calendar.getInstance()

        val dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK)
        val daysFromMonday = if (dayOfWeek == java.util.Calendar.SUNDAY) 6 else (dayOfWeek - java.util.Calendar.MONDAY)
        cal.add(java.util.Calendar.DAY_OF_YEAR, -daysFromMonday + (weekOffset * 7))

        val sdfDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        val sdfFormat = java.text.SimpleDateFormat("MMM d", java.util.Locale.US)
        val todayDateStr = sdfDate.format(todayCal.time)

        return (0..6).map { i ->
            val dateStr = sdfDate.format(cal.time)
            val formattedDate = sdfFormat.format(cal.time)
            val isToday = (dateStr == todayDateStr)
            val item = DailyProgressItem(
                day = dayNamesShort[i],
                dayFull = dayNamesFull[i],
                date = dateStr,
                formattedDate = formattedDate,
                minutes = 0,
                tasks = 0,
                xp = 0,
                isToday = isToday,
                hasActivity = false,
                skillsPracticed = emptyList(),
                tasksDetail = emptyList()
            )
            cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
            item
        }
    }

    fun resolveImageUrl(pathOrUrl: String?): String {
        if (pathOrUrl.isNullOrBlank()) return ""
        val clean = pathOrUrl.trim()
        if (clean.startsWith("http://", ignoreCase = true) || clean.startsWith("https://", ignoreCase = true)) {
            return clean
        }
        val base = BASE_URL.trimEnd('/')
        val rel = clean.trimStart('/')
        return "$base/$rel"
    }


    private fun postJson(
        endpoint: String,
        bodyJson: JSONObject,
        connectTimeoutMs: Int = 10000,
        readTimeoutMs: Int = 30000
    ): Pair<Int, String> {
        val targets = listOf(BASE_URL, FALLBACK_URL, EMULATOR_URL).distinct()
        var lastException: Exception? = null
        for (targetBase in targets) {
            try {
                val cleanTarget = targetBase.trimEnd('/')
                val cleanEndpoint = endpoint.trimStart('/')
                val url = URL("$cleanTarget/$cleanEndpoint")
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty("ngrok-skip-browser-warning", "true")
                    setRequestProperty("User-Agent", "AISkillMirror/1.0")
                    doOutput = true
                    doInput = true
                    connectTimeout = connectTimeoutMs
                    readTimeout = readTimeoutMs
                }
                OutputStreamWriter(conn.outputStream).use { writer ->
                    writer.write(bodyJson.toString())
                    writer.flush()
                }
                val code = conn.responseCode
                val stream = if (code in 200..299) conn.inputStream else conn.errorStream
                val text = stream?.bufferedReader()?.use(BufferedReader::readText) ?: ""
                conn.disconnect()
                if (targetBase != BASE_URL && code in 200..299) {
                    BASE_URL = targetBase
                }
                return Pair(code, text)
            } catch (e: Exception) {
                lastException = e
            }
        }
        throw lastException ?: java.io.IOException("Unable to connect to server")
    }

    private fun getJson(
        endpointWithQuery: String,
        connectTimeoutMs: Int = 10000,
        readTimeoutMs: Int = 20000
    ): Pair<Int, String> {
        val targets = listOf(BASE_URL, FALLBACK_URL, EMULATOR_URL).distinct()
        var lastException: Exception? = null
        for (targetBase in targets) {
            try {
                val cleanTarget = targetBase.trimEnd('/')
                val cleanEndpoint = endpointWithQuery.trimStart('/')
                val url = URL("$cleanTarget/$cleanEndpoint")
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty("ngrok-skip-browser-warning", "true")
                    setRequestProperty("User-Agent", "AISkillMirror/1.0")
                    connectTimeout = connectTimeoutMs
                    readTimeout = readTimeoutMs
                }
                val code = conn.responseCode
                val stream = if (code in 200..299) conn.inputStream else conn.errorStream
                val text = stream?.bufferedReader()?.use(BufferedReader::readText) ?: ""
                conn.disconnect()
                if (targetBase != BASE_URL && code in 200..299) {
                    BASE_URL = targetBase
                }
                return Pair(code, text)
            } catch (e: Exception) {
                lastException = e
            }
        }
        throw lastException ?: java.io.IOException("Unable to connect to server")
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
                val rawPic = userObj?.optString("profile_picture_url")?.takeIf { it.isNotBlank() }
                    ?: userObj?.optString("profile_picture", "") ?: ""
                val resolvedPic = resolveImageUrl(rawPic)
                NetworkResult(true, message, UserData(id, name, userEmail, resolvedPic))
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
                    val rawPic = p.optString("profile_picture_url").takeIf { it.isNotBlank() }
                        ?: p.optString("profile_picture", "")
                    val resolvedPic = resolveImageUrl(rawPic)

                    // Parse weekly progress
                    val wpJson = p.optJSONObject("weekly_progress") ?: json.optJSONObject("weekly_progress")
                    val weeklyProgress = if (wpJson != null) {
                        val daysArray = wpJson.optJSONArray("days")
                        val daysList = mutableListOf<DailyProgressItem>()
                        if (daysArray != null) {
                            for (i in 0 until daysArray.length()) {
                                val d = daysArray.optJSONObject(i)
                                if (d != null) {
                                    val skillsArr = d.optJSONArray("skills_practiced")
                                    val skillsList = mutableListOf<String>()
                                    if (skillsArr != null) {
                                        for (sIdx in 0 until skillsArr.length()) {
                                            skillsList.add(skillsArr.optString(sIdx))
                                        }
                                    }
                                    val tasksArr = d.optJSONArray("tasks_detail")
                                    val tasksList = mutableListOf<PracticeTaskDetail>()
                                    if (tasksArr != null) {
                                        for (tIdx in 0 until tasksArr.length()) {
                                            val tObj = tasksArr.optJSONObject(tIdx)
                                            if (tObj != null) {
                                                tasksList.add(
                                                    PracticeTaskDetail(
                                                        id = tObj.optInt("id", 0),
                                                        type = tObj.optString("type", "PRACTICE"),
                                                        title = tObj.optString("title", ""),
                                                        skill = tObj.optString("skill", ""),
                                                        durationMinutes = tObj.optInt("duration_minutes", 0),
                                                        xpAwarded = tObj.optInt("xp_awarded", 0),
                                                        score = if (tObj.has("score") && !tObj.isNull("score")) tObj.optInt("score") else null
                                                    )
                                                )
                                            }
                                        }
                                    }
                                    daysList.add(
                                        DailyProgressItem(
                                            day = d.optString("day", ""),
                                            dayFull = d.optString("day_full", d.optString("day", "")),
                                            date = d.optString("date", ""),
                                            formattedDate = d.optString("formatted_date", ""),
                                            minutes = d.optInt("minutes", 0),
                                            tasks = d.optInt("tasks", 0),
                                            xp = d.optInt("xp", 0),
                                            isToday = d.optBoolean("is_today", false),
                                            hasActivity = d.optBoolean("has_activity", d.optInt("tasks", 0) > 0 || d.optInt("minutes", 0) > 0),
                                            skillsPracticed = skillsList,
                                            tasksDetail = tasksList
                                        )
                                    )
                                }
                            }
                        }
                        ProfileWeeklyProgressData(
                            weekOffset = wpJson.optInt("week_offset", 0),
                            weekLabel = wpJson.optString("week_label", "This Week"),
                            mondayDate = wpJson.optString("monday_date", ""),
                            sundayDate = wpJson.optString("sunday_date", ""),
                            days = daysList,
                            totalWeeklyMinutes = wpJson.optInt("total_weekly_minutes", 0),
                            weeklyCompletedTasks = wpJson.optInt("weekly_completed_tasks", 0),
                            weeklyXpEarned = wpJson.optInt("weekly_xp_earned", 0),
                            weeklyGoalMinutes = wpJson.optInt("weekly_goal_minutes", 60),
                            weeklyGoalPercentage = wpJson.optInt("weekly_goal_percentage", 0)
                        )
                    } else ProfileWeeklyProgressData()

                    // Parse achieved badges
                    val achArray = p.optJSONArray("achieved_badges") ?: json.optJSONArray("achieved_badges")
                    val achievedBadges = mutableListOf<ProfileBadgeItem>()
                    if (achArray != null) {
                        for (i in 0 until achArray.length()) {
                            val b = achArray.optJSONObject(i)
                            if (b != null) {
                                achievedBadges.add(
                                    ProfileBadgeItem(
                                        id = b.optString("id", ""),
                                        name = b.optString("name", ""),
                                        description = b.optString("description", ""),
                                        iconType = b.optString("icon_type", "TROPHY"),
                                        isUnlocked = b.optBoolean("is_unlocked", true),
                                        earnedDate = b.optString("earned_date").takeIf { it.isNotBlank() && it != "null" }
                                    )
                                )
                            }
                        }
                    }

                    // Parse locked badges
                    val lockedArray = p.optJSONArray("locked_badges") ?: json.optJSONArray("locked_badges")
                    val lockedBadges = mutableListOf<ProfileBadgeItem>()
                    if (lockedArray != null) {
                        for (i in 0 until lockedArray.length()) {
                            val b = lockedArray.optJSONObject(i)
                            if (b != null) {
                                lockedBadges.add(
                                    ProfileBadgeItem(
                                        id = b.optString("id", ""),
                                        name = b.optString("name", ""),
                                        description = b.optString("description", ""),
                                        iconType = b.optString("icon_type", "TROPHY"),
                                        isUnlocked = b.optBoolean("is_unlocked", false),
                                        earnedDate = null
                                    )
                                )
                            }
                        }
                    }

                    val profile = ProfileData(
                        userId = p.optInt("user_id", userId),
                        name = p.optString("name", ""),
                        email = p.optString("email", ""),
                        phone = p.optString("phone", ""),
                        education = p.optString("education", ""),
                        skills = p.optString("skills", ""),
                        careerGoal = p.optString("career_goal", ""),
                        bio = p.optString("bio", ""),
                        profilePicture = resolvedPic,
                        skillScore = p.optInt("skill_score", 0),
                        totalSessions = p.optInt("total_sessions", 0),
                        dayStreak = p.optInt("day_streak", 0),
                        totalXp = p.optInt("total_xp", 0),
                        memberSinceText = p.optString("member_since_text", "Growing with AI Skills Mirror"),
                        weeklyProgress = weeklyProgress,
                        achievedBadges = achievedBadges,
                        lockedBadges = lockedBadges
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
                setRequestProperty("ngrok-skip-browser-warning", "true")
                setRequestProperty("User-Agent", "AISkillMirror/1.0")
                doOutput = true
                doInput = true
                connectTimeout = 15000
                readTimeout = 15000
            }

            val mime = when {
                fileName.endsWith(".png", true) -> "image/png"
                fileName.endsWith(".webp", true) -> "image/webp"
                else -> "image/jpeg"
            }

            conn.outputStream.use { os ->
                // Write user_id
                os.write("--$boundary\r\n".toByteArray())
                os.write("Content-Disposition: form-data; name=\"user_id\"\r\n\r\n".toByteArray())
                os.write("$userId\r\n".toByteArray())

                // Write file
                os.write("--$boundary\r\n".toByteArray())
                os.write("Content-Disposition: form-data; name=\"profile_picture\"; filename=\"$fileName\"\r\n".toByteArray())
                os.write("Content-Type: $mime\r\n\r\n".toByteArray())
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
            val message = json.optString("message", "Profile picture uploaded successfully")

            if (code == 200 && status == "success") {
                val dataObj = json.optJSONObject("data")
                val rawUrl = dataObj?.optString("url")?.takeIf { it.isNotBlank() }
                    ?: json.optString("url").takeIf { it.isNotBlank() }
                    ?: dataObj?.optString("profile_picture")
                    ?: json.optString("profile_picture", "")
                val resolvedUrl = resolveImageUrl(rawUrl)
                NetworkResult(true, message, resolvedUrl)
            } else {
                NetworkResult(false, message)
            }
        } catch (e: Exception) {
            NetworkResult(false, "Upload error: ${e.localizedMessage}")
        }
    }

    suspend fun removeProfilePicture(userId: Int): NetworkResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("user_id", userId)
            }
            val (code, text) = postJson("profile/profile_remove_picture.php", body)
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            val status = json.optString("status")
            val message = json.optString("message", "Profile photo removed")
            NetworkResult(code == 200 && status == "success", message, code == 200 && status == "success")
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}", false)
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
                val rawProfilePic = d.optString("profilePictureUrl").takeIf { it.isNotBlank() }
                    ?: d.optString("profilePicture", "")
                val resolvedProfilePic = resolveImageUrl(rawProfilePic)

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
                    currentLessonProgress = d.optDouble("currentLessonProgress", 0.6),
                    greeting = d.optString("greeting", "Good morning,"),
                    currentDateText = d.optString("currentDateText", ""),
                    headline = d.optString("headline", "Your future is taking shape."),
                    streakSubtitle = d.optString("streakSubtitle", "Your best week yet"),
                    minutesSubtitle = d.optString("minutesSubtitle", "to your goal"),
                    currentPlanTitle = d.optString("currentPlanTitle", ""),
                    profilePicture = resolvedProfilePic
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

    suspend fun fetchRoadmap(userId: Int, subcategoryId: String? = null): NetworkResult<GrowthRoadmapData> = withContext(Dispatchers.IO) {
        try {
            val query = if (!subcategoryId.isNullOrBlank()) {
                "roadmap/roadmap_fetch.php?user_id=$userId&subcategory_id=$subcategoryId"
            } else {
                "roadmap/roadmap_fetch.php?user_id=$userId"
            }
            val (code, text) = getJson(query)
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            if (code == 200 && json.optString("status") == "success") {
                val d = json.getJSONObject("data")

                var activeSub: SubcategoryItemData? = null
                if (d.has("activeSubcategory") && !d.isNull("activeSubcategory")) {
                    val asObj = d.getJSONObject("activeSubcategory")
                    activeSub = SubcategoryItemData(
                        id = asObj.optInt("id"),
                        mainSkill = asObj.optString("mainSkill"),
                        subcategoryId = asObj.optString("subcategoryId"),
                        subcategoryName = asObj.optString("subcategoryName"),
                        proficiencyLevel = asObj.optString("proficiencyLevel"),
                        learningGoal = asObj.optString("learningGoal"),
                        isActive = asObj.optBoolean("isActive", true),
                        xpEarned = asObj.optInt("xpEarned"),
                        lessonsCompleted = asObj.optInt("lessonsCompleted"),
                        practiceMinutes = asObj.optInt("practiceMinutes")
                    )
                }

                val availSubs = mutableListOf<SubcategoryItemData>()
                val avArr = d.optJSONArray("availableSubcategories")
                if (avArr != null) {
                    for (i in 0 until avArr.length()) {
                        val so = avArr.getJSONObject(i)
                        availSubs.add(
                            SubcategoryItemData(
                                id = so.optInt("id"),
                                mainSkill = so.optString("mainSkill"),
                                subcategoryId = so.optString("subcategoryId"),
                                subcategoryName = so.optString("subcategoryName"),
                                proficiencyLevel = so.optString("proficiencyLevel"),
                                learningGoal = so.optString("learningGoal"),
                                isActive = so.optBoolean("isActive", false),
                                xpEarned = so.optInt("xpEarned"),
                                lessonsCompleted = so.optInt("lessonsCompleted"),
                                practiceMinutes = so.optInt("practiceMinutes")
                            )
                        )
                    }
                }

                val stagesList = mutableListOf<RoadmapStageData>()
                val stArr = d.optJSONArray("stages")
                if (stArr != null) {
                    for (i in 0 until stArr.length()) {
                        val st = stArr.getJSONObject(i)

                        val objList = mutableListOf<String>()
                        st.optJSONArray("learningObjectives")?.let { oa ->
                            for (j in 0 until oa.length()) objList.add(oa.getString(j))
                        }

                        val taskList = mutableListOf<RoadmapTaskData>()
                        st.optJSONArray("practiceTasks")?.let { ta ->
                            for (j in 0 until ta.length()) {
                                val to = ta.getJSONObject(j)
                                taskList.add(
                                    RoadmapTaskData(
                                        id = to.optString("id"),
                                        title = to.optString("title"),
                                        durationText = to.optString("durationText", "10 mins"),
                                        isDone = to.optBoolean("isDone", false)
                                    )
                                )
                            }
                        }

                        val questList = mutableListOf<RoadmapQuestionData>()
                        st.optJSONArray("assessmentQuestions")?.let { qa ->
                            for (j in 0 until qa.length()) {
                                val qo = qa.getJSONObject(j)
                                val optList = mutableListOf<String>()
                                qo.optJSONArray("options")?.let { opa ->
                                    for (k in 0 until opa.length()) optList.add(opa.getString(k))
                                }
                                questList.add(
                                    RoadmapQuestionData(
                                        id = qo.optInt("id"),
                                        question = qo.optString("question"),
                                        options = optList,
                                        correct = qo.optInt("correct", 0),
                                        explanation = qo.optString("explanation", "")
                                    )
                                )
                            }
                        }

                        stagesList.add(
                            RoadmapStageData(
                                id = st.optInt("id"),
                                stageOrder = st.optInt("stageOrder", 1),
                                stageLevel = st.optString("stageLevel", "Beginner"),
                                topicName = st.optString("topicName"),
                                topicDescription = st.optString("topicDescription"),
                                learningObjectives = objList,
                                practiceInstructions = st.optString("practiceInstructions", ""),
                                estimatedDuration = st.optString("estimatedDuration", "15 mins"),
                                difficulty = st.optString("difficulty", "Beginner"),
                                isCompleted = st.optBoolean("isCompleted", false),
                                isUnlocked = st.optBoolean("isUnlocked", false),
                                scoreAchieved = if (st.isNull("scoreAchieved")) null else st.optInt("scoreAchieved"),
                                needsRemedial = st.optBoolean("needsRemedial", false),
                                remedialFeedback = st.optString("remedialFeedback", ""),
                                practiceTasks = taskList,
                                assessmentQuestions = questList
                            )
                        )
                    }
                }

                var aiFeedback: RoadmapAiFeedbackData? = null
                if (d.has("aiFeedback") && !d.isNull("aiFeedback")) {
                    val fb = d.getJSONObject("aiFeedback")
                    val strengthsList = mutableListOf<String>()
                    fb.optJSONArray("strengths")?.let { sa ->
                        for (i in 0 until sa.length()) strengthsList.add(sa.getString(i))
                    }
                    aiFeedback = RoadmapAiFeedbackData(
                        coachAdvice = fb.optString("coachAdvice", ""),
                        hasRemedialAlert = fb.optBoolean("hasRemedialAlert", false),
                        remedialFeedback = fb.optString("remedialFeedback", ""),
                        strengths = strengthsList,
                        nextGoal = fb.optString("nextGoal", "")
                    )
                }

                val arr = d.optJSONArray("upcomingSteps") ?: JSONArray()
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
                    userId = d.optInt("userId", userId),
                    activeSubcategory = activeSub,
                    availableSubcategories = availSubs,
                    roadmapProgress = d.optDouble("roadmapProgress", 0.0).toFloat(),
                    roadmapProgressPercentage = d.optInt("roadmapProgressPercentage", 0),
                    roadmapProgressText = d.optString("roadmapProgressText", ""),
                    completedCount = d.optInt("completedCount", 0),
                    totalCount = d.optInt("totalCount", 5),
                    totalPracticeMinutes = d.optInt("totalPracticeMinutes", 0),
                    currentStageTitle = d.optString("currentStageTitle", ""),
                    currentStageOrder = d.optInt("currentStageOrder", 1),
                    stages = stagesList,
                    aiFeedback = aiFeedback,
                    currentWeekBadgeText = d.optString("currentWeekBadgeText"),
                    planTitle = d.optString("planTitle"),
                    planSubtitle = d.optString("planSubtitle"),
                    planProgress = d.optDouble("planProgress", 0.0),
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
                NetworkResult(false, json.optString("message", "Failed to fetch roadmap"))
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}")
        }
    }

    suspend fun completeRoadmapTopic(
        userId: Int,
        stageId: Int,
        score: Int,
        answers: Map<Int, Int>? = null
    ): NetworkResult<CompleteTopicResultData> = withContext(Dispatchers.IO) {
        try {
            val payload = JSONObject().apply {
                put("user_id", userId)
                put("stage_id", stageId)
                put("score", score)
                if (answers != null && answers.isNotEmpty()) {
                    val ansObj = JSONObject()
                    for ((k, v) in answers) {
                        ansObj.put(k.toString(), v)
                    }
                    put("answers", ansObj)
                }
            }
            val (code, text) = postJson("roadmap/roadmap_topic_complete.php", payload)
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            if (code == 200 && json.optString("status") == "success") {
                val d = json.getJSONObject("data")

                val revList = mutableListOf<QuizReviewItem>()
                d.optJSONArray("reviewQuestions")?.let { rqa ->
                    for (i in 0 until rqa.length()) {
                        val ro = rqa.getJSONObject(i)
                        val optList = mutableListOf<String>()
                        ro.optJSONArray("options")?.let { opa ->
                            for (j in 0 until opa.length()) optList.add(opa.getString(j))
                        }
                        revList.add(
                            QuizReviewItem(
                                id = ro.optInt("id", i + 1),
                                question = ro.optString("question", ""),
                                options = optList,
                                userChoice = ro.optInt("userChoice", -1),
                                correctChoice = ro.optInt("correctChoice", 0),
                                explanation = ro.optString("explanation", ""),
                                isCorrect = ro.optBoolean("isCorrect", false)
                            )
                        )
                    }
                }

                val res = CompleteTopicResultData(
                    passed = d.optBoolean("passed", true),
                    score = d.optInt("score", score),
                    xpAwarded = d.optInt("xpAwarded", 0),
                    message = json.optString("message", "Topic completed"),
                    totalQuestions = d.optInt("totalQuestions", 5),
                    correctAnswers = d.optInt("correctAnswers", 0),
                    incorrectAnswers = d.optInt("incorrectAnswers", 0),
                    reviewQuestions = revList,
                    nextTopicUnlocked = if (d.isNull("nextTopicUnlocked")) null else d.optString("nextTopicUnlocked"),
                    remedialAdvice = if (d.isNull("remedialAdvice")) null else d.optString("remedialAdvice"),
                    remedialFeedback = if (d.isNull("remedialFeedback")) (if (d.isNull("remedialAdvice")) null else d.optString("remedialAdvice")) else d.optString("remedialFeedback"),
                    promotedLevel = if (d.isNull("promotedLevel")) null else d.optString("promotedLevel")
                )
                NetworkResult(true, json.optString("message"), res)
            } else {
                NetworkResult(false, json.optString("message", "Failed to complete topic"))
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}")
        }
    }

    suspend fun toggleRoadmapTask(
        userId: Int,
        stageId: Int,
        taskId: String,
        isDone: Boolean
    ): NetworkResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            val payload = JSONObject().apply {
                put("user_id", userId)
                put("stage_id", stageId)
                put("task_id", taskId)
                put("is_done", isDone)
            }
            val (code, text) = postJson("roadmap/roadmap_task_toggle.php", payload)
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            NetworkResult(code == 200 && json.optString("status") == "success", json.optString("message"), code == 200)
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}", false)
        }
    }

    suspend fun saveUserSubcategory(
        userId: Int,
        mainSkill: String,
        subcategoryId: String,
        subcategoryName: String,
        proficiencyLevel: String,
        learningGoal: String
    ): NetworkResult<SubcategoryItemData> = withContext(Dispatchers.IO) {
        try {
            val payload = JSONObject().apply {
                put("user_id", userId)
                put("main_skill", mainSkill)
                put("subcategory_id", subcategoryId)
                put("subcategory_name", subcategoryName)
                put("proficiency_level", proficiencyLevel)
                put("learning_goal", learningGoal)
            }
            val (code, text) = postJson("skills/user_skill_subcategory_save.php", payload)
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            if (code == 200 && json.optString("status") == "success") {
                val d = json.getJSONObject("data").getJSONObject("activeSubcategory")
                val sub = SubcategoryItemData(
                    id = d.optInt("id"),
                    mainSkill = d.optString("mainSkill"),
                    subcategoryId = d.optString("subcategoryId"),
                    subcategoryName = d.optString("subcategoryName"),
                    proficiencyLevel = d.optString("proficiencyLevel"),
                    learningGoal = d.optString("learningGoal"),
                    isActive = d.optBoolean("isActive", true),
                    xpEarned = d.optInt("xpEarned"),
                    lessonsCompleted = d.optInt("lessonsCompleted"),
                    practiceMinutes = d.optInt("practiceMinutes")
                )
                NetworkResult(true, json.optString("message"), sub)
            } else {
                NetworkResult(false, json.optString("message", "Failed to save subcategory"))
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}")
        }
    }

    suspend fun fetchUserSubcategories(userId: Int): NetworkResult<Pair<SubcategoryItemData?, List<SubcategoryItemData>>> = withContext(Dispatchers.IO) {
        try {
            val (code, text) = getJson("skills/user_skill_subcategory_fetch.php?user_id=$userId")
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            if (code == 200 && json.optString("status") == "success") {
                val d = json.getJSONObject("data")
                var active: SubcategoryItemData? = null
                if (d.has("activeSubcategory") && !d.isNull("activeSubcategory")) {
                    val a = d.getJSONObject("activeSubcategory")
                    active = SubcategoryItemData(
                        id = a.optInt("id"),
                        mainSkill = a.optString("mainSkill"),
                        subcategoryId = a.optString("subcategoryId"),
                        subcategoryName = a.optString("subcategoryName"),
                        proficiencyLevel = a.optString("proficiencyLevel"),
                        learningGoal = a.optString("learningGoal"),
                        isActive = a.optBoolean("isActive", true),
                        xpEarned = a.optInt("xpEarned"),
                        lessonsCompleted = a.optInt("lessonsCompleted"),
                        practiceMinutes = a.optInt("practiceMinutes")
                    )
                }
                val list = mutableListOf<SubcategoryItemData>()
                val arr = d.optJSONArray("selectedSubcategories")
                if (arr != null) {
                    for (i in 0 until arr.length()) {
                        val s = arr.getJSONObject(i)
                        list.add(
                            SubcategoryItemData(
                                id = s.optInt("id"),
                                mainSkill = s.optString("mainSkill"),
                                subcategoryId = s.optString("subcategoryId"),
                                subcategoryName = s.optString("subcategoryName"),
                                proficiencyLevel = s.optString("proficiencyLevel"),
                                learningGoal = s.optString("learningGoal"),
                                isActive = s.optBoolean("isActive"),
                                xpEarned = s.optInt("xpEarned"),
                                lessonsCompleted = s.optInt("lessonsCompleted"),
                                practiceMinutes = s.optInt("practiceMinutes")
                            )
                        )
                    }
                }
                NetworkResult(true, "Subcategories loaded", Pair(active, list))
            } else {
                NetworkResult(false, json.optString("message", "Failed to fetch subcategories"))
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}")
        }
    }

    suspend fun completeLesson(
        userId: Int,
        lessonId: String = "1",
        taskTitle: String = "Daily Practice",
        durationSeconds: Int = 60,
        durationMinutes: Int = 1,
        xpPoints: Int = 35,
        mainSkill: String = "",
        subcategoryId: String = "",
        codeSnippet: String? = null,
        transcript: String? = null,
        quizAnswers: List<Pair<Int, Boolean>>? = null,
        audioPath: String? = null,
        imagePath: String? = null
    ): NetworkResult<LessonCompleteResultData> = withContext(Dispatchers.IO) {
        try {
            val payload = JSONObject().apply {
                put("user_id", userId)
                put("lesson_id", lessonId)
                put("task_title", taskTitle)
                put("duration_seconds", durationSeconds)
                put("duration_minutes", durationMinutes)
                put("xp_points", xpPoints)
                if (mainSkill.isNotBlank()) put("main_skill", mainSkill)
                if (subcategoryId.isNotBlank()) put("subcategory_id", subcategoryId)
                if (!codeSnippet.isNullOrBlank()) put("code_snippet", codeSnippet)
                if (!transcript.isNullOrBlank()) put("transcript", transcript)
                if (!audioPath.isNullOrBlank()) {
                    put("audio_path", audioPath)
                    try {
                        val aFile = java.io.File(audioPath)
                        if (aFile.exists() && aFile.length() > 0) {
                            val bytes = aFile.readBytes()
                            val b64 = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
                            put("audio_base64", b64)
                        }
                    } catch (_: Exception) {}
                }
                if (!imagePath.isNullOrBlank()) {
                    put("image_path", imagePath)
                    try {
                        val iFile = java.io.File(imagePath)
                        if (iFile.exists() && iFile.length() > 0) {
                            val bytes = if (iFile.length() > 600 * 1024) {
                                val bmp = android.graphics.BitmapFactory.decodeFile(iFile.absolutePath)
                                if (bmp != null) {
                                    val maxDim = 1200
                                    val scaled = if (bmp.width > maxDim || bmp.height > maxDim) {
                                        val ratio = minOf(maxDim.toFloat() / bmp.width, maxDim.toFloat() / bmp.height)
                                        android.graphics.Bitmap.createScaledBitmap(bmp, (bmp.width * ratio).toInt(), (bmp.height * ratio).toInt(), true)
                                    } else bmp
                                    val baos = java.io.ByteArrayOutputStream()
                                    scaled.compress(android.graphics.Bitmap.CompressFormat.JPEG, 85, baos)
                                    baos.toByteArray()
                                } else {
                                    iFile.readBytes()
                                }
                            } else {
                                iFile.readBytes()
                            }
                            val b64 = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
                            put("image_base64", b64)
                        }
                    } catch (_: Exception) {}
                }
                if (!quizAnswers.isNullOrEmpty()) {
                    val qArr = JSONArray()
                    quizAnswers.forEach { (qId, isCorr) ->
                        qArr.put(JSONObject().apply {
                            put("question_id", qId)
                            put("is_correct", isCorr)
                        })
                    }
                    put("quiz_answers", qArr)
                }
            }
            val (code, text) = postJson("training/lesson_complete.php", payload, connectTimeoutMs = 30000, readTimeoutMs = 60000)
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            if (code == 200 && json.optString("status") == "success") {
                val dataObj = json.optJSONObject("data") ?: JSONObject()
                val psObj = dataObj.optJSONObject("performanceScore") ?: JSONObject()

                val strengthsList = mutableListOf<String>()
                val strArr = psObj.optJSONArray("strengths")
                if (strArr != null) {
                    for (i in 0 until strArr.length()) {
                        strengthsList.add(strArr.getString(i))
                    }
                }

                val weaknessesList = mutableListOf<String>()
                val weakArr = psObj.optJSONArray("weaknesses")
                if (weakArr != null) {
                    for (i in 0 until weakArr.length()) {
                        weaknessesList.add(weakArr.getString(i))
                    }
                }

                val metricsMap = mutableMapOf<String, Any>()
                val metObj = psObj.optJSONObject("metrics")
                if (metObj != null) {
                    metObj.keys().forEach { k ->
                        metricsMap[k] = metObj.get(k)
                    }
                }

                val perfScore = PerformanceScoreData(
                    score = if (psObj.isNull("score")) null else psObj.optInt("score"),
                    scoreStatus = psObj.optString("scoreStatus", "not_available"),
                    scoreReason = if (psObj.isNull("scoreReason")) null else psObj.optString("scoreReason"),
                    strengths = strengthsList,
                    weaknesses = weaknessesList,
                    aiFeedback = psObj.optString("aiFeedback", ""),
                    metrics = metricsMap,
                    assessmentType = psObj.optString("assessmentType", "none"),
                    taskTitle = psObj.optString("taskTitle", taskTitle),
                    durationMinutes = psObj.optInt("durationMinutes", durationMinutes),
                    xpEarned = psObj.optInt("xpEarned", xpPoints)
                )

                val refreshedTraining = fetchTraining(userId, if (subcategoryId.isNotBlank()) subcategoryId else null).data

                val resultData = LessonCompleteResultData(
                    lessonId = dataObj.optString("lesson_id"),
                    minutesToday = dataObj.optInt("minutesToday", durationMinutes),
                    goalMinutesToday = dataObj.optInt("goalMinutesToday", 20),
                    streakDays = dataObj.optInt("streakDays", 1),
                    xpPoints = dataObj.optInt("xpPoints", xpPoints),
                    activeSubcategory = null,
                    performanceScore = perfScore,
                    trainingData = refreshedTraining
                )

                NetworkResult(true, json.optString("message", "Practice completed"), resultData)
            } else {
                NetworkResult(false, json.optString("message", "Failed to complete lesson"))
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}")
        }
    }

    suspend fun executeCode(
        userId: Int,
        language: String,
        code: String,
        taskTitle: String
    ): NetworkResult<CodeExecutionResult> = withContext(Dispatchers.IO) {
        try {
            val payload = JSONObject().apply {
                put("user_id", userId)
                put("language", language)
                put("code", code)
                put("task_title", taskTitle)
            }
            val (statusCode, text) = postJson("training/code_execute.php", payload, connectTimeoutMs = 15000, readTimeoutMs = 20000)
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            val status = json.optString("status")
            if (statusCode == 200 && status == "success") {
                val tcArr = json.optJSONArray("test_cases")
                val tcList = mutableListOf<CodeTestCaseResult>()
                if (tcArr != null) {
                    for (i in 0 until tcArr.length()) {
                        val tc = tcArr.optJSONObject(i)
                        if (tc != null) {
                            tcList.add(
                                CodeTestCaseResult(
                                    name = tc.optString("name", "Test"),
                                    passed = tc.optBoolean("passed", false),
                                    input = tc.optString("input", ""),
                                    expected = tc.optString("expected", ""),
                                    actual = tc.optString("actual", "")
                                )
                            )
                        }
                    }
                }
                val res = CodeExecutionResult(
                    language = json.optString("language", language),
                    output = json.optString("output", ""),
                    error = json.optString("error", ""),
                    hasError = json.optBoolean("has_error", false),
                    testCases = tcList,
                    allTestsPassed = json.optBoolean("all_tests_passed", false),
                    executionTimeMs = json.optInt("execution_time_ms", 0),
                    explanation = json.optString("explanation", "")
                )
                NetworkResult(true, "Code executed", res)
            } else {
                NetworkResult(false, json.optString("message", "Execution failed"))
            }
        } catch (e: Exception) {
            NetworkResult(false, "Code execution error: ${e.localizedMessage}")
        }
    }

    suspend fun fetchTraining(userId: Int, subcategoryId: String? = null): NetworkResult<DailyTrainingData> = withContext(Dispatchers.IO) {
        try {
            val query = if (!subcategoryId.isNullOrBlank()) {
                "training/training_fetch.php?user_id=$userId&subcategory_id=$subcategoryId"
            } else {
                "training/training_fetch.php?user_id=$userId"
            }
            val (code, text) = getJson(query)
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            if (code == 200 && json.optString("status") == "success") {
                val d = json.getJSONObject("data")
                val arr = d.optJSONArray("completedPracticeItems") ?: JSONArray()
                val completed = mutableListOf<TrainingLessonItem>()
                for (i in 0 until arr.length()) {
                    val it = arr.getJSONObject(i)
                    val strList = mutableListOf<String>()
                    it.optJSONArray("strengths")?.let { sa ->
                        for (j in 0 until sa.length()) strList.add(sa.getString(j))
                    }
                    val weakList = mutableListOf<String>()
                    it.optJSONArray("weaknesses")?.let { wa ->
                        for (j in 0 until wa.length()) weakList.add(wa.getString(j))
                    }

                    val metMap = mutableMapOf<String, Any>()
                    it.optJSONObject("metrics")?.let { mo ->
                        mo.keys().forEach { k -> metMap[k] = mo.get(k) }
                    }
                    val assessType = it.optString("assessmentType", "")

                    completed.add(
                        TrainingLessonItem(
                            id = it.optString("id"),
                            title = it.optString("title"),
                            category = it.optString("category"),
                            durationText = it.optString("durationText"),
                            xpText = it.optString("xpText"),
                            isCompleted = it.optBoolean("isCompleted", true),
                            performanceScore = if (it.isNull("performanceScore")) null else it.optInt("performanceScore"),
                            scoreStatus = it.optString("scoreStatus", "not_available"),
                            scoreReason = if (it.isNull("scoreReason")) null else it.optString("scoreReason"),
                            strengths = strList,
                            weaknesses = weakList,
                            aiFeedback = it.optString("aiFeedback", ""),
                            metrics = metMap,
                            assessmentType = assessType
                        )
                    )
                }

                val pArr = d.optJSONArray("pendingPracticeItems") ?: JSONArray()
                val pending = mutableListOf<TrainingLessonItem>()
                for (i in 0 until pArr.length()) {
                    val it = pArr.getJSONObject(i)
                    pending.add(
                        TrainingLessonItem(
                            id = it.optString("id"),
                            title = it.optString("title"),
                            category = it.optString("category"),
                            durationText = it.optString("durationText"),
                            xpText = it.optString("xpText"),
                            isCompleted = false
                        )
                    )
                }

                var activeSub: SubcategoryItemData? = null
                if (d.has("activeSubcategory") && !d.isNull("activeSubcategory")) {
                    val asObj = d.getJSONObject("activeSubcategory")
                    activeSub = SubcategoryItemData(
                        id = asObj.optInt("id"),
                        mainSkill = asObj.optString("mainSkill"),
                        subcategoryId = asObj.optString("subcategoryId"),
                        subcategoryName = asObj.optString("subcategoryName"),
                        proficiencyLevel = asObj.optString("proficiencyLevel"),
                        learningGoal = asObj.optString("learningGoal"),
                        isActive = true,
                        xpEarned = asObj.optInt("xpEarned"),
                        lessonsCompleted = asObj.optInt("lessonsCompleted"),
                        practiceMinutes = asObj.optInt("practiceMinutes")
                    )
                }

                val stepsList = mutableListOf<String>()
                val sArr = d.optJSONArray("stepByStepInstructions")
                if (sArr != null) {
                    for (i in 0 until sArr.length()) stepsList.add(sArr.getString(i))
                }

                val exercisesList = mutableListOf<String>()
                val eArr = d.optJSONArray("beginnerExercises")
                if (eArr != null) {
                    for (i in 0 until eArr.length()) exercisesList.add(eArr.getString(i))
                }

                val challengesList = mutableListOf<String>()
                val cArr = d.optJSONArray("practiceChallenges")
                if (cArr != null) {
                    for (i in 0 until cArr.length()) challengesList.add(cArr.getString(i))
                }

                val weeklyPlan = mutableListOf<WeeklyPlanDayData>()
                val wArr = d.optJSONArray("weeklyLearningPlan")
                if (wArr != null) {
                    for (i in 0 until wArr.length()) {
                        val wObj = wArr.getJSONObject(i)
                        weeklyPlan.add(
                            WeeklyPlanDayData(
                                day = wObj.optString("day"),
                                focus = wObj.optString("focus")
                            )
                        )
                    }
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
                    startButtonText = d.optString("startButtonText"),
                    activeSubcategory = activeSub,
                    stepByStepInstructions = stepsList,
                    beginnerExercises = exercisesList,
                    practiceChallenges = challengesList,
                    weeklyLearningPlan = weeklyPlan,
                    aiTip = d.optString("aiTip"),
                    totalXp = d.optInt("totalXp", 1240),
                    pendingPracticeItems = pending
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
                fun parseArr(arr: JSONArray?): List<NotificationItemData> {
                    if (arr == null) return emptyList()
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
                                category = n.optString("category"),
                                isRead = n.optBoolean("isRead", false)
                            )
                        )
                    }
                    return list
                }
                val today = parseArr(d.optJSONArray("todayNotifications"))
                val yesterday = parseArr(d.optJSONArray("yesterdayNotifications"))
                val older = parseArr(d.optJSONArray("olderNotifications"))
                val data = NotificationsData(
                    unreadCount = d.optInt("unreadCount", 0),
                    isDailyReminderEnabled = d.optBoolean("isDailyReminderEnabled", true),
                    reminderTimeText = d.optString("reminderTimeText", "Every day at 7:00 PM"),
                    reminderDaysText = d.optString("reminderDaysText", "Every day"),
                    reminderNoticeText = d.optString("reminderNoticeText", "A gentle nudge to invest in yourself."),
                    todayNotifications = today,
                    yesterdayNotifications = yesterday,
                    olderNotifications = older
                )
                NetworkResult(true, "Notifications fetched", data)
            } else {
                NetworkResult(false, json.optString("message"))
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}")
        }
    }

    suspend fun markNotificationRead(userId: Int, notificationId: String = "all"): NetworkResult<Int> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("user_id", userId)
                put("notification_id", notificationId)
            }
            val (code, text) = postJson("notifications/notification_mark_read.php", body)
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            if (code == 200 && json.optString("status") == "success") {
                val unread = json.optJSONObject("data")?.optInt("unreadCount") ?: 0
                NetworkResult(true, "Marked as read", unread)
            } else {
                NetworkResult(false, json.optString("message", "Failed to mark as read"))
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}")
        }
    }

    suspend fun clearNotifications(userId: Int, notificationId: String? = null): NetworkResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("user_id", userId)
                notificationId?.let { put("notification_id", it) }
            }
            val (code, text) = postJson("notifications/notification_clear.php", body)
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            NetworkResult(code == 200 && json.optString("status") == "success", json.optString("message"), true)
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}", false)
        }
    }

    suspend fun createNotification(
        userId: Int,
        title: String,
        description: String,
        badgeText: String = "REMINDER",
        iconType: String = "ALARM",
        category: String = "PRACTICE",
        isGreenBadge: Boolean = false
    ): NetworkResult<Int> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("user_id", userId)
                put("title", title)
                put("description", description)
                put("badge_text", badgeText)
                put("icon_type", iconType)
                put("category", category)
                put("is_green_badge", if (isGreenBadge) 1 else 0)
            }
            val (code, text) = postJson("notifications/notification_create.php", body)
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            if (code == 200 && json.optString("status") == "success") {
                val notifId = json.optJSONObject("data")?.optInt("notificationId") ?: 0
                NetworkResult(true, "Notification created", notifId)
            } else {
                NetworkResult(false, json.optString("message"))
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}")
        }
    }

    suspend fun saveReminder(
        userId: Int,
        title: String,
        time: String,
        days: String,
        frequency: String = "Daily",
        skill: String = "Public Speaking",
        isEnabled: Boolean = true
    ): NetworkResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("user_id", userId)
                put("title", title)
                put("reminder_time", time)
                put("reminder_days", days)
                put("frequency", frequency)
                put("skill_category", skill)
                put("is_enabled", isEnabled)
            }
            val (code, text) = postJson("reminders/reminder_save.php", body)
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            NetworkResult(code == 200 && json.optString("status") == "success", json.optString("message"), true)
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}", false)
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

    suspend fun sendCoachMessage(userId: Int, message: String, language: String = "en"): NetworkResult<Pair<ChatMessageData, ChatMessageData>> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("user_id", userId)
                put("message", message)
                put("language", language)
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

    suspend fun clearCoachChat(userId: Int, language: String = "en"): NetworkResult<ChatMessageData> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("user_id", userId)
                put("language", language)
            }
            val (code, text) = postJson("coach/coach_clear_chat.php", body)
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            if (code == 200 && json.optString("status") == "success") {
                val d = json.optJSONObject("data")
                val w = d?.optJSONObject("welcomeMessage")
                val welcomeMsg = ChatMessageData(
                    id = w?.optString("id") ?: "1",
                    text = w?.optString("text") ?: "Conversation cleared.",
                    isUser = false,
                    timestamp = w?.optString("timestamp") ?: "Now"
                )
                NetworkResult(true, "Conversation cleared", welcomeMsg)
            } else {
                NetworkResult(false, json.optString("message", "Failed to clear chat"))
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
                    currentAppearanceText = d.optString("currentAppearanceText", "Light"),
                    reminderTimeText = d.optString("reminderTimeText", "07:00 PM"),
                    themePaletteText = d.optString("themePaletteText", "Lavender"),
                    isSoundEffectsEnabled = d.optBoolean("isSoundEffectsEnabled", true),
                    isAudioGuidanceEnabled = d.optBoolean("isAudioGuidanceEnabled", true),
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
        reminderTime: String? = null,
        themePalette: String? = null,
        sound: Boolean? = null,
        audio: Boolean? = null,
        language: String? = null
    ): NetworkResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("user_id", userId)
                reminders?.let { put("isLearningRemindersEnabled", it) }
                ai?.let { put("isPersonalizedAiEnabled", it) }
                voice?.let { put("isSaveVoiceRecordingsEnabled", it) }
                appearance?.let { put("currentAppearanceText", it) }
                reminderTime?.let { put("reminderTimeText", it) }
                themePalette?.let { put("themePaletteText", it) }
                sound?.let { put("isSoundEffectsEnabled", it) }
                audio?.let { put("isAudioGuidanceEnabled", it) }
                language?.let { put("currentLanguageText", it) }
            }
            val (code, text) = postJson("settings/settings_update.php", body)
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            NetworkResult(code == 200 && json.optString("status") == "success", json.optString("message"), true)
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}", false)
        }
    }

    suspend fun fetchWeeklyProgress(
        userId: Int,
        weekOffset: Int = 0
    ): NetworkResult<ProfileWeeklyProgressData> = withContext(Dispatchers.IO) {
        try {
            val (code, text) = getJson("progress/weekly_progress_fetch.php?user_id=$userId&week_offset=$weekOffset")
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            if (code == 200 && json.optString("status") == "success") {
                val wpJson = json.optJSONObject("data")
                if (wpJson != null) {
                    val daysArray = wpJson.optJSONArray("days")
                    val daysList = mutableListOf<DailyProgressItem>()
                    if (daysArray != null) {
                        for (i in 0 until daysArray.length()) {
                            val d = daysArray.optJSONObject(i)
                            if (d != null) {
                                val skillsArr = d.optJSONArray("skills_practiced")
                                val skillsList = mutableListOf<String>()
                                if (skillsArr != null) {
                                    for (sIdx in 0 until skillsArr.length()) {
                                        skillsList.add(skillsArr.optString(sIdx))
                                    }
                                }
                                val tasksArr = d.optJSONArray("tasks_detail")
                                val tasksList = mutableListOf<PracticeTaskDetail>()
                                if (tasksArr != null) {
                                    for (tIdx in 0 until tasksArr.length()) {
                                        val tObj = tasksArr.optJSONObject(tIdx)
                                        if (tObj != null) {
                                            tasksList.add(
                                                PracticeTaskDetail(
                                                    id = tObj.optInt("id", 0),
                                                    type = tObj.optString("type", "PRACTICE"),
                                                    title = tObj.optString("title", ""),
                                                    skill = tObj.optString("skill", ""),
                                                    durationMinutes = tObj.optInt("duration_minutes", 0),
                                                    xpAwarded = tObj.optInt("xp_awarded", 0),
                                                    score = if (tObj.has("score") && !tObj.isNull("score")) tObj.optInt("score") else null
                                                )
                                            )
                                        }
                                    }
                                }
                                daysList.add(
                                    DailyProgressItem(
                                        day = d.optString("day", ""),
                                        dayFull = d.optString("day_full", d.optString("day", "")),
                                        date = d.optString("date", ""),
                                        formattedDate = d.optString("formatted_date", ""),
                                        minutes = d.optInt("minutes", 0),
                                        tasks = d.optInt("tasks", 0),
                                        xp = d.optInt("xp", 0),
                                        isToday = d.optBoolean("is_today", false),
                                        hasActivity = d.optBoolean("has_activity", d.optInt("tasks", 0) > 0 || d.optInt("minutes", 0) > 0),
                                        skillsPracticed = skillsList,
                                        tasksDetail = tasksList
                                    )
                                )
                            }
                        }
                    }
                    val data = ProfileWeeklyProgressData(
                        weekOffset = wpJson.optInt("week_offset", weekOffset),
                        weekLabel = wpJson.optString("week_label", "This Week"),
                        mondayDate = wpJson.optString("monday_date", ""),
                        sundayDate = wpJson.optString("sunday_date", ""),
                        days = daysList,
                        totalWeeklyMinutes = wpJson.optInt("total_weekly_minutes", 0),
                        weeklyCompletedTasks = wpJson.optInt("weekly_completed_tasks", 0),
                        weeklyXpEarned = wpJson.optInt("weekly_xp_earned", 0),
                        weeklyGoalMinutes = wpJson.optInt("weekly_goal_minutes", 60),
                        weeklyGoalPercentage = wpJson.optInt("weekly_goal_percentage", 0)
                    )
                    NetworkResult(true, "Weekly progress fetched", data)
                } else {
                    NetworkResult(false, "No weekly data returned")
                }
            } else {
                NetworkResult(false, json.optString("message", "Failed to fetch weekly progress"))
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}")
        }
    }

    suspend fun fetchSpeechHistory(userId: Int): NetworkResult<SpeechDashboardData> = withContext(Dispatchers.IO) {
        try {
            val (code, text) = getJson("speech/speech_history_fetch.php?user_id=$userId")
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            if (code == 200 && json.optString("status") == "success") {
                val d = json.getJSONObject("data")
                val arr = d.optJSONArray("history")
                val list = mutableListOf<SpeechHistoryItemData>()
                if (arr != null) {
                    for (i in 0 until arr.length()) {
                        val item = arr.getJSONObject(i)
                        list.add(
                            SpeechHistoryItemData(
                                id = item.optString("id"),
                                promptTitle = item.optString("promptTitle"),
                                promptQuote = item.optString("promptQuote"),
                                durationSeconds = item.optInt("durationSeconds", 45),
                                speechScore = item.optInt("speechScore", 80),
                                pronunciationScore = item.optInt("pronunciationScore", 80),
                                fluencyScore = item.optInt("fluencyScore", 80),
                                clarityScore = item.optInt("clarityScore", 80),
                                transcript = item.optString("transcript"),
                                aiFeedback = item.optString("aiFeedback"),
                                dateText = item.optString("dateText")
                            )
                        )
                    }
                }
                val result = SpeechDashboardData(
                    totalPracticeSessions = d.optInt("totalPracticeSessions", 18),
                    speakingProgress = d.optInt("speakingProgress", 86),
                    speechClarityFeedback = d.optString("speechClarityFeedback", "88% - Crisp articulation & structured flow"),
                    speakingSpeed = d.optString("speakingSpeed", "142 WPM (Optimal Conversational Range)"),
                    pronunciationFeedback = d.optString("pronunciationFeedback", "84% - Excellent vowel balance"),
                    recommendedNextExercise = d.optString("recommendedNextExercise", "60-Second Elevator Pitch with Zero Filler Words"),
                    history = list
                )
                NetworkResult(true, "Speech history fetched", result)
            } else {
                NetworkResult(false, json.optString("message"))
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}")
        }
    }

    suspend fun fetchSpeechTopic(module: String): NetworkResult<SpeechTopicData> = withContext(Dispatchers.IO) {
        try {
            val (code, text) = getJson("speech/speech_topics_fetch.php?module=$module")
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            if (code == 200 && json.optString("status") == "success") {
                val d = json.getJSONObject("data")
                val t = d.getJSONObject("topic")
                val topic = SpeechTopicData(
                    title = t.optString("title"),
                    prompt = t.optString("prompt"),
                    duration = t.optInt("duration", 60),
                    tip = t.optString("tip")
                )
                NetworkResult(true, "Topic fetched", topic)
            } else {
                NetworkResult(false, json.optString("message"))
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}")
        }
    }

    suspend fun saveSpeechSession(
        userId: Int,
        moduleType: String,
        promptTitle: String,
        promptQuote: String,
        durationSeconds: Int,
        speechScore: Int,
        pronunciationScore: Int,
        fluencyScore: Int,
        clarityScore: Int,
        transcript: String,
        aiTip: String,
        wpm: Int
    ): NetworkResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("user_id", userId)
                put("module_type", moduleType)
                put("prompt_title", promptTitle)
                put("prompt_quote", promptQuote)
                put("duration_seconds", durationSeconds)
                put("speech_score", speechScore)
                put("pronunciation_score", pronunciationScore)
                put("fluency_score", fluencyScore)
                put("clarity_score", clarityScore)
                put("transcript", transcript)
                put("ai_tip", aiTip)
                put("wpm", wpm)
            }
            val (code, text) = postJson("speech/speech_session_save.php", body)
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            NetworkResult(code == 200 && json.optString("status") == "success", json.optString("message"), true)
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}", false)
        }
    }

    suspend fun fetchAnalytics(userId: Int): NetworkResult<ProgressAnalyticsData> = withContext(Dispatchers.IO) {
        try {
            val (code, text) = getJson("progress/analytics_fetch.php?user_id=$userId")
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            if (code == 200 && json.optString("status") == "success") {
                val d = json.getJSONObject("data")
                val skillArr = d.optJSONArray("skillItems") ?: JSONArray()
                val skills = mutableListOf<ProgressSkillItemData>()
                for (i in 0 until skillArr.length()) {
                    val s = skillArr.getJSONObject(i)
                    skills.add(
                        ProgressSkillItemData(
                            name = s.optString("name"),
                            score = s.optInt("score", 70),
                            maxScore = s.optInt("maxScore", 100),
                            isTopTier = s.optBoolean("isTopTier", false)
                        )
                    )
                }

                val recentArr = d.optJSONArray("recentPracticeScores") ?: JSONArray()
                val recentScores = mutableListOf<RecentPracticeScoreData>()
                for (i in 0 until recentArr.length()) {
                    val r = recentArr.getJSONObject(i)
                    val strList = mutableListOf<String>()
                    r.optJSONArray("strengths")?.let { sa ->
                        for (j in 0 until sa.length()) strList.add(sa.getString(j))
                    }
                    val weakList = mutableListOf<String>()
                    r.optJSONArray("weaknesses")?.let { wa ->
                        for (j in 0 until wa.length()) weakList.add(wa.getString(j))
                    }
                    val metMap = mutableMapOf<String, Any>()
                    r.optJSONObject("metrics")?.let { mo ->
                        mo.keys().forEach { k -> metMap[k] = mo.get(k) }
                    }

                    recentScores.add(
                        RecentPracticeScoreData(
                            id = r.optInt("id"),
                            taskTitle = r.optString("taskTitle"),
                            mainSkill = r.optString("mainSkill"),
                            subcategoryName = r.optString("subcategoryName"),
                            durationMinutes = r.optInt("durationMinutes", 10),
                            durationText = r.optString("durationText", "10 min"),
                            xpAwarded = r.optInt("xpAwarded", 35),
                            score = if (r.isNull("score")) null else r.optInt("score"),
                            scoreStatus = r.optString("scoreStatus", "not_available"),
                            scoreReason = if (r.isNull("scoreReason")) null else r.optString("scoreReason"),
                            strengths = strList,
                            weaknesses = weakList,
                            aiFeedback = r.optString("aiFeedback", ""),
                            metrics = metMap,
                            dateText = r.optString("dateText", "Today")
                        )
                    )
                }

                val result = ProgressAnalyticsData(
                    overallScore = d.optInt("overallScore", 78),
                    pointsGainText = d.optString("pointsGainText", "↑ 14 points"),
                    scoreRangeText = d.optString("scoreRangeText", ""),
                    totalSessions = d.optInt("totalSessions", 0),
                    sessionsWeeklyGainText = d.optString("sessionsWeeklyGainText", "↑ 0 this week"),
                    timeInvestedText = d.optString("timeInvestedText", "0 h"),
                    timeWeeklyGainText = d.optString("timeWeeklyGainText", "↑ 0h this week"),
                    skillItems = skills,
                    recentPracticeScores = recentScores
                )
                NetworkResult(true, "Analytics fetched successfully", result)
            } else {
                NetworkResult(false, json.optString("message", "Failed to fetch analytics"))
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}")
        }
    }

    private fun parseSkillGapReport(o: JSONObject): SkillGapReportData {
        val strongList = mutableListOf<SkillAreaItem>()
        o.optJSONArray("strong_areas")?.let { arr ->
            for (i in 0 until arr.length()) {
                val itm = arr.getJSONObject(i)
                strongList.add(
                    SkillAreaItem(
                        name = itm.optString("name"),
                        score = itm.optInt("score", 0),
                        gapDelta = itm.optInt("gap_delta", 0),
                        status = itm.optString("status"),
                        detail = itm.optString("detail")
                    )
                )
            }
        }

        val weakList = mutableListOf<SkillAreaItem>()
        o.optJSONArray("weak_areas")?.let { arr ->
            for (i in 0 until arr.length()) {
                val itm = arr.getJSONObject(i)
                weakList.add(
                    SkillAreaItem(
                        name = itm.optString("name"),
                        score = itm.optInt("score", 0),
                        gapDelta = itm.optInt("gap_delta", 0),
                        status = itm.optString("status"),
                        detail = itm.optString("detail")
                    )
                )
            }
        }

        val needsList = mutableListOf<SkillAreaItem>()
        o.optJSONArray("needs_improvement")?.let { arr ->
            for (i in 0 until arr.length()) {
                val itm = arr.getJSONObject(i)
                needsList.add(
                    SkillAreaItem(
                        name = itm.optString("name"),
                        score = itm.optInt("score", 0),
                        gapDelta = itm.optInt("gap_delta", 0),
                        status = itm.optString("status"),
                        detail = itm.optString("detail")
                    )
                )
            }
        }

        val missingList = mutableListOf<MissingTopicItem>()
        o.optJSONArray("missing_topics")?.let { arr ->
            for (i in 0 until arr.length()) {
                val itm = arr.getJSONObject(i)
                missingList.add(
                    MissingTopicItem(
                        topic = itm.optString("topic"),
                        reason = itm.optString("reason")
                    )
                )
            }
        }

        val recList = mutableListOf<SkillRecommendationItem>()
        o.optJSONArray("recommendations")?.let { arr ->
            for (i in 0 until arr.length()) {
                val itm = arr.getJSONObject(i)
                recList.add(
                    SkillRecommendationItem(
                        title = itm.optString("title"),
                        description = itm.optString("description"),
                        actionType = itm.optString("action_type"),
                        actionLabel = itm.optString("action_label", "Start")
                    )
                )
            }
        }

        val compList = mutableListOf<SkillCompetencyItem>()
        o.optJSONArray("competencies")?.let { arr ->
            for (i in 0 until arr.length()) {
                val itm = arr.getJSONObject(i)
                compList.add(
                    SkillCompetencyItem(
                        key = itm.optString("key"),
                        name = itm.optString("name"),
                        description = itm.optString("description"),
                        score = itm.optInt("score"),
                        gapDelta = itm.optInt("gap_delta"),
                        targetScore = itm.optInt("target_score", 100)
                    )
                )
            }
        }

        val prevComp = o.optJSONObject("previous_comparison")?.let { p ->
            SkillGapComparison(
                previousScore = p.optInt("previous_score"),
                scoreDelta = p.optInt("score_delta"),
                previousDate = p.optString("previous_date"),
                summary = p.optString("summary")
            )
        }

        return SkillGapReportData(
            reportId = if (o.isNull("report_id")) null else o.optInt("report_id"),
            status = o.optString("status", "success"),
            dataSufficiency = o.optString("data_sufficiency", "sufficient"),
            userId = o.optInt("user_id", 1),
            mainSkill = o.optString("main_skill", "music"),
            subcategoryId = o.optString("subcategory_id", "singing"),
            subcategoryName = o.optString("subcategory_name", "Singing"),
            proficiencyLevel = o.optString("proficiency_level", "Beginner"),
            overallCompetencyScore = o.optInt("overall_competency_score", 0),
            gapSeverity = o.optString("gap_severity", "Moderate Gap"),
            assessmentCount = o.optInt("assessment_count", 0),
            aiSummary = o.optString("ai_summary", ""),
            strongAreas = strongList,
            weakAreas = weakList,
            needsImprovement = needsList,
            missingTopics = missingList,
            recommendations = recList,
            competencies = compList,
            previousComparison = prevComp
        )
    }

    suspend fun fetchSkillGapData(
        userId: Int,
        subcategoryId: String? = null
    ): NetworkResult<SkillGapResponseData> = withContext(Dispatchers.IO) {
        try {
            val q = if (!subcategoryId.isNullOrBlank()) {
                "gap_detector/gap_fetch.php?user_id=$userId&subcategory_id=$subcategoryId"
            } else {
                "gap_detector/gap_fetch.php?user_id=$userId"
            }
            val (code, text) = getJson(q)
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            if (code == 200 && json.optString("status") == "success") {
                val d = json.optJSONObject("data") ?: JSONObject()
                val curRepObj = d.optJSONObject("current_report") ?: JSONObject()
                val curReport = parseSkillGapReport(curRepObj)

                val availList = mutableListOf<AvailableSkillItem>()
                d.optJSONArray("available_skills")?.let { arr ->
                    for (i in 0 until arr.length()) {
                        val s = arr.getJSONObject(i)
                        availList.add(
                            AvailableSkillItem(
                                id = s.optInt("id"),
                                mainSkill = s.optString("main_skill"),
                                subcategoryId = s.optString("subcategory_id"),
                                subcategoryName = s.optString("subcategory_name"),
                                proficiencyLevel = s.optString("proficiency_level", "Beginner"),
                                isActive = s.optBoolean("is_active", false),
                                lessonsCompleted = s.optInt("lessons_completed", 0),
                                xpEarned = s.optInt("xp_earned", 0)
                            )
                        )
                    }
                }

                val histList = mutableListOf<SkillGapHistoryItem>()
                d.optJSONArray("history")?.let { arr ->
                    for (i in 0 until arr.length()) {
                        val h = arr.getJSONObject(i)
                        histList.add(
                            SkillGapHistoryItem(
                                reportId = h.optInt("report_id"),
                                score = h.optInt("score"),
                                gapSeverity = h.optString("gap_severity"),
                                assessmentCount = h.optInt("assessment_count"),
                                dateFormatted = h.optString("date_formatted")
                            )
                        )
                    }
                }

                val res = SkillGapResponseData(
                    currentReport = curReport,
                    availableSkills = availList,
                    history = histList
                )
                NetworkResult(true, "Skill gap data retrieved", res)
            } else {
                NetworkResult(false, json.optString("message", "Failed to retrieve skill gap data"))
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}")
        }
    }

    suspend fun analyzeSkillGap(
        userId: Int,
        mainSkill: String,
        subcategoryId: String
    ): NetworkResult<SkillGapReportData> = withContext(Dispatchers.IO) {
        try {
            val payload = JSONObject().apply {
                put("user_id", userId)
                put("main_skill", mainSkill)
                put("subcategory_id", subcategoryId)
            }
            val (code, text) = postJson("gap_detector/gap_analyze.php", payload)
            val json = if (text.isNotEmpty()) JSONObject(text) else JSONObject()
            if (code == 200 && json.optString("status") == "success") {
                val d = json.optJSONObject("data") ?: JSONObject()
                val rep = parseSkillGapReport(d)
                NetworkResult(true, "Analysis completed", rep)
            } else {
                NetworkResult(false, json.optString("message", "Failed to analyze skills"))
            }
        } catch (e: Exception) {
            NetworkResult(false, "Network error: ${e.localizedMessage}")
        }
    }
}

