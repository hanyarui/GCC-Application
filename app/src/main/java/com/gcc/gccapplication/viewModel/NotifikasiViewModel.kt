package com.gcc.gccapplication.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gcc.gccapplication.data.model.AngkutModel
import com.gcc.gccapplication.data.model.HistoryModel
import com.gcc.gccapplication.data.model.NotifikasiModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class NotifikasiViewModel : ViewModel() {
    private val _notifikasiData = MutableLiveData<List<NotifikasiModel>>()
    val notifikasiData: LiveData<List<NotifikasiModel>> get() = _notifikasiData

    private val db = FirebaseFirestore.getInstance()

    fun fetchAllTrashData() {
        val buktiCollection = db.collection("buktiSampah")
        val angkutCollection = db.collection("angkut")
        val userCollection = db.collection("user")

        buktiCollection.get()
            .addOnSuccessListener { buktiDocuments ->
                Log.d("NotifikasiViewModel", "buktiDocuments size: ${buktiDocuments.size()}")
                val notifikasiList = ArrayList<NotifikasiModel>()
                val fetchTasks = ArrayList<Task<*>>()

                for (buktiDoc in buktiDocuments) {
                    val buktiUploadId = buktiDoc.id
                    val namaLengkap = buktiDoc.getString("namaLengkap") ?: ""
                    val alamatLengkap = buktiDoc.getString("alamatLengkap") ?: ""
                    val noHp = buktiDoc.getString("noHp") ?: ""
                    val email = buktiDoc.getString("email") ?: ""
                    val isPicked = buktiDoc.getBoolean("isPicked") ?: false
                    val timestamp = buktiDoc.getTimestamp("timestamp")?.toDate() ?: Date()

                    val trashItemsQuery = angkutCollection.whereEqualTo("buktiUploadId", buktiUploadId).get()
                    fetchTasks.add(trashItemsQuery)

                    trashItemsQuery.addOnSuccessListener { trashItemsDocuments ->
                        var totalAmount = 0.0

                        for (trashItemDoc in trashItemsDocuments) {
                            val amount = trashItemDoc.getString("amount")?.toDoubleOrNull() ?: 0.0
                            totalAmount += amount
                        }

                        userCollection.whereEqualTo("email", email).get()
                            .addOnSuccessListener { userDocs ->
                                val dusun = userDocs.documents.firstOrNull()?.getString("dusun") ?: ""

                                notifikasiList.add(
                                    NotifikasiModel(
                                        namaLengkap = namaLengkap,
                                        alamatLengkap = alamatLengkap,
                                        noHp = noHp,
                                        totalAmount = totalAmount.toString(),
                                        dusun = dusun,
                                        isPicked = isPicked,
                                        time = timestamp.toString(),
                                        buktiUploadId = buktiUploadId
                                    )
                                )

                                if (notifikasiList.size == buktiDocuments.size()) {
                                    notifikasiList.sortByDescending { it.time }
                                    _notifikasiData.value = notifikasiList
                                    Log.d("NotifikasiViewModel", "Fetched ${notifikasiList.size} notifikasi items")
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("NotifikasiViewModel", "Failed to fetch user data for email $email: ${e.message}")
                            }

                    }.addOnFailureListener { e ->
                        Log.e("NotifikasiViewModel", "Failed to fetch trash items for $buktiUploadId: ${e.message}")
                    }
                }

                Tasks.whenAll(fetchTasks).addOnCompleteListener {
                    if (!it.isSuccessful) {
                        Log.e("NotifikasiViewModel", "Failed to fetch all trash items: ${it.exception?.message}")
                    }
                }
            }
            .addOnFailureListener { e ->
                _notifikasiData.value = emptyList()
                Log.e("NotifikasiViewModel", "Failed to fetch notifikasi data: ${e.message}")
            }
    }

    // New function to update the isPicked status
    fun updatePickedStatus(buktiUploadId: String, isPicked: Boolean, callback: (Boolean) -> Unit) {
        val buktiCollection = db.collection("buktiSampah")

        // Mendapatkan referensi dokumen dengan ID dokumen yang diberikan
        val docRef = buktiCollection.document(buktiUploadId)

        // Memperbarui field isPicked pada dokumen tersebut
        docRef.update("isPicked", isPicked)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { e ->
                callback(false)
                Log.d("ItemNotifViewmodel", "Update status for ${buktiUploadId} failed: ${e.message}")
            }
    }
}