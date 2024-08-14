package com.gcc.gccapplication.data.local

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "userPreferences"
        private const val EMAIL_KEY = "email"
        private const val NAME_KEY = "fullName"
        private const val ROLE_KEY = "role"  // Tambahkan konstanta untuk role
        private const val ADDRESS_KEY = "address"
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
        return prefs.getString(NAME_KEY, "Unknown")
    }

    // Tambahkan metode untuk menyimpan dan mengambil role
    fun saveRole(role: String) {
        with(prefs.edit()) {
            putString(ROLE_KEY, role)
            apply()
        }
    }

    fun getRole(): String? {
        return prefs.getString(ROLE_KEY, null)
    }

    fun saveAddress(address: String) {
        with(prefs.edit()) {
            putString(ADDRESS_KEY, address)
            apply()
        }
    }

    fun getAddress(): String? {
        return prefs.getString(ADDRESS_KEY, null)
    }

    fun clear() {
        with(prefs.edit()) {
            clear()
            apply()
        }
    }
}
