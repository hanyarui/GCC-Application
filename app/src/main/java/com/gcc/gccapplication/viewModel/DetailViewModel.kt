package com.gcc.gccapplication.viewModel

import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.activity.viewModels
import androidx.lifecycle.viewModelScope
import com.gcc.gccapplication.data.model.TrashModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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

    suspend fun fetchLatestBuktiUploadIdByTimestamp(): String? {
        val db = FirebaseFirestore.getInstance()
        val buktiUploadCollection = db.collection("buktiSampah")

        // Query untuk mengambil dokumen dan urutkan berdasarkan timestamp terbaru
        val querySnapshot = buktiUploadCollection
            .orderBy("timestamp", Query.Direction.DESCENDING) // Urutkan berdasarkan field timestamp
            .limit(1) // Ambil hanya satu dokumen terbaru
            .get().await()

        return if (querySnapshot.isEmpty) {
            null
        } else {
            querySnapshot.documents.first().id
        }
    }


    fun angkutSampah(
        trashId: String,
        trashAmount: String,
        trashTime: String,
        email: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Ambil buktiUploadId
                val buktiUploadId = fetchLatestBuktiUploadIdByTimestamp()

                if (buktiUploadId != null) {
                    // Data yang akan dikirimkan
                    val angkutData = hashMapOf(
                        "trashId" to trashId,
                        "amount" to trashAmount,
                        "time" to trashTime,
                        "email" to email,
                        "buktiUploadId" to buktiUploadId
                    )

                    // Tambahkan data ke koleksi 'angkut'
                    db.collection("angkut")
                        .add(angkutData)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            onFailure(e)
                        }
                } else {
                    // Tangani kasus jika buktiUploadId null
                    onFailure(Exception("Bukti Upload ID tidak ditemukan"))
                }
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }




    fun hapusDokumenTrash(
        trashId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
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
