package com.simats.aiskillmirror.data.network

import android.content.Context
import android.content.SharedPreferences

object UserSession {
    private const val PREFS_NAME = "skill_mirror_user_prefs"
    private const val KEY_USER_ID = "current_user_id"
    private const val KEY_USER_NAME = "current_user_name"
    private const val KEY_USER_EMAIL = "current_user_email"
    private const val KEY_PROFILE_PIC_PREFIX = "profile_picture_user_"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"

    private var appContext: Context? = null

    var userId: Int = 1
    var userName: String = "Test Student"
    var userEmail: String = "teststudent@example.com"
    var profilePictureUrl: String = ""
    var isLoggedIn: Boolean = false

    fun init(context: Context) {
        appContext = context.applicationContext
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        isLoggedIn = if (prefs.contains(KEY_IS_LOGGED_IN)) {
            prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        } else {
            prefs.contains(KEY_USER_ID) && prefs.getInt(KEY_USER_ID, 0) > 0
        }
        userId = prefs.getInt(KEY_USER_ID, 1)
        userName = prefs.getString(KEY_USER_NAME, "Test Student") ?: "Test Student"
        userEmail = prefs.getString(KEY_USER_EMAIL, "teststudent@example.com") ?: "teststudent@example.com"
        profilePictureUrl = prefs.getString("$KEY_PROFILE_PIC_PREFIX$userId", "") ?: ""
    }

    fun update(id: Int, name: String, email: String, pictureUrl: String = "") {
        userId = if (id > 0) id else 1
        userName = name
        userEmail = email
        if (pictureUrl.isNotBlank()) {
            profilePictureUrl = pictureUrl
        }
        isLoggedIn = id > 0
        appContext?.let { ctx ->
            val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
                .putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
                .putInt(KEY_USER_ID, id)
                .putString(KEY_USER_NAME, name)
                .putString(KEY_USER_EMAIL, email)
            if (pictureUrl.isNotBlank()) {
                editor.putString("$KEY_PROFILE_PIC_PREFIX$id", pictureUrl)
            }
            editor.apply()
        }
    }

    fun updateWithContext(context: Context, id: Int, name: String, email: String, pictureUrl: String = "") {
        appContext = context.applicationContext
        update(id, name, email, pictureUrl)
    }

    fun setProfilePicture(context: Context?, url: String) {
        profilePictureUrl = url
        val ctx = context ?: appContext
        if (ctx != null) {
            val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putString("$KEY_PROFILE_PIC_PREFIX$userId", url)
                .apply()
        }
    }

    fun getPersistedProfilePicture(context: Context, uid: Int = userId): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString("$KEY_PROFILE_PIC_PREFIX$uid", "") ?: ""
    }

    private const val KEY_SOUND_EFFECTS = "pref_sound_effects"
    private const val KEY_AUDIO_GUIDANCE = "pref_audio_guidance"
    private const val KEY_REMINDER_TIME = "pref_reminder_time"

    fun getSoundEffectsEnabled(context: Context? = null): Boolean {
        val ctx = context ?: appContext ?: return true
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(KEY_SOUND_EFFECTS, true)
    }

    fun setSoundEffectsEnabled(context: Context? = null, enabled: Boolean) {
        val ctx = context ?: appContext ?: return
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putBoolean(KEY_SOUND_EFFECTS, enabled).apply()
    }

    fun getAudioGuidanceEnabled(context: Context? = null): Boolean {
        val ctx = context ?: appContext ?: return true
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(KEY_AUDIO_GUIDANCE, true)
    }

    fun setAudioGuidanceEnabled(context: Context? = null, enabled: Boolean) {
        val ctx = context ?: appContext ?: return
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putBoolean(KEY_AUDIO_GUIDANCE, enabled).apply()
    }

    fun getReminderTime(context: Context? = null): String {
        val ctx = context ?: appContext ?: return "07:00 PM"
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_REMINDER_TIME, "07:00 PM") ?: "07:00 PM"
    }

    fun setReminderTime(context: Context? = null, time: String) {
        val ctx = context ?: appContext ?: return
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(KEY_REMINDER_TIME, time).apply()
    }

    fun clear(context: Context? = null) {
        userId = 1
        userName = "Test Student"
        userEmail = "teststudent@example.com"
        profilePictureUrl = ""
        isLoggedIn = false
        val ctx = context ?: appContext
        ctx?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.edit()?.clear()?.apply()
    }
}
