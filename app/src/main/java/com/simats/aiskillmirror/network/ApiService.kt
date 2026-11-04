package com.simats.aiskillmirror.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    // Auth
    @POST("auth/signup.php")
    suspend fun signup(
        @Body request: SignupRequest
    ): Response<AuthResponse>

    @POST("auth/login.php")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("auth/recovery.php")
    suspend fun recoverPassword(
        @Body request: RecoveryRequest
    ): Response<SimpleMessageResponse>

    // Profile
    @GET("profile/profile_fetch.php")
    suspend fun getProfile(
        @Query("user_id") userId: Int
    ): Response<ProfileResponse>

    @POST("profile/profile_edit.php")
    suspend fun editProfile(
        @Body request: ProfileEditRequest
    ): Response<SimpleMessageResponse>

    @Multipart
    @POST("profile/profile_upload_picture.php")
    suspend fun uploadProfilePicture(
        @Part("user_id") userId: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<ProfileUploadResponse>

    // Dashboard
    @GET("dashboard/dashboard_fetch.php")
    suspend fun getDashboard(
        @Query("user_id") userId: Int
    ): Response<DashboardResponse>

    // Assessment
    @GET("assessment/skills_fetch.php")
    suspend fun getSkills(
        @Query("user_id") userId: Int
    ): Response<SkillsResponse>

    @GET("assessment/questions_fetch.php")
    suspend fun getQuestions(
        @Query("skill_id") skillId: String
    ): Response<QuestionsResponse>

    @POST("assessment/submit_assessment.php")
    suspend fun submitAssessment(
        @Body request: SubmitAssessmentRequest
    ): Response<SubmitAssessmentResponse>

    // Skill Mirror & Progress
    @GET("progress/skill_mirror_fetch.php")
    suspend fun getSkillMirror(
        @Query("user_id") userId: Int
    ): Response<SkillMirrorResponse>

    // Roadmap
    @GET("roadmap/roadmap_fetch.php")
    suspend fun getRoadmap(
        @Query("user_id") userId: Int
    ): Response<RoadmapResponse>

    // Training
    @GET("training/training_fetch.php")
    suspend fun getTraining(
        @Query("user_id") userId: Int
    ): Response<TrainingResponse>

    @POST("training/lesson_complete.php")
    suspend fun completeLesson(
        @Body request: LessonCompleteRequest
    ): Response<LessonCompleteResponse>

    // Speech Studio & Analysis
    @GET("speech/speech_studio_fetch.php")
    suspend fun getSpeechStudio(
        @Query("user_id") userId: Int
    ): Response<SpeechStudioResponse>

    @GET("speech/speech_analysis_fetch.php")
    suspend fun getSpeechAnalysis(
        @Query("user_id") userId: Int
    ): Response<SpeechAnalysisResponse>

    // Milestones
    @GET("milestones/milestones_fetch.php")
    suspend fun getMilestones(
        @Query("user_id") userId: Int
    ): Response<MilestonesResponse>

    // Notifications
    @GET("notifications/notifications_fetch.php")
    suspend fun getNotifications(
        @Query("user_id") userId: Int
    ): Response<NotificationsResponse>

    // Coach Chat
    @GET("coach/coach_chat_fetch.php")
    suspend fun getCoachChat(
        @Query("user_id") userId: Int
    ): Response<CoachChatResponse>

    @POST("coach/coach_send_message.php")
    suspend fun sendCoachMessage(
        @Body request: CoachMessageRequest
    ): Response<CoachSendMessageResponse>

    // Settings
    @GET("settings/settings_fetch.php")
    suspend fun getSettings(
        @Query("user_id") userId: Int
    ): Response<SettingsResponse>

    @POST("settings/settings_update.php")
    suspend fun updateSettings(
        @Body request: SettingsUpdateRequest
    ): Response<SimpleMessageResponse>

    // Reminders
    @GET("reminders/reminders_fetch.php")
    suspend fun getReminders(
        @Query("user_id") userId: Int
    ): Response<RemindersResponse>

    @POST("reminders/reminder_create.php")
    suspend fun createReminder(
        @Body request: ReminderCreateRequest
    ): Response<SimpleMessageResponse>

    @POST("reminders/reminder_toggle.php")
    suspend fun toggleReminder(
        @Body request: ReminderToggleRequest
    ): Response<SimpleMessageResponse>

    @POST("reminders/reminder_delete.php")
    suspend fun deleteReminder(
        @Body request: ReminderDeleteRequest
    ): Response<SimpleMessageResponse>

    // Multi-Modal Performance Upload & History
    @Multipart
    @POST("upload/performance_upload.php")
    suspend fun uploadPerformance(
        @Part("user_id") userId: RequestBody,
        @Part("media_type") mediaType: RequestBody,
        @Part("skill_category") skillCategory: RequestBody,
        @Part("title") title: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<PerformanceUploadResponse>

    @GET("upload/performance_history_fetch.php")
    suspend fun getPerformanceHistory(
        @Query("user_id") userId: Int
    ): Response<PerformanceHistoryResponse>
}

