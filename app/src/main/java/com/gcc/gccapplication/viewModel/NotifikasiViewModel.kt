package com.gcc.gccapplication.viewModel

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gcc.gccapplication.data.local.UserPreferences
import com.gcc.gccapplication.data.model.AngkutModel
import com.gcc.gccapplication.data.model.HistoryModel
import com.gcc.gccapplication.data.model.NotifikasiModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import java.util.Date

class NotifikasiViewModel : ViewModel() {
    private val _notifikasiData = MutableLiveData<List<NotifikasiModel>>()
    val notifikasiData: LiveData<List<NotifikasiModel>> get() = _notifikasiData

    private val _hasNewNotif = MutableLiveData<Boolean>()
    val hasNewNotif: LiveData<Boolean> get() = _hasNewNotif

    fun setHasNewNotif(value: Boolean) {
        _hasNewNotif.value = value
    }

    private val db = FirebaseFirestore.getInstance()



    fun fetchAllTrashData() {
        val buktiCollection = db.collection("buktiSampah")
        val angkutCollection = db.collection("angkut")
        val userCollection = db.collection("users")

        // Ambil alamat admin dari currentUser
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
        val currentUserQuery = userCollection.whereEqualTo("email", currentUserEmail).get()

        currentUserQuery.addOnSuccessListener { userDocs ->
            val adminAddress = userDocs.documents.firstOrNull()?.getString("address") ?: ""

            // Ambil semua dokumen dari buktiSampah
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
                        val timestamp = buktiDoc.getTimestamp("timestamp")
                        val time = timestamp?.toDate().toString()
                        val timestampLong = timestamp?.toDate()?.time ?: 0L

                        // Ambil alamat dari user yang sesuai dengan email
                        userCollection.whereEqualTo("email", email).get()
                            .addOnSuccessListener { userDocs ->
                                val userAddress = userDocs.documents.firstOrNull()?.getString("address") ?: ""

                                // Cek jika alamat user sama dengan alamat admin
                                if (userAddress == adminAddress) {
                                    // Ambil data dari collection angkut
                                    val trashItemsQuery = angkutCollection.whereEqualTo("buktiUploadId", buktiUploadId).get()
                                    fetchTasks.add(trashItemsQuery)

                                    trashItemsQuery.addOnSuccessListener { trashItemsDocuments ->
                                        var totalAmount = 0.0

                                        for (trashItemDoc in trashItemsDocuments) {
                                            val amount = trashItemDoc.getString("amount")?.toDoubleOrNull() ?: 0.0
                                            totalAmount += amount
                                        }

                                        notifikasiList.add(
                                            NotifikasiModel(
                                                namaLengkap = namaLengkap,
                                                alamatLengkap = alamatLengkap,
                                                noHp = noHp,
                                                totalAmount = totalAmount.toString(),
                                                dusun = userAddress,
                                                time = time,
                                                timeStampLong = timestampLong,
                                                isPicked = isPicked,
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
                                            Log.e("NotifikasiViewModel", "Failed to fetch trash items for $buktiUploadId: ${e.message}")
                                        }
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("NotifikasiViewModel", "Failed to fetch user data for email $email: ${e.message}")
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
                    Log.e("NotifikasiViewModel", "Failed to fetch buktiSampah data: ${e.message}")
                }
        }
            .addOnFailureListener { e ->
                Log.e("NotifikasiViewModel", "Failed to fetch currentUser data: ${e.message}")
            }
    }

    fun exportDataToPdf(notifikasiList: List<NotifikasiModel>, context: Context) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        // Atur posisi awal teks
        var yPos = 40

        for (notifikasi in notifikasiList) {
            canvas.drawText("Nama Lengkap: ${notifikasi.namaLengkap}", 10f, yPos.toFloat(), paint)
            yPos += 20
            canvas.drawText("Alamat Lengkap: ${notifikasi.alamatLengkap}", 10f, yPos.toFloat(), paint)
            yPos += 20
            canvas.drawText("No HP: ${notifikasi.noHp}", 10f, yPos.toFloat(), paint)
            yPos += 20
            canvas.drawText("Total Amount: ${notifikasi.totalAmount}", 10f, yPos.toFloat(), paint)
            yPos += 20
            canvas.drawText("Dusun: ${notifikasi.dusun}", 10f, yPos.toFloat(), paint)
            yPos += 20
            canvas.drawText("Timestamp: ${notifikasi.time}", 10f, yPos.toFloat(), paint)
            yPos += 40 // Tambahkan spasi antara item
        }

        pdfDocument.finishPage(page)

        // Simpan PDF di penyimpanan perangkat
        val file = File(context.getExternalFilesDir(null), "RekapSampah.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
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

    fun checkForNewNotification(context: Context): LiveData<Boolean> {
        val userPreferences = UserPreferences(context)
        val lastTimestamp = userPreferences.getLastTimestamp() // Ambil timestamp terakhir dari SharedPreferences
        val result = MutableLiveData<Boolean>()

        val buktiCollection = db.collection("buktiSampah")
        buktiCollection
            .whereGreaterThan("timestamp", Date(lastTimestamp)) // Query untuk data baru
            .get()
            .addOnSuccessListener { documents ->
                val hasNewNotification = documents.isEmpty.not()
                result.value = hasNewNotification
                if (hasNewNotification) {
                    // Update timestamp terakhir
                    val newLastTimestamp = documents.documents.maxOfOrNull { it.getTimestamp("timestamp")?.toDate()?.time ?: 0L } ?: lastTimestamp
                    userPreferences.saveLastTimestamp(newLastTimestamp)
                    userPreferences.setHasNewNotif(true) // Simpan status notifikasi baru
                } else {
                    userPreferences.setHasNewNotif(false) // Tidak ada notifikasi baru
                }
            }
            .addOnFailureListener { e ->
                Log.e("NotifikasiViewModel", "Failed to check for new notifications: ${e.message}")
                result.value = false
            }

        return result
    }


//    // Menyimpan timestamp terakhir
//    fun saveLastTimestamp(context: Context,timestamp: Long) {
//        // Simpan timestamp di shared preferences atau database lokal
//
//
//
//    }
}