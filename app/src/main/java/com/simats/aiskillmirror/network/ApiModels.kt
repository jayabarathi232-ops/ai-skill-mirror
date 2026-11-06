package com.simats.aiskillmirror.network

import com.google.gson.annotations.SerializedName

// Generic base response
data class ApiResponse<T>(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: T? = null
)

data class SimpleMessageResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null
)

// Auth
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class SignupRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RecoveryRequest(
    @SerializedName("email") val email: String
)

data class AuthUserDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("user_id") val userId: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("email") val email: String = ""
) {
    fun getResolvedId(): Int = if (id > 0) id else userId
}

data class AuthResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("user") val user: AuthUserDto? = null
)

// Profile
data class ProfileDto(
    @SerializedName("user_id") val userId: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("phone") val phone: String? = "",
    @SerializedName("education") val education: String? = "",
    @SerializedName("skills") val skills: String? = "",
    @SerializedName("career_goal") val careerGoal: String? = "",
    @SerializedName("bio") val bio: String? = "",
    @SerializedName("profile_picture") val profilePicture: String? = ""
)

data class ProfileResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("profile") val profile: ProfileDto? = null
)

data class ProfileEditRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("name") val name: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("education") val education: String? = null,
    @SerializedName("skills") val skills: String? = null,
    @SerializedName("career_goal") val careerGoal: String? = null,
    @SerializedName("bio") val bio: String? = null
)

data class ProfileUploadResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("url") val url: String? = null
)

// Dashboard
data class DashboardDto(
    @SerializedName("greeting") val greeting: String? = "Good morning,",
    @SerializedName("currentDateText") val currentDateText: String? = null,
    @SerializedName("userName") val userName: String = "",
    @SerializedName("profilePicture") val profilePicture: String? = null,
    @SerializedName("skillScore") val skillScore: Int = 0,
    @SerializedName("monthlyPtsGain") val monthlyPtsGain: Int = 0,
    @SerializedName("assessedSkillsCount") val assessedSkillsCount: Int = 0,
    @SerializedName("learnerStatus") val learnerStatus: String = "",
    @SerializedName("currentStreakDays") val currentStreakDays: Int = 0,
    @SerializedName("minutesToday") val minutesToday: Int = 0,
    @SerializedName("goalMinutesToday") val goalMinutesToday: Int = 20,
    @SerializedName("minutesSubtitle") val minutesSubtitle: String? = null,
    @SerializedName("streakSubtitle") val streakSubtitle: String? = null,
    @SerializedName("headline") val headline: String? = null,
    @SerializedName("currentPlanTitle") val currentPlanTitle: String? = null,
    @SerializedName("currentLessonTitle") val currentLessonTitle: String = "",
    @SerializedName("currentLessonCategory") val currentLessonCategory: String = "",
    @SerializedName("currentLessonDuration") val currentLessonDuration: String = "",
    @SerializedName("currentLessonProgress") val currentLessonProgress: Double = 0.0,
    @SerializedName("completedLessonsCount") val completedLessonsCount: Int = 0,
    @SerializedName("totalLessonsCount") val totalLessonsCount: Int = 0,
    @SerializedName("remainingLessonsCount") val remainingLessonsCount: Int = 0
)

data class DashboardResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: DashboardDto? = null
)

// Assessment
data class SkillItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String = "",
    @SerializedName("lastScore") val lastScore: Int = 0,
    @SerializedName("maxScore") val maxScore: Int = 100,
    @SerializedName("iconType") val iconType: String = "TECHNICAL"
)

data class SkillsResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: List<SkillItemDto>? = null
)

data class OptionDto(
    @SerializedName("key") val key: String,
    @SerializedName("text") val text: String,
    @SerializedName("points") val points: Int = 0
)

data class QuestionDto(
    @SerializedName("id") val id: Int,
    @SerializedName("topicBadge") val topicBadge: String = "",
    @SerializedName("headline") val headline: String = "",
    @SerializedName("scenario") val scenario: String = "",
    @SerializedName("options") val options: List<OptionDto> = emptyList()
)

data class QuestionsResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: List<QuestionDto>? = null
)

data class SubmitAnswerDto(
    @SerializedName("question_id") val questionId: Int,
    @SerializedName("selected_key") val selectedKey: String
)

data class SubmitAssessmentRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("skill_id") val skillId: String,
    @SerializedName("answers") val answers: List<SubmitAnswerDto>
)

data class AssessmentSubmitDto(
    @SerializedName("skillId") val skillId: String,
    @SerializedName("score") val score: Int,
    @SerializedName("overallScore") val overallScore: Int,
    @SerializedName("pointsGained") val pointsGained: Int
)

data class SubmitAssessmentResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: AssessmentSubmitDto? = null
)

// Skill Mirror
data class SkillMirrorScoreDto(
    @SerializedName("name") val name: String,
    @SerializedName("score") val score: Int,
    @SerializedName("maxScore") val maxScore: Int = 100,
    @SerializedName("isTopTier") val isTopTier: Boolean = false
)

data class SkillMirrorDto(
    @SerializedName("overallScore") val overallScore: Int,
    @SerializedName("statusBadge") val statusBadge: String,
    @SerializedName("pointsGainSubtitle") val pointsGainSubtitle: String,
    @SerializedName("skillCountText") val skillCountText: String,
    @SerializedName("skillScores") val skillScores: List<SkillMirrorScoreDto> = emptyList(),
    @SerializedName("nextMoveTitle") val nextMoveTitle: String,
    @SerializedName("nextMoveDescription") val nextMoveDescription: String,
    @SerializedName("lastUpdatedText") val lastUpdatedText: String
)

data class SkillMirrorResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: SkillMirrorDto? = null
)

// Roadmap
data class RoadmapStepDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("subtitle") val subtitle: String,
    @SerializedName("lessonCountText") val lessonCountText: String,
    @SerializedName("iconType") val iconType: String
)

data class GrowthRoadmapDto(
    @SerializedName("currentWeekBadgeText") val currentWeekBadgeText: String,
    @SerializedName("planTitle") val planTitle: String,
    @SerializedName("planSubtitle") val planSubtitle: String,
    @SerializedName("planProgress") val planProgress: Double,
    @SerializedName("planProgressText") val planProgressText: String,
    @SerializedName("activeStepTitle") val activeStepTitle: String,
    @SerializedName("activeStepSubtitle") val activeStepSubtitle: String,
    @SerializedName("activeStepProgressPercentage") val activeStepProgressPercentage: String,
    @SerializedName("activeStepDescription") val activeStepDescription: String,
    @SerializedName("activeStepTags") val activeStepTags: List<String> = emptyList(),
    @SerializedName("upcomingSteps") val upcomingSteps: List<RoadmapStepDto> = emptyList(),
    @SerializedName("continueButtonText") val continueButtonText: String
)

data class RoadmapResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: GrowthRoadmapDto? = null
)

// Training
data class TrainingLessonDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("category") val category: String,
    @SerializedName("durationText") val durationText: String,
    @SerializedName("xpText") val xpText: String,
    @SerializedName("isCompleted") val isCompleted: Boolean
)

data class DailyTrainingDto(
    @SerializedName("headerDateText") val headerDateText: String,
    @SerializedName("streakBadgeText") val streakBadgeText: String,
    @SerializedName("currentMinutes") val currentMinutes: Int,
    @SerializedName("targetMinutes") val targetMinutes: Int,
    @SerializedName("practiceProgressText") val practiceProgressText: String,
    @SerializedName("completedPracticeItems") val completedPracticeItems: List<TrainingLessonDto> = emptyList(),
    @SerializedName("upNextTitle") val upNextTitle: String,
    @SerializedName("upNextCategory") val upNextCategory: String,
    @SerializedName("upNextMetaText") val upNextMetaText: String,
    @SerializedName("upNextDescription") val upNextDescription: String,
    @SerializedName("startButtonText") val startButtonText: String
)

data class TrainingResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: DailyTrainingDto? = null
)

data class LessonCompleteRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("lesson_id") val lessonId: String,
    @SerializedName("minutes") val minutes: Int = 10
)

data class LessonCompleteResultDto(
    @SerializedName("minutesToday") val minutesToday: Int,
    @SerializedName("skillScore") val skillScore: Int,
    @SerializedName("xpGained") val xpGained: Int
)

data class LessonCompleteResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: LessonCompleteResultDto? = null
)

// Speech Studio & Analysis
data class SpeechStudioDto(
    @SerializedName("promptTitle") val promptTitle: String,
    @SerializedName("promptSubtitle") val promptSubtitle: String,
    @SerializedName("targetDurationSeconds") val targetDurationSeconds: Int = 60
)

data class SpeechStudioResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: SpeechStudioDto? = null
)

data class SpeechAnalysisDto(
    @SerializedName("speechScore") val speechScore: Int,
    @SerializedName("baselineGainText") val baselineGainText: String,
    @SerializedName("headlineLine1") val headlineLine1: String,
    @SerializedName("headlineLine2") val headlineLine2: String,
    @SerializedName("sessionDurationText") val sessionDurationText: String,
    @SerializedName("pronunciationScore") val pronunciationScore: Int,
    @SerializedName("fluencyScore") val fluencyScore: Int,
    @SerializedName("clarityScore") val clarityScore: Int,
    @SerializedName("transcriptPart1") val transcriptPart1: String,
    @SerializedName("transcriptHighlightPronunciation") val transcriptHighlightPronunciation: String,
    @SerializedName("transcriptPart2") val transcriptPart2: String,
    @SerializedName("transcriptHighlightFiller") val transcriptHighlightFiller: String,
    @SerializedName("transcriptPart3") val transcriptPart3: String,
    @SerializedName("aiTipText") val aiTipText: String
)

data class SpeechAnalysisResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: SpeechAnalysisDto? = null
)

// Milestones
data class MilestoneAchievementDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("earnedDateText") val earnedDateText: String,
    @SerializedName("iconType") val iconType: String
)

data class MilestonesDto(
    @SerializedName("mainGoalTitle") val mainGoalTitle: String,
    @SerializedName("currentScore") val currentScore: Int,
    @SerializedName("targetScore") val targetScore: Int,
    @SerializedName("statusText") val statusText: String,
    @SerializedName("targetDateText") val targetDateText: String,
    @SerializedName("streakTitle") val streakTitle: String,
    @SerializedName("streakSubtitle") val streakSubtitle: String,
    @SerializedName("streakProgressText") val streakProgressText: String,
    @SerializedName("earnedCountText") val earnedCountText: String,
    @SerializedName("achievements") val achievements: List<MilestoneAchievementDto> = emptyList(),
    @SerializedName("nextRewardTitle") val nextRewardTitle: String,
    @SerializedName("nextRewardSubtitle") val nextRewardSubtitle: String,
    @SerializedName("nextRewardTarget") val nextRewardTarget: Int,
    @SerializedName("totalXpText") val totalXpText: String
)

data class MilestonesResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: MilestonesDto? = null
)

// Notifications
data class NotificationItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("timestampText") val timestampText: String,
    @SerializedName("badgeText") val badgeText: String?,
    @SerializedName("isGreenBadge") val isGreenBadge: Boolean,
    @SerializedName("iconType") val iconType: String,
    @SerializedName("category") val category: String,
    @SerializedName("is_read") val isRead: Boolean = false
)

data class NotificationsDto(
    @SerializedName("unreadCount") val unreadCount: Int = 0,
    @SerializedName("isDailyReminderEnabled") val isDailyReminderEnabled: Boolean,
    @SerializedName("reminderTimeText") val reminderTimeText: String,
    @SerializedName("reminderDaysText") val reminderDaysText: String? = null,
    @SerializedName("reminderNoticeText") val reminderNoticeText: String,
    @SerializedName("todayNotifications") val todayNotifications: List<NotificationItemDto> = emptyList(),
    @SerializedName("yesterdayNotifications") val yesterdayNotifications: List<NotificationItemDto> = emptyList(),
    @SerializedName("olderNotifications") val olderNotifications: List<NotificationItemDto> = emptyList()
)

data class NotificationsResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: NotificationsDto? = null
)

// Coach Chat
data class ChatMessageDto(
    @SerializedName("id") val id: String,
    @SerializedName("text") val text: String,
    @SerializedName("isUser") val isUser: Boolean,
    @SerializedName("timestamp") val timestamp: String
)

data class CoachChatDto(
    @SerializedName("coachName") val coachName: String,
    @SerializedName("statusText") val statusText: String,
    @SerializedName("bannerNoticeText") val bannerNoticeText: String,
    @SerializedName("timestampHeader") val timestampHeader: String,
    @SerializedName("messages") val messages: List<ChatMessageDto> = emptyList(),
    @SerializedName("suggestionChips") val suggestionChips: List<String> = emptyList()
)

data class CoachChatResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: CoachChatDto? = null
)

data class CoachMessageRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("message") val message: String,
    @SerializedName("language") val language: String? = "en"
)

data class ClearCoachChatRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("language") val language: String? = "en"
)

data class CoachSendMessageDataDto(
    @SerializedName("userMessage") val userMessage: ChatMessageDto,
    @SerializedName("botMessage") val botMessage: ChatMessageDto
)

data class CoachSendMessageResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: CoachSendMessageDataDto? = null
)

// Settings
data class UserSettingsDto(
    @SerializedName("isLearningRemindersEnabled") val isLearningRemindersEnabled: Boolean,
    @SerializedName("isPersonalizedAiEnabled") val isPersonalizedAiEnabled: Boolean,
    @SerializedName("isSaveVoiceRecordingsEnabled") val isSaveVoiceRecordingsEnabled: Boolean,
    @SerializedName("currentAppearanceText") val currentAppearanceText: String,
    @SerializedName("currentLanguageText") val currentLanguageText: String,
    @SerializedName("appVersionText") val appVersionText: String
)

data class SettingsResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: UserSettingsDto? = null
)

data class SettingsUpdateRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("learningReminders") val learningReminders: Boolean? = null,
    @SerializedName("personalizedAi") val personalizedAi: Boolean? = null,
    @SerializedName("saveVoiceRecordings") val saveVoiceRecordings: Boolean? = null,
    @SerializedName("appearance") val appearance: String? = null,
    @SerializedName("language") val language: String? = null
)

// Reminders
data class ReminderItemDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("reminder_time") val reminderTime: String,
    @SerializedName("reminder_days") val reminderDays: String,
    @SerializedName("is_active") val isActive: Boolean
)

data class RemindersResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: List<ReminderItemDto>? = null
)

data class ReminderCreateRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("reminder_time") val reminderTime: String,
    @SerializedName("reminder_days") val reminderDays: String
)

data class ReminderToggleRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("reminder_id") val reminderId: Int,
    @SerializedName("is_active") val isActive: Boolean
)

data class ReminderDeleteRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("reminder_id") val reminderId: Int
)

// Multi-Modal Performance Upload & History
data class PerformanceUploadItemDto(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("media_type") val mediaType: String,
    @SerializedName("skill_category") val skillCategory: String,
    @SerializedName("title") val title: String,
    @SerializedName("file_name") val fileName: String,
    @SerializedName("file_size_bytes") val fileSizeBytes: Long,
    @SerializedName("duration_seconds") val durationSeconds: Int? = null,
    @SerializedName("file_url") val fileUrl: String,
    @SerializedName("relative_path") val relativePath: String,
    @SerializedName("score") val score: Int,
    @SerializedName("analysis_summary") val analysisSummary: String,
    @SerializedName("ai_feedback") val aiFeedback: String,
    @SerializedName("gemini_configured") val geminiConfigured: Boolean = false,
    @SerializedName("gemini_note") val geminiNote: String? = null,
    @SerializedName("metrics") val metrics: Map<String, Any>? = null,
    @SerializedName("created_at") val createdAt: String
)

data class PerformanceUploadResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: PerformanceUploadItemDto? = null
)

data class PerformanceHistoryDataDto(
    @SerializedName("total_uploads") val totalUploads: Int,
    @SerializedName("uploads") val uploads: List<PerformanceUploadItemDto> = emptyList()
)

data class PerformanceHistoryResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: PerformanceHistoryDataDto? = null
)


