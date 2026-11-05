package com.simats.aiskillmirror.data.network

import android.content.Context
import android.content.SharedPreferences

object UserSession {
    private const val PREFS_NAME = "skill_mirror_user_prefs"
    private const val KEY_USER_ID = "current_user_id"
    private const val KEY_USER_NAME = "current_user_name"
    private const val KEY_USER_EMAIL = "current_user_email"
    private const val KEY_PROFILE_PIC_PREFIX = "profile_picture_user_"

    var userId: Int = 1
    var userName: String = "Test Student"
    var userEmail: String = "teststudent@example.com"
    var profilePictureUrl: String = ""

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        userId = prefs.getInt(KEY_USER_ID, 1)
        userName = prefs.getString(KEY_USER_NAME, "Test Student") ?: "Test Student"
        userEmail = prefs.getString(KEY_USER_EMAIL, "teststudent@example.com") ?: "teststudent@example.com"
        profilePictureUrl = prefs.getString("$KEY_PROFILE_PIC_PREFIX$userId", "") ?: ""
    }

    fun update(id: Int, name: String, email: String, pictureUrl: String = "") {
        userId = id
        userName = name
        userEmail = email
        if (pictureUrl.isNotBlank()) {
            profilePictureUrl = pictureUrl
        }
    }

    fun updateWithContext(context: Context, id: Int, name: String, email: String, pictureUrl: String = "") {
        update(id, name, email, pictureUrl)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
            .putInt(KEY_USER_ID, id)
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_EMAIL, email)
        
        if (pictureUrl.isNotBlank()) {
            editor.putString("$KEY_PROFILE_PIC_PREFIX$id", pictureUrl)
        }
        editor.apply()
    }

    fun setProfilePicture(context: Context?, url: String) {
        profilePictureUrl = url
        if (context != null) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putString("$KEY_PROFILE_PIC_PREFIX$userId", url)
                .apply()
        }
    }

    fun getPersistedProfilePicture(context: Context, uid: Int = userId): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString("$KEY_PROFILE_PIC_PREFIX$uid", "") ?: ""
    }

    fun clear(context: Context?) {
        userId = 1
        userName = "Test Student"
        userEmail = "teststudent@example.com"
        profilePictureUrl = ""
        context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.edit()?.clear()?.apply()
    }
}
