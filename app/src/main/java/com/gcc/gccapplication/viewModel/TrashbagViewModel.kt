package com.gcc.gccapplication.viewModel

import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gcc.gccapplication.data.model.TrashModel
import com.gcc.gccapplication.data.model.TrashbagModel
import com.google.firebase.firestore.FirebaseFirestore
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
                _trashData.value = emptyList() // Handle failure by setting empty list
                Log.e("TrashbagViewModel", "Failed to fetch trashbag data: ${e.message}")
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

}

//        fun getTrashIdByCondition(conditionField: String, conditionValue: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
//            db.collection("trash")
//                .whereEqualTo(conditionField, conditionValue)
//                .get()
//                .addOnSuccessListener { documents ->
//                    if (!documents.isEmpty) {
//                        val trashId = documents.documents[0].id  // Ambil ID dokumen pertama yang cocok
//                        onSuccess(trashId)
//                    } else {
//                        onFailure(Exception("Document not found"))
//                    }
//                }
//                .addOnFailureListener { e ->
//                    onFailure(e)
//                }
//        }
//        db.collection("trashbag")
//            .add(trashbagData)
//            .addOnSuccessListener {
//                Log.d("TrashbagViewModel", "Trash successfully added to trashbag collection")
//                onSuccess()
//            }
//            .addOnFailureListener { e ->
//                Log.e("TrashbagViewModel", "Failed to add trash to trashbag: ${e.message}")
//                onFailure(e)
//            }
//    }
//}