package com.gcc.gccapplication.data.API

import com.gcc.gccapplication.data.model.NotificationRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("sendNotificationUploadTrash")
    fun sendNotification(@Body notificationRequest: NotificationRequest): Call<Void>

//    @GET("getNotifications")
//    fun getNotifications(@Query("role") role: String): Call<NotificationResponse>
}
