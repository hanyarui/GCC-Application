package com.gcc.gccapplication.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.gcc.gccapplication.data.local.UserPreferences
import com.google.firebase.firestore.FirebaseFirestore

class PageViewModel : ViewModel() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun checkAddressData(email: String, onResult: (Boolean) -> Unit) {
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                var addressExists = false
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val address = document.getString("address")
                        if (!address.isNullOrEmpty()) {
                            addressExists = true
                            break
                        }
                    }
                }
                onResult(addressExists)
            }
            .addOnFailureListener {
                onResult(false) // Consider no address in case of failure
            }
    }

    fun saveAddressData(context: Context, email: String, address: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val documentId = documents.documents[0].id
                    db.collection("users").document(documentId)
                        .update("address", address)
                        .addOnSuccessListener {
                            val userPreferences = UserPreferences(context)
                            userPreferences.saveAddress(address)
                            onSuccess()
                        }
                        .addOnFailureListener { exception ->
                            onFailure(exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
