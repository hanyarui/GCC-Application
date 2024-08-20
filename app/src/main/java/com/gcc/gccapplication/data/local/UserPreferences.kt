package com.gcc.gccapplication.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class UserPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "userPreferences"
        private const val EMAIL_KEY = "email"
        private const val NAME_KEY = "fullName"
        private const val ROLE_KEY = "role"  // Tambahkan konstanta untuk role
        private const val ADDRESS_KEY = "address"
        private const val UID_KEY = "uid"
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

    fun firebaseCurrrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    fun firebaseSignOut(){
        FirebaseAuth.getInstance().signOut()
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
        // Debugging line to check if preferences are actually cleared
        Log.d("Preferences", "All preferences cleared: ${prefs.all}")
    }


    fun getUid(): String? {
        return prefs.getString(UID_KEY, null)
    }
    // Fungsi untuk mendapatkan semua data user dalam satu objek User
    fun getUserData(): User? {
        val email = getEmail()
        val fullName = getFullName()
        val role = getRole()
        val address = getAddress()
        val uid = getUid().toString()

        return if (email != null && fullName != null && role != null && address != null) {
            User(email, fullName, role, address,uid)
        } else {
            null
        }
    }
}

data class User(
    val email: String,
    val fullName: String,
    val role: String,
    val address: String,
    val Uid: String
)
