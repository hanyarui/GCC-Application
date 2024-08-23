package com.gcc.gccapplication.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class UploadTrashViewModel: ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    fun saveUploadData(
        userFulllName: String,
        userAddress: String,
        phoneNumb: String,
        email: String,
        imageUri: Uri?,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ){
        viewModelScope.launch {
            try {
                val newDocRef = db.collection("buktiSampah").document()
                val uploadId = newDocRef.id

                var  photoUrl: String? = null

                imageUri?.let { uri ->
                    val ref = storage.reference.child("ImgUser/${UUID.randomUUID()}.jpg")
                    val uploadTask = ref.putFile(uri).await()
                    photoUrl = ref.downloadUrl.await().toString()
                }
                val uploadData =  mapOf(
                    "id" to uploadId,
                    "namaLengkap" to userFulllName,
                    "noHp" to phoneNumb,
                    "alamatLengkap" to userAddress,
                    "email" to email,
                    "timestamp" to FieldValue.serverTimestamp(),
                    "photoUrl" to photoUrl,
                )
                newDocRef.set(uploadData).await()
                onSuccess()
            }catch (e: Exception){
                onFailure(e)
            }
        }
    }
}