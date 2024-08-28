package com.gcc.gccapplication.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gcc.gccapplication.data.API.ApiService
import com.gcc.gccapplication.data.model.NotificationRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID
import kotlin.math.log

class UploadTrashViewModel: ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private lateinit var apiService: ApiService
    val retrofit = Retrofit.Builder()
        .baseUrl("https://018b-103-65-214-6.ngrok-free.app/") // Gabisa makek http, bisa nya https akalin nya makek ngrok
        .addConverterFactory(GsonConverterFactory.create())
        .build()



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
                val angkutDocRef = db.collection("angkut").document()
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
                    "isPicked" to false,
//                    "total (kg)" to amount,
                )
                newDocRef.set(uploadData).await()


                onSuccess()
            }catch (e: Exception){
                onFailure(e)
            }
        }
    }

    fun sendNotification(userId: String, title: String, body: String) {
        apiService = retrofit.create(ApiService::class.java)
        val notificationRequest = NotificationRequest(userId, title, body)
        apiService.sendNotification(notificationRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Notifikasi berhasil dikirim
                    Log.d("Notification", "Notification sent successfully")
                } else {
                    // Tangani error
                    Log.e("Notification", "Failed to send notification bla bla: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Tangani kegagalan
                Log.e("Notification", "Failed to send notification: ${t.message}")
            }
        })
    }



}