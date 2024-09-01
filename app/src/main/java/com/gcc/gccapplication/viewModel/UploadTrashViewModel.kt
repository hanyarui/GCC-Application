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
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import kotlin.math.log

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



    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo?.isConnected == true
        }
    }





}