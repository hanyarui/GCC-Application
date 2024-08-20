package com.gcc.gccapplication.viewModel

import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.activity.viewModels
import com.gcc.gccapplication.data.model.TrashModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class DetailViewModel : ViewModel() {

    private val _trashDetail = MutableLiveData<TrashModel?>()
    val trashDetail: MutableLiveData<TrashModel?> = _trashDetail

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
//    val newDocRef = db.collection("trashbag").document()

    private val trashbagViewModel: TrashbagViewModel by lazy {
        TrashbagViewModel()
    }


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

    fun kumpulSampah(
        trashId: String,
//        trashbagId: String,
        trashAmount: String,
        trashTime: String,
        email: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        trashbagViewModel.addTrashToTrashbag(
//            trashbagId,
            trashId,
            trashAmount,
            trashTime,
            email,
            onSuccess,
            onFailure
        )
    }

    fun angkutSampah(trashId: String, trashAmount: String, trashTime: String, email: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
//        val db = FirebaseFirestore.getInstance()
//
        val angkutData = hashMapOf(
            "trashId" to trashId,
            "amount" to trashAmount,
            "time" to trashTime,
            "email" to email
        )

        db.collection("angkut")
            .add(angkutData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun hapusDokumenTrash(trashId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("trashbag").document(trashId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}
