package com.gcc.gccapplication.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gcc.gccapplication.data.model.AngkutModel
import com.gcc.gccapplication.data.model.HistoryModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class HistoryViewModel : ViewModel() {
    private val _historyData = MutableLiveData<List<HistoryModel>>()
    val historyData: LiveData<List<HistoryModel>> get() = _historyData

    fun fetchTrashData(email: String) {
        val db = FirebaseFirestore.getInstance()
        val buktiCollection = db.collection("buktiSampah")
        val angkutCollection = db.collection("angkut")
        val trashCollection = db.collection("trash")

        // Step 1: Ambil data dari koleksi buktiSampah berdasarkan email
        buktiCollection.whereEqualTo("email", email).get()
            .addOnSuccessListener { buktiDocuments ->
                val historyList = ArrayList<HistoryModel>()
                val fetchTasks = ArrayList<Task<*>>()

                for (buktiDoc in buktiDocuments) {
                    val buktiUploadId = buktiDoc.id
                    val alamat = buktiDoc.getString("alamatLengkap") ?: ""
                    val nama = buktiDoc.getString("namaLengkap") ?: ""
                    val noHp = buktiDoc.getString("noHp") ?: ""
                    val photoUrl = buktiDoc.getString("photoUrl") ?: ""
                    val timestamp = buktiDoc.getTimestamp("timestamp")?.toDate() ?: Date()

                    // Step 2: Ambil data dari koleksi angkut berdasarkan buktiUploadId
                    val trashItemsQuery = angkutCollection.whereEqualTo("buktiUploadId", buktiUploadId).get()
                    fetchTasks.add(trashItemsQuery)

                    trashItemsQuery.addOnSuccessListener { trashItemsDocuments ->
                        val trashItemsList = ArrayList<AngkutModel>()
                        val innerFetchTasks = ArrayList<Task<*>>()
                        var totalAmount = 0.0
                        for (trashItemDoc in trashItemsDocuments) {
                            val trashId = trashItemDoc.getString("trashId") ?: ""

                            // Step 3: Ambil nama trash dari koleksi trash berdasarkan trashId
                            val trashNameQuery = trashCollection.document(trashId).get()
                            innerFetchTasks.add(trashNameQuery)

                            trashNameQuery.addOnSuccessListener { trashDoc ->
                                val trashName = trashDoc.getString("name") ?: ""
                                val photo = trashDoc.getString("photoUrl")
                                val amount = trashItemDoc.getString("amount").toString().toDoubleOrNull() ?: 0.0

                                totalAmount += amount
                                val trashItem = AngkutModel(
                                    name = trashName,
                                    amount = amount,
                                    time = trashItemDoc.getString("time"),
                                    photoUrl = photo,

                                )
                                trashItemsList.add(trashItem)

                                // Tambahkan data ke dalam list HistoryModel
                                if (trashItemsList.size == trashItemsDocuments.size()) {
                                    historyList.add(
                                        HistoryModel(
                                            trashItems = trashItemsList,
                                            name = nama,
                                            alamat = alamat,
                                            telp = noHp,
                                            timeStamp=timestamp,
                                            totalAmount = totalAmount,
                                            photoUrl = photoUrl
                                        )
                                    )
                                    _historyData.value = historyList
                                }

                                // Update LiveData jika semua data sudah diambil
                                if (historyList.size == buktiDocuments.size() && innerFetchTasks.size == trashItemsDocuments.size()) {
                                    historyList.sortByDescending { it.timeStamp}
                                    _historyData.value = historyList
                                    Log.d("HistoryViewModel", "Fetched ${historyList.size} history items")
                                }
                            }.addOnFailureListener { e ->
                                Log.e("HistoryViewModel", "Failed to fetch trash data for $trashId: ${e.message}")
                            }
                        }

                        // Handle jika ada error saat mengambil semua data dari trash
                        Tasks.whenAll(innerFetchTasks).addOnCompleteListener {
                            if (!it.isSuccessful) {
                                Log.e("HistoryViewModel", "Failed to fetch all trash names: ${it.exception?.message}")
                            }
                        }

                    }.addOnFailureListener { e ->
                        Log.e("HistoryViewModel", "Failed to fetch trash items for $buktiUploadId: ${e.message}")
                    }
                }

                // Handle jika ada error saat mengambil semua data dari angkut
                Tasks.whenAll(fetchTasks).addOnCompleteListener {
                    if (!it.isSuccessful) {
                        Log.e("HistoryViewModel", "Failed to fetch all trash items: ${it.exception?.message}")
                    }
                }
            }
            .addOnFailureListener { e ->
                _historyData.value = emptyList()
                Log.e("HistoryViewModel", "Failed to fetch history data: ${e.message}")
            }
    }


}


//package com.gcc.gccapplication.viewModel
//
//import android.util.Log
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.gcc.gccapplication.data.model.AngkutModel
//import com.google.firebase.firestore.FirebaseFirestore
//
//class HistoryViewModel : ViewModel() {
//    private val _angkutData = MutableLiveData<List<AngkutModel>>()
//    val angkutData: LiveData<List<AngkutModel>> get() = _angkutData
//
//    fun fetchTrashData(email: String) {
//        val db = FirebaseFirestore.getInstance()
//        val anngkutCollection = db.collection("angkut")
//        val trashCollection = db.collection("trash")
////        val userCollection = db.collection("trash")
//
//        val trashbagQuery = anngkutCollection   .whereEqualTo("email",email)
//
//        trashbagQuery.get()
//            .addOnSuccessListener { historyDoccuments ->
//                val trashList = ArrayList<AngkutModel>()
//
//                // Process hiistory data
//                for (historyDoc in historyDoccuments) {
//                    val trashbagId = historyDoc.id
//                    val trashId = historyDoc.getString("trashId") ?: ""
//                    val amount = historyDoc.getString("amount") ?: ""
//                    val time = historyDoc.getString("time") ?: ""
//
//                    // Fetch data from trash collection for each trashId
//                    trashCollection.document(trashId).get()
//                        .addOnSuccessListener { trashDoc ->
//                            val name = trashDoc.getString("name") ?: ""
//                            val photoUrl = trashDoc.getString("photoUrl") ?: ""
//
//                            // Add trash data to the list
//                            trashList.add(
//                                AngkutModel(
////                                    trashbagId,
//                                    name,
////                                    trashId,
//                                    time,
//                                    amount,
//                                    photoUrl
//                                )
//                            )
//
//                            // Update UI or data source once all data is fetched
//                            if (trashList.size == historyDoccuments.size()) {
//                                _angkutData.value = trashList
//                                Log.d("TrashbagViewModel", "Fetched ${trashList.size} trash items")
//                            }
//                        }
//                        .addOnFailureListener { e ->
//                            Log.e(
//                                "TrashbagViewModel",
//                                "Failed to fetch trash data for $trashId: ${e.message}"
//                            )
//                        }
//                }
//            }
//            .addOnFailureListener { e ->
//                _angkutData.value = emptyList() // Handle failure by setting empty list
//                Log.e("TrashbagViewModel", "Failed to fetch trashbag data: ${e.message}")
//            }
//    }
//}