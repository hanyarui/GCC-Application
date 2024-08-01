package com.gcc.gccapplication.data.local

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "userPreferences"
        private const val TOKEN_KEY = "token"
        private const val EMAIL_KEY = "email"
        private const val NAME_KEY = "fullName"
    }

    fun saveToken(token: String) {
        with(prefs.edit()) {
            putString(TOKEN_KEY, token)
            apply()
        }
    }

    fun getToken(): String? {
        return prefs.getString(TOKEN_KEY, null)
    }

    fun saveEmail(email: String) {
        with(prefs.edit()) {
            putString(EMAIL_KEY, email)
            apply()
        }
    }

    fun getEmail(): String? {
        return prefs.getString(EMAIL_KEY, null)
    }

    fun saveFullName(fullName: String) {
        with(prefs.edit()) {
            putString(NAME_KEY, fullName)
            apply()
        }
    }

    fun getFullName(): String? {
        return prefs.getString(NAME_KEY, null)
    }

    fun clear() {
        with(prefs.edit()) {
            clear()
            apply()
        }
    }
}
