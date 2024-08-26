package com.gcc.gccapplication.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gcc.gccapplication.data.model.TrashModel
import com.google.firebase.firestore.FirebaseFirestore

class HomeViewModel : ViewModel() {

    private val _trashData = MutableLiveData<List<TrashModel>>()
    val trashData: LiveData<List<TrashModel>> get() = _trashData

    fun fetchTrashData(userAddress: String, userRole: String) {
        val db = FirebaseFirestore.getInstance()
        val trashCollection = db.collection("trash")

        val query = if (userRole == "admin") {
            trashCollection // Admin sees all trash data
        } else {
            Log.d("HomeViewModel", "Fetching trash data for address: $userAddress")
            trashCollection.whereEqualTo("address", userAddress) // Other users see only their address-specific trash data
        }

        query.get()
            .addOnSuccessListener { documents ->
                val trashList = ArrayList<TrashModel>()
                for (document in documents) {
                    val address = document.getString("address") ?: ""
                    val id = document.id
                    val name = document.getString("name") ?: ""
                    val type = document.getString("type") ?: ""
                    val description = document.getString("description") ?: ""
                    val photoUrl = document.getString("photoUrl") ?: ""
                    trashList.add(TrashModel(id, name, description, type, address, photoUrl))
                }
                _trashData.value = trashList
                Log.d("HomeViewModel", "Fetched ${trashList.size} trash items")
            }
            .addOnFailureListener { e ->
                _trashData.value = emptyList() // Handle failure by setting empty list
                Log.e("HomeViewModel", "Failed to fetch trash data: ${e.message}")
            }
    }
}
