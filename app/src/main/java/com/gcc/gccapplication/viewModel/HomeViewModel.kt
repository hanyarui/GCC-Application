package com.gcc.gccapplication.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gcc.gccapplication.data.model.TrashModel
import com.google.firebase.firestore.FirebaseFirestore

class HomeViewModel : ViewModel() {

    private val _trashData = MutableLiveData<List<TrashModel>>()
    val trashData: LiveData<List<TrashModel>> get() = _trashData

    fun fetchTrashData() {
        val db = FirebaseFirestore.getInstance()
        db.collection("trash").get()
            .addOnSuccessListener { documents ->
                val trashList = ArrayList<TrashModel>()
                for (document in documents) {
                    val id = document.id
                    val name = document.getString("name") ?: ""
                    val description = document.getString("description") ?: ""
                    val address = document.getString("address") ?: ""
                    val photoUrl = document.getString("photoUrl") ?: ""
                    trashList.add(TrashModel(id, name, description, address, photoUrl))
                }
                _trashData.value = trashList
            }
            .addOnFailureListener { exception ->
                // Handle failure
                _trashData.value = emptyList()
            }
    }
}

