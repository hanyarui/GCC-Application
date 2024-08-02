package com.gcc.gccapplication.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import java.util.*

class CreateTrashViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    fun saveTrashData(
        trashName: String,
        trashType: String,
        trashDesc: String,
        trashAddress: String,
        imageUri: Uri?,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Buat referensi dokumen baru untuk mendapatkan ID dokumen
                val newDocRef = db.collection("trash").document()
                val trashId = newDocRef.id

                var photoUrl: String? = null

                // Upload image to Firebase Storage
                imageUri?.let { uri ->
                    val ref = storage.reference.child("ImgTrash/${UUID.randomUUID()}.jpg")
                    val uploadTask = ref.putFile(uri).await()
                    photoUrl = ref.downloadUrl.await().toString()
                }

                // Simpan data ke Firestore menggunakan ID dokumen
                val trashData = mapOf(
                    "id" to trashId, // Simpan ID dokumen sebagai ID sampah
                    "name" to trashName,
                    "type" to trashType,
                    "description" to trashDesc,
                    "address" to trashAddress,
                    "photoUrl" to photoUrl
                )
                newDocRef.set(trashData).await()

                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }
}

