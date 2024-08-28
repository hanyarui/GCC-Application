package com.gcc.gccapplication.data.API

import com.gcc.gccapplication.data.model.NotificationRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("sendNotification")
    fun sendNotification(@Body notificationRequest: NotificationRequest): Call<Void>
}
