package com.group_7.library_management.data.local.preferences

import android.content.Context
import android.content.SharedPreferences

class CheckLogin(context: Context) {
    private val pref: SharedPreferences = context.getSharedPreferences("check_login", Context.MODE_PRIVATE)

    companion object {
        private const val key_is_logged = "is_login"
        private const val key_is_user_id = "user_id"
        private const val key_biometric_enabled = "biometric_enabled"
    }

    fun saveLogin(userId: String) {
        pref.edit().apply {
            putBoolean(key_is_logged, true)
            putString(key_is_user_id, userId)
            apply()
        }
    }

    fun clearLogin() {
        pref.edit().apply {
            remove(key_is_logged)
            remove(key_is_user_id)
            apply()
        }
    }

    fun isLogin(): Boolean {
        return pref.getBoolean(key_is_logged, false)
    }

    fun getUserId(): String? {
        return pref.getString(key_is_user_id, null)
    }

    fun getSavedUserId(): String? {
        return getUserId()
    }

    fun setBiometricEnabled(enabled: Boolean) {
        pref.edit().putBoolean(key_biometric_enabled, enabled).apply()
    }

    fun isBiometricEnabled(): Boolean {
        return pref.getBoolean(key_biometric_enabled, false)
    }
}
