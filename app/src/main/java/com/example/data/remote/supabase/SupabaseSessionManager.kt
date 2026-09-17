package com.example.data.remote.supabase

import android.content.Context
import android.content.SharedPreferences

object SupabaseSessionManager {
    private const val PREFS_NAME = "torqfix_supabase_auth_session"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_EXPIRES_AT = "expires_at"

    private var sharedPreferences: SharedPreferences? = null

    @Volatile
    private var cachedAccessToken: String? = null

    @Volatile
    private var cachedUserId: String? = null

    fun init(context: Context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            cachedAccessToken = sharedPreferences?.getString(KEY_ACCESS_TOKEN, null)
            cachedUserId = sharedPreferences?.getString(KEY_USER_ID, null)
        }
    }

    fun saveSession(
        accessToken: String?,
        refreshToken: String?,
        userId: String?,
        email: String?,
        name: String? = null,
        expiresInSeconds: Long? = 3600
    ) {
        cachedAccessToken = accessToken
        cachedUserId = userId

        sharedPreferences?.edit()?.apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_EMAIL, email)
            if (name != null) putString(KEY_USER_NAME, name)
            val expiresAt = System.currentTimeMillis() + ((expiresInSeconds ?: 3600) * 1000)
            putLong(KEY_EXPIRES_AT, expiresAt)
            apply()
        }
    }

    fun getAccessToken(): String? {
        return cachedAccessToken ?: sharedPreferences?.getString(KEY_ACCESS_TOKEN, null)
    }

    fun getUserId(): String? {
        return cachedUserId ?: sharedPreferences?.getString(KEY_USER_ID, null)
    }

    fun getUserEmail(): String? {
        return sharedPreferences?.getString(KEY_USER_EMAIL, null)
    }

    fun getUserName(): String? {
        return sharedPreferences?.getString(KEY_USER_NAME, null)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences?.getString(KEY_REFRESH_TOKEN, null)
    }

    fun isAuthenticated(): Boolean {
        val token = getAccessToken()
        val userId = getUserId()
        return !token.isNullOrBlank() && !userId.isNullOrBlank()
    }

    fun clearSession() {
        cachedAccessToken = null
        cachedUserId = null
        sharedPreferences?.edit()?.clear()?.apply()
    }
}
