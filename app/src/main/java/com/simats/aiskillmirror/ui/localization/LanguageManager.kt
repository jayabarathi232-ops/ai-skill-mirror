package com.simats.aiskillmirror.ui.localization

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String
) {
    ENGLISH("en", "English", "English"),
    TAMIL("ta", "Tamil", "தமிழ்");

    companion object {
        fun fromCodeOrName(value: String): AppLanguage {
            return entries.firstOrNull {
                it.code.equals(value, ignoreCase = true) ||
                it.displayName.equals(value, ignoreCase = true) ||
                it.nativeName.equals(value, ignoreCase = true)
            } ?: ENGLISH
        }
    }
}

object LanguageManager {
    private const val PREFS_NAME = "skill_mirror_lang_prefs"
    private const val KEY_LANGUAGE = "pref_app_language"

    var currentLanguage by mutableStateOf(AppLanguage.ENGLISH)
        private set

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val code = prefs.getString(KEY_LANGUAGE, AppLanguage.ENGLISH.code) ?: AppLanguage.ENGLISH.code
        currentLanguage = AppLanguage.fromCodeOrName(code)
    }

    fun setLanguage(context: Context?, language: AppLanguage) {
        currentLanguage = language
        context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            ?.edit()
            ?.putString(KEY_LANGUAGE, language.code)
            ?.apply()

        // Also update Android system configuration locale
        context?.let { ctx ->
            try {
                val locale = Locale(language.code)
                Locale.setDefault(locale)
                val config = ctx.resources.configuration
                config.setLocale(locale)
                ctx.resources.updateConfiguration(config, ctx.resources.displayMetrics)
            } catch (ignored: Exception) {}
        }
    }

    private val translations: Map<String, Map<AppLanguage, String>> = mapOf(
        // Navigation
        "nav_home" to mapOf(AppLanguage.ENGLISH to "Home", AppLanguage.TAMIL to "முகப்பு"),
        "nav_skills" to mapOf(AppLanguage.ENGLISH to "Skills", AppLanguage.TAMIL to "திறன்கள்"),
        "nav_coach" to mapOf(AppLanguage.ENGLISH to "AI Coach", AppLanguage.TAMIL to "AI பயிற்சியாளர்"),
        "nav_progress" to mapOf(AppLanguage.ENGLISH to "Comparison", AppLanguage.TAMIL to "ஒப்பீடு"),
        "nav_profile" to mapOf(AppLanguage.ENGLISH to "Profile", AppLanguage.TAMIL to "சுயவிவரம்"),

        // Make It Yours Screen
        "make_it_yours" to mapOf(AppLanguage.ENGLISH to "Make it yours", AppLanguage.TAMIL to "உங்கள் விருப்பப்படி"),
        "account_privacy" to mapOf(AppLanguage.ENGLISH to "ACCOUNT & PRIVACY", AppLanguage.TAMIL to "கணக்கு மற்றும் தனியுரிமை"),
        "account_section" to mapOf(AppLanguage.ENGLISH to "Account", AppLanguage.TAMIL to "கணக்கு"),
        "personal_info" to mapOf(AppLanguage.ENGLISH to "Personal information", AppLanguage.TAMIL to "தனிப்பட்ட தகவல்"),
        "password_security" to mapOf(AppLanguage.ENGLISH to "Password & security", AppLanguage.TAMIL to "கடவுச்சொல் மற்றும் பாதுகாப்பு"),
        "learning_reminders" to mapOf(AppLanguage.ENGLISH to "Learning reminders", AppLanguage.TAMIL to "கற்றல் நினைவூட்டல்கள்"),
        "reminder_time" to mapOf(AppLanguage.ENGLISH to "Daily Practice Reminder Time", AppLanguage.TAMIL to "தினசரி பயிற்சி நினைவூட்டல் நேரம்"),
        "send_test_reminder" to mapOf(AppLanguage.ENGLISH to "Send Test Reminder", AppLanguage.TAMIL to "சோதனை நினைவூட்டலை அனுப்பு"),
        "privacy_intelligence" to mapOf(AppLanguage.ENGLISH to "Privacy & intelligence", AppLanguage.TAMIL to "தனியுரிமை மற்றும் நுண்ணறிவு"),
        "personalized_ai" to mapOf(AppLanguage.ENGLISH to "Personalized AI insights", AppLanguage.TAMIL to "தனிப்பயனாக்கப்பட்ட AI நுண்ணறிவுகள்"),
        "save_voice" to mapOf(AppLanguage.ENGLISH to "Save voice recordings", AppLanguage.TAMIL to "குரல் பதிவுகளைச் சேமிக்கவும்"),
        "data_permissions" to mapOf(AppLanguage.ENGLISH to "Data & permissions", AppLanguage.TAMIL to "தரவு மற்றும் அனுமதிகள்"),
        "download_data" to mapOf(AppLanguage.ENGLISH to "Download my data", AppLanguage.TAMIL to "எனது தரவைப் பதிவிறக்கவும்"),
        "preferences_section" to mapOf(AppLanguage.ENGLISH to "Preferences", AppLanguage.TAMIL to "விருப்பத்தேர்வுகள்"),
        "appearance" to mapOf(AppLanguage.ENGLISH to "Appearance", AppLanguage.TAMIL to "தோற்றம்"),
        "dark_mode" to mapOf(AppLanguage.ENGLISH to "Dark Mode", AppLanguage.TAMIL to "இருண்ட பயன்முறை"),
        "theme_customization" to mapOf(AppLanguage.ENGLISH to "Theme Customization", AppLanguage.TAMIL to "தீம் தனிப்பயனாக்கம்"),
        "sound_settings" to mapOf(AppLanguage.ENGLISH to "Sound Settings", AppLanguage.TAMIL to "ஒலி அமைப்புகள்"),
        "sound_effects" to mapOf(AppLanguage.ENGLISH to "Sound Effects", AppLanguage.TAMIL to "ஒலி விளைவுகள்"),
        "audio_guidance" to mapOf(AppLanguage.ENGLISH to "Voice & Audio Guidance", AppLanguage.TAMIL to "குரல் மற்றும் ஆடியோ வழிகாட்டுதல்"),
        "language" to mapOf(AppLanguage.ENGLISH to "Language", AppLanguage.TAMIL to "மொழி"),
        "help_support" to mapOf(AppLanguage.ENGLISH to "Help & support", AppLanguage.TAMIL to "உதவி மற்றும் ஆதரவு"),
        "logout" to mapOf(AppLanguage.ENGLISH to "Log out →", AppLanguage.TAMIL to "வெளியேறு →"),
        "app_version" to mapOf(AppLanguage.ENGLISH to "AI Skills Mirror · Version 1.0.0", AppLanguage.TAMIL to "AI ஸ்கில்ஸ் மிரர் · பதிப்பு 1.0.0"),

        // Weekly Progress & Weekly Comparison
        "weekly_progress" to mapOf(AppLanguage.ENGLISH to "Weekly Progress", AppLanguage.TAMIL to "வாராந்திர முன்னேற்றம்"),
        "weekly_comparison" to mapOf(AppLanguage.ENGLISH to "Weekly Comparison", AppLanguage.TAMIL to "வாராந்திர ஒப்பீடு"),
        "nav_comparison" to mapOf(AppLanguage.ENGLISH to "Comparison", AppLanguage.TAMIL to "ஒப்பீடு"),
        "curr_week" to mapOf(AppLanguage.ENGLISH to "Current Week", AppLanguage.TAMIL to "தற்போதைய வாரம்"),
        "week_in_progress" to mapOf(AppLanguage.ENGLISH to "Week in Progress", AppLanguage.TAMIL to "நடைபெறும் வாரம்"),
        "practice_time_comp" to mapOf(AppLanguage.ENGLISH to "Practice Time Comparison", AppLanguage.TAMIL to "பயிற்சி நேர ஒப்பீடு"),
        "skill_performance_comp" to mapOf(AppLanguage.ENGLISH to "Skill-wise Performance Comparison", AppLanguage.TAMIL to "திறன் வாரியான செயல்திறன் ஒப்பீடு"),
        "completed_tasks_comp" to mapOf(AppLanguage.ENGLISH to "Completed Tasks Comparison", AppLanguage.TAMIL to "முடித்த பணிகள் ஒப்பீடு"),
        "ai_weekly_summary" to mapOf(AppLanguage.ENGLISH to "AI Weekly Improvement Summary", AppLanguage.TAMIL to "AI வாராந்திர முன்னேற்றச் சுருக்கம்"),
        "retry_ai_summary" to mapOf(AppLanguage.ENGLISH to "Retry AI Summary", AppLanguage.TAMIL to "AI சுருக்கத்தை மீண்டும் முயற்சிக்கவும்"),
        "not_enough_data" to mapOf(AppLanguage.ENGLISH to "Not Enough Data", AppLanguage.TAMIL to "போதுமான தரவு இல்லை"),
        "increased" to mapOf(AppLanguage.ENGLISH to "Increased", AppLanguage.TAMIL to "அதிகரித்துள்ளது"),
        "decreased" to mapOf(AppLanguage.ENGLISH to "Decreased", AppLanguage.TAMIL to "குறைந்துள்ளது"),
        "no_change" to mapOf(AppLanguage.ENGLISH to "No Change", AppLanguage.TAMIL to "மாற்றமில்லை"),
        "diff" to mapOf(AppLanguage.ENGLISH to "Difference", AppLanguage.TAMIL to "வேறுபாடு"),
        "fair_comparison" to mapOf(AppLanguage.ENGLISH to "Fair Elapsed Comparison", AppLanguage.TAMIL to "நியாயமான கால ஒப்பீடு"),
        "mon_to_sun_activity" to mapOf(AppLanguage.ENGLISH to "Monday to Sunday activity", AppLanguage.TAMIL to "திங்கள் முதல் ஞாயிறு வரையிலான செயல்பாடு"),
        "practice" to mapOf(AppLanguage.ENGLISH to "Practice", AppLanguage.TAMIL to "பயிற்சி"),
        "tasks_done" to mapOf(AppLanguage.ENGLISH to "Tasks Done", AppLanguage.TAMIL to "முடித்த பணிகள்"),
        "xp_earned" to mapOf(AppLanguage.ENGLISH to "XP Earned", AppLanguage.TAMIL to "பெற்ற XP"),
        "weekly_goal" to mapOf(AppLanguage.ENGLISH to "Weekly Goal", AppLanguage.TAMIL to "வாராந்திர இலக்கு"),
        "daily_practice_chart" to mapOf(AppLanguage.ENGLISH to "Daily Practice Chart", AppLanguage.TAMIL to "தினசரி பயிற்சி விளக்கப்படம்"),
        "practice_details" to mapOf(AppLanguage.ENGLISH to "Practice Details", AppLanguage.TAMIL to "பயிற்சி விவரங்கள்"),
        "no_activity_today" to mapOf(AppLanguage.ENGLISH to "No practice activity for this day", AppLanguage.TAMIL to "இந்த நாளில் பயிற்சி நடவடிக்கை எதுவும் இல்லை"),
        "no_activity_desc" to mapOf(AppLanguage.ENGLISH to "No recorded practice sessions or quizzes on this date. Complete a training lesson or speech exercise to see your progress here.", AppLanguage.TAMIL to "இந்த தேதியில் எந்த அமர்வுகளும் பதிவு செய்யப்படவில்லை. உங்கள் முன்னேற்றத்தைக் காண பயிற்சிப் பாடத்தை முடிக்கவும்."),
        "skills_practiced" to mapOf(AppLanguage.ENGLISH to "Skills Practiced", AppLanguage.TAMIL to "பயிற்சி செய்த திறன்கள்"),
        "start_practice" to mapOf(AppLanguage.ENGLISH to "Start Practice Now", AppLanguage.TAMIL to "இப்போது பயிற்சி செய்யுங்கள்"),
        "total_time" to mapOf(AppLanguage.ENGLISH to "Total Practice Time", AppLanguage.TAMIL to "மொத்த பயிற்சி நேரம்"),
        "completed_tasks" to mapOf(AppLanguage.ENGLISH to "Completed Tasks", AppLanguage.TAMIL to "முடித்த பணிகள்"),
        "this_week" to mapOf(AppLanguage.ENGLISH to "This Week", AppLanguage.TAMIL to "இந்த வாரம்"),
        "prev_week" to mapOf(AppLanguage.ENGLISH to "◀ Prev Week", AppLanguage.TAMIL to "◀ முந்தைய வாரம்"),
        "next_week" to mapOf(AppLanguage.ENGLISH to "Next Week ▶", AppLanguage.TAMIL to "அடுத்த வாரம் ▶"),
        "today_tag" to mapOf(AppLanguage.ENGLISH to "Today", AppLanguage.TAMIL to "இன்று"),

        // Days
        "day_mon" to mapOf(AppLanguage.ENGLISH to "Mon", AppLanguage.TAMIL to "திங்கள்"),
        "day_tue" to mapOf(AppLanguage.ENGLISH to "Tue", AppLanguage.TAMIL to "செவ்வாய்"),
        "day_wed" to mapOf(AppLanguage.ENGLISH to "Wed", AppLanguage.TAMIL to "புதன்"),
        "day_thu" to mapOf(AppLanguage.ENGLISH to "Thu", AppLanguage.TAMIL to "வியாழன்"),
        "day_fri" to mapOf(AppLanguage.ENGLISH to "Fri", AppLanguage.TAMIL to "வெள்ளி"),
        "day_sat" to mapOf(AppLanguage.ENGLISH to "Sat", AppLanguage.TAMIL to "சனி"),
        "day_sun" to mapOf(AppLanguage.ENGLISH to "Sun", AppLanguage.TAMIL to "ஞாயிறு"),

        "day_full_Monday" to mapOf(AppLanguage.ENGLISH to "Monday", AppLanguage.TAMIL to "திங்கட்கிழமை"),
        "day_full_Tuesday" to mapOf(AppLanguage.ENGLISH to "Tuesday", AppLanguage.TAMIL to "செவ்வாய்க்கிழமை"),
        "day_full_Wednesday" to mapOf(AppLanguage.ENGLISH to "Wednesday", AppLanguage.TAMIL to "புதன்கிழமை"),
        "day_full_Thursday" to mapOf(AppLanguage.ENGLISH to "Thursday", AppLanguage.TAMIL to "வியாழக்கிழமை"),
        "day_full_Friday" to mapOf(AppLanguage.ENGLISH to "Friday", AppLanguage.TAMIL to "வெள்ளிக்கிழமை"),
        "day_full_Saturday" to mapOf(AppLanguage.ENGLISH to "Saturday", AppLanguage.TAMIL to "சனிக்கிழமை"),
        "day_full_Sunday" to mapOf(AppLanguage.ENGLISH to "Sunday", AppLanguage.TAMIL to "ஞாயிற்றுக்கிழமை"),

        // Dialogs & Actions
        "select_language" to mapOf(AppLanguage.ENGLISH to "Select App Language", AppLanguage.TAMIL to "பயன்பாட்டு மொழியைத் தேர்ந்தெடுக்கவும்"),
        "select_theme" to mapOf(AppLanguage.ENGLISH to "Select App Theme", AppLanguage.TAMIL to "பயன்பாட்டு தீமைத் தேர்ந்தெடுக்கவும்"),
        "set_reminder_time" to mapOf(AppLanguage.ENGLISH to "Set Daily Reminder Time", AppLanguage.TAMIL to "தினசரி நினைவூட்டல் நேரத்தை அமைக்கவும்"),
        "permission_required" to mapOf(AppLanguage.ENGLISH to "Notification Permission Required", AppLanguage.TAMIL to "நினைவூட்டல் அனுமதி தேவை"),
        "permission_msg" to mapOf(AppLanguage.ENGLISH to "Please allow notifications so AI Skill Mirror can remind you to practice daily and keep your skill streak alive.", AppLanguage.TAMIL to "தினசரி பயிற்சி செய்து திறனை மேம்படுத்த அறிவிப்பு அனுமதியை வழங்கவும்."),
        "confirm_logout" to mapOf(AppLanguage.ENGLISH to "Are you sure you want to log out?", AppLanguage.TAMIL to "நிச்சயமாக வெளியேற விரும்புகிறீர்களா?"),
        "cancel" to mapOf(AppLanguage.ENGLISH to "Cancel", AppLanguage.TAMIL to "ரத்துசெய்"),
        "save" to mapOf(AppLanguage.ENGLISH to "Save", AppLanguage.TAMIL to "சேமி"),
        "close" to mapOf(AppLanguage.ENGLISH to "Close", AppLanguage.TAMIL to "மூடு"),
        "ok" to mapOf(AppLanguage.ENGLISH to "OK", AppLanguage.TAMIL to "சரி"),

        // Toasts
        "toast_dark_mode_on" to mapOf(AppLanguage.ENGLISH to "Dark mode enabled", AppLanguage.TAMIL to "இருண்ட பயன்முறை இயக்கப்பட்டது"),
        "toast_dark_mode_off" to mapOf(AppLanguage.ENGLISH to "Light mode restored", AppLanguage.TAMIL to "ஒளி பயன்முறை மீட்டமைக்கப்பட்டது"),
        "toast_theme_changed" to mapOf(AppLanguage.ENGLISH to "Theme changed to %s", AppLanguage.TAMIL to "தீம் %s ஆக மாற்றப்பட்டது"),
        "toast_lang_changed" to mapOf(AppLanguage.ENGLISH to "Language changed to %s", AppLanguage.TAMIL to "மொழி %s ஆக மாற்றப்பட்டது"),
        "toast_reminder_enabled" to mapOf(AppLanguage.ENGLISH to "Learning reminders set for %s", AppLanguage.TAMIL to "%s மணிக்கு நினைவூட்டல் அமைக்கப்பட்டது"),
        "toast_reminder_disabled" to mapOf(AppLanguage.ENGLISH to "Learning reminders turned off", AppLanguage.TAMIL to "நினைவூட்டல்கள் அணைக்கப்பட்டன"),
        "toast_data_downloaded" to mapOf(AppLanguage.ENGLISH to "Your learning data has been exported successfully!", AppLanguage.TAMIL to "உங்கள் கற்றல் தரவு வெற்றிகரமாக பதிவிறக்கப்பட்டது!"),
        "toast_setting_updated" to mapOf(AppLanguage.ENGLISH to "Setting updated successfully", AppLanguage.TAMIL to "அமைப்பு புதுப்பிக்கப்பட்டது")
    )

    fun t(key: String, vararg args: Any): String {
        val entry = translations[key]
        val template = entry?.get(currentLanguage)
            ?: entry?.get(AppLanguage.ENGLISH)
            ?: key
        return if (args.isNotEmpty()) {
            try {
                String.format(template, *args)
            } catch (e: Exception) {
                template
            }
        } else {
            template
        }
    }

    fun translateDay(dayShort: String): String {
        val key = when (dayShort.trim()) {
            "Mon", "M" -> "day_mon"
            "Tue", "T" -> "day_tue"
            "Wed", "W" -> "day_wed"
            "Thu", "T" -> "day_thu"
            "Fri", "F" -> "day_fri"
            "Sat", "S" -> "day_sat"
            "Sun", "S" -> "day_sun"
            else -> null
        }
        return if (key != null) t(key) else dayShort
    }

    fun translateFullDay(dayFull: String): String {
        val key = "day_full_" + dayFull.trim()
        val res = t(key)
        return if (res != key) res else dayFull
    }
}
