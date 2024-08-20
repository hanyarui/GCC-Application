package com.gcc.gccapplication.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gcc.gccapplication.data.model.AngkutModel
import com.google.firebase.firestore.FirebaseFirestore

class HistoryViewModel : ViewModel() {
    private val _angkutData = MutableLiveData<List<AngkutModel>>()
    val angkutData: LiveData<List<AngkutModel>> get() = _angkutData

    fun fetchTrashData(email: String) {
        val db = FirebaseFirestore.getInstance()
        val anngkutCollection = db.collection("angkut")
        val trashCollection = db.collection("trash")
//        val userCollection = db.collection("trash")

        val trashbagQuery = anngkutCollection   .whereEqualTo("email",email)

        trashbagQuery.get()
            .addOnSuccessListener { historyDoccuments ->
                val trashList = ArrayList<AngkutModel>()

                // Process hiistory data
                for (historyDoc in historyDoccuments) {
                    val trashbagId = historyDoc.id
                    val trashId = historyDoc.getString("trashId") ?: ""
                    val amount = historyDoc.getString("amount") ?: ""
                    val time = historyDoc.getString("time") ?: ""

                    // Fetch data from trash collection for each trashId
                    trashCollection.document(trashId).get()
                        .addOnSuccessListener { trashDoc ->
                            val name = trashDoc.getString("name") ?: ""
                            val photoUrl = trashDoc.getString("photoUrl") ?: ""

                            // Add trash data to the list
                            trashList.add(
                                AngkutModel(
//                                    trashbagId,
                                    name,
//                                    trashId,
                                    time,
                                    amount,
                                    photoUrl
                                )
                            )

                            // Update UI or data source once all data is fetched
                            if (trashList.size == historyDoccuments.size()) {
                                _angkutData.value = trashList
                                Log.d("TrashbagViewModel", "Fetched ${trashList.size} trash items")
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e(
                                "TrashbagViewModel",
                                "Failed to fetch trash data for $trashId: ${e.message}"
                            )
                        }
                }
            }
            .addOnFailureListener { e ->
                _angkutData.value = emptyList() // Handle failure by setting empty list
                Log.e("TrashbagViewModel", "Failed to fetch trashbag data: ${e.message}")
            }
    }
}