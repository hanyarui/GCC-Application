package com.gcc.gccapplication.viewModel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gcc.gccapplication.data.model.TrashbagModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TrashbagViewModel : ViewModel() {

    private val _trashData = MutableLiveData<List<TrashbagModel>>()
    val trashData: LiveData<List<TrashbagModel>> get() = _trashData

    fun fetchTrashData(userId: String) {
        val db = FirebaseFirestore.getInstance()
        val trashbagCollection = db.collection("trashbag")
        val trashCollection = db.collection("trash")
        val userCollection = db.collection("trash")

        val trashbagQuery = trashbagCollection.whereEqualTo("email", userId)
//            if (isAdmin) {
//            trashbagCollection // Admin sees all trash data
//        } else {
//            trashbagCollection.whereEqualTo("userId", userId) // Regular users see only their own trash data
//        }

        trashbagQuery.get()
            .addOnSuccessListener { trashbagDocuments ->
                val trashList = ArrayList<TrashbagModel>()

                // Process trashbag data
                for (trashbagDoc in trashbagDocuments) {
                    val trashbagId = trashbagDoc.id
                    val trashId = trashbagDoc.getString("trashId") ?: ""
                    val amount = trashbagDoc.getString("jumlah") ?: ""

                    // Fetch data from trash collection for each trashId
                    trashCollection.document(trashId).get()
                        .addOnSuccessListener { trashDoc ->
                            val name = trashDoc.getString("name") ?: ""
                            val photoUrl = trashDoc.getString("photoUrl") ?: ""

                            // Add trash data to the list
                            trashList.add(
                                TrashbagModel(
                                    trashbagId,
                                    name,
                                    trashId,
                                    amount,
                                    photoUrl
                                )
                            )

                            // Update UI or data source once all data is fetched
                            if (trashList.size == trashbagDocuments.size()) {
                                _trashData.value = trashList
                                Log.d("HistoryViewModel", "Fetched ${trashList.size} trash items")
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e(
                                "HisstoryViewModel",
                                "Failed to fetch trash data for $trashId: ${e.message}"
                            )
                        }
                }
            }
            .addOnFailureListener { e ->
                _trashData.value = emptyList() // Handle failure by setting empty list
                Log.e("HistoryViewModel", "Failed to fetch trashbag data: ${e.message}")
            }
    }


    // Fungsi untuk menambahkan data ke koleksi trashbag
    val db = FirebaseFirestore.getInstance()
    fun addTrashToTrashbag(
//        trashbagId: String,
        trashId: String,
        trashAmount: String,
        trashTime: String,
        email: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val newDocRef = db.collection("trashbag").document()
                val trashbagId = newDocRef.id

                val trashbagData = hashMapOf(
                    "trashbagId" to trashbagId,
                    "trashId" to trashId,
                    "jumlah" to trashAmount,
                    "waktu" to trashTime,
                    "email" to email,
                )
                newDocRef.set(trashbagData).await()
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }

        }
    }

    fun resetTrashbag(email: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val trashbagCollection = db.collection("trashbag")

        // Query untuk mengambil semua dokumen dari koleksi trashbag yang memiliki email yang sama dengan userId
        trashbagCollection.whereEqualTo("email", email).get()
            .addOnSuccessListener { querySnapshot ->
                // Menghapus semua dokumen yang ditemukan
                val batch = db.batch()
                for (document in querySnapshot.documents) {
                    batch.delete(document.reference)
                }

                batch.commit()
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        onFailure(e)
                    }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

//    fun fetchBuktiUploadIdByEmail(
//        email: String,
//        onSuccess: (String) -> Unit,
//        onFailure: (Exception) -> Unit
//    ) {
//        val db = FirebaseFirestore.getInstance()
//        val buktiUploadCollection = db.collection("buktiSampah")
//
//        // Query untuk mengambil dokumen berdasarkan email
//        buktiUploadCollection.whereEqualTo("email", email).get()
//            .addOnSuccessListener { querySnapshot ->
//                if (!querySnapshot.isEmpty) {
//                    // Asumsikan bahwa hanya ada satu dokumen yang sesuai dengan email ini
//                    val document = querySnapshot.documents[0]
//                    val buktiUploadId = document.id
//
//                    // Panggil onSuccess dengan buktiUploadId yang ditemukan
//                    onSuccess(buktiUploadId)
//                } else {
//                    // Jika tidak ada dokumen yang ditemukan
//                    onFailure(Exception("No document found for email: $email"))
//                }
//            }
//            .addOnFailureListener { exception ->
//                // Panggil onFailure jika terjadi kesalahan
//                onFailure(exception)
//            }
//    }

    private suspend fun fetchLatestBuktiUploadIdByTimestamp(): String {
        val db = FirebaseFirestore.getInstance()
        val buktiUploadCollection = db.collection("buktiSampah")

        // Query untuk mengambil dokumen dan urutkan berdasarkan timestamp terbaru
        val querySnapshot = buktiUploadCollection
            .orderBy("timestamp", Query.Direction.DESCENDING) // Urutkan berdasarkan field timestamp
            .limit(1) // Ambil hanya satu dokumen terbaru
            .get()
            .await()

        return if (!querySnapshot.isEmpty) {
            querySnapshot.documents[0].id // Ambil ID dokumen terbaru
        } else {
            throw Exception("No document found")
        }
    }



    fun angkutSampahBatch(
        trashList: List<Map<String, String?>>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            val batch = db.batch()

            try {
                for (trash in trashList) {
                    val buktiUploadId = fetchLatestBuktiUploadIdByTimestamp()

                    // Gabungkan buktiUploadId ke dalam data angkut
                    val angkutData = hashMapOf(
                        "trashId" to trash["trashId"],
                        "amount" to trash["amount"],
                        "time" to trash["time"],
                        "email" to trash["email"],
                        "buktiUploadId" to buktiUploadId
                    )

                    val docRef = db.collection("angkut").document()
                    batch.set(docRef, angkutData)
                }

                // Commit angkut data
                batch.commit().await()
                Log.d("AngkutSampahBatch", "Angkut data committed successfully")

                // Buat batch untuk menghapus data dari trashbag
                val deleteBatch = db.batch()
                for (trashItem in trashList) {
                    val trashbagId = trashItem["trashbagId"]
                    if (trashbagId != null) {
                        val trashDocRef = db.collection("trashbag").document(trashbagId)
                        deleteBatch.delete(trashDocRef)
                    } else {
                        Log.e("AngkutSampahBatch", "Trashbag ID is null for item: $trashItem")
                    }
                }

                // Commit delete batch
                deleteBatch.commit().await()
                Log.d("AngkutSampahBatch", "Trashbag data deleted successfully")
                onSuccess()
            } catch (e: Exception) {
                Log.e("AngkutSampahBatch", "Failed to process batch", e)
                onFailure(e)
            }
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