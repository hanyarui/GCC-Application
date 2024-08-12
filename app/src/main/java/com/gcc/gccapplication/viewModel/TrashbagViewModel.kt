package com.gcc.gccapplication.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gcc.gccapplication.data.model.TrashModel
import com.gcc.gccapplication.data.model.TrashbagModel
import com.google.firebase.firestore.FirebaseFirestore

class TrashbagViewModel : ViewModel(){

    private val _trashData = MutableLiveData<List<TrashbagModel>>()
    val trashData: LiveData<List<TrashbagModel>> get() = _trashData

    fun fetchTrashData(userAddress: String, userRole: String) {
        val db = FirebaseFirestore.getInstance()
        val trashCollection = db.collection("trash")

        val query = if (userRole == "admin") {
            trashCollection // Admin sees all trash data
        } else {
            Log.d("TrashbagViewModel", "Fetching trash data for address: $userAddress")
            trashCollection.whereEqualTo("address", userAddress) // Other users see only their address-specific trash data
        }

        query.get()
            .addOnSuccessListener { documents ->
                val trashList = ArrayList<TrashbagModel>()
                for (document in documents) {
//                    val address = document.getString("address") ?: ""
                    val id = document.id
                    val name = document.getString("name") ?: ""
//                    val trashId = document.getString("trashId") ?: ""
                    val photoUrl = document.getString("photoUrl") ?: ""
                    val amount : Double ? = 10.0
                    trashList.add(TrashbagModel(id, name, "ini ID Trash",amount, photoUrl))
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