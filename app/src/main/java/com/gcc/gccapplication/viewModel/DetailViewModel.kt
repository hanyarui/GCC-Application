package com.gcc.gccapplication.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gcc.gccapplication.data.model.TrashModel
import com.google.firebase.firestore.FirebaseFirestore

class DetailViewModel : ViewModel() {

    private val _trashDetail = MutableLiveData<TrashModel?>()
    val trashDetail: MutableLiveData<TrashModel?> = _trashDetail

    fun getTrashDetail(id: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("trash")
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val trash = document.toObject(TrashModel::class.java)
                    _trashDetail.value = trash
                }
            }
    }
}
