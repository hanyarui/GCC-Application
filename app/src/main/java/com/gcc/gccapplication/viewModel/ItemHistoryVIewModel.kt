//package com.gcc.gccapplication.viewModel
//
//import android.util.Log
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.gcc.gccapplication.data.model.AngkutModel
//import com.gcc.gccapplication.data.model.HistoryModel
//import com.google.firebase.firestore.FirebaseFirestore
//
//class ItemHistoryVIewModel: ViewModel() {
//    private val _historyData = MutableLiveData<List<HistoryModel>>()
//    val historyData: LiveData<List<HistoryModel>> get() = _historyData
//
//    fun fetchTrashData(email: String) {
//        val db = FirebaseFirestore.getInstance()
//        val anngkutCollection = db.collection("buktiSampah")
////        val trashCollection = db.collection("trash")
////        val userCollection = db.collection("trash")
//
//        val trashbagQuery = anngkutCollection   .whereEqualTo("email",email)
//
//        trashbagQuery.get()
//            .addOnSuccessListener { historyDoccuments ->
//                val trashList = ArrayList<HistoryModel>()
//
//                // Process hiistory data
//                for (historyDoc in historyDoccuments) {
//                    val trashbagId = historyDoc.id
//                    val alamat = historyDoc.getString("alamatLengkap") ?: ""
//                    val nama = historyDoc.getString("namaLengkap") ?: ""
//                    val noHp = historyDoc.getString("noHp") ?: ""
//                    val photoUrl = historyDoc.getString("photoUrl")?: ""
//                    trashList.add(
//                        HistoryModel(
//                            nama,
//                            nama,
//                            alamat,
//                            photoUrl
//                        )
//                    )
//
//                    // Update UI or data source once all data is fetched
//                    if (trashList.size == historyDoccuments.size()) {
//                        _historyData.value = trashList
//                        Log.d("TrashbagViewModel", "Fetched ${trashList.size} trash items")
//                    }
//
//                }
//            }
//            .addOnFailureListener { e ->
//                _historyData.value = emptyList() // Handle failure by setting empty list
//                Log.e("TrashbagViewModel", "Failed to fetch trashbag data: ${e.message}")
//            }
//    }
//
//}