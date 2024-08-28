package com.gcc.gccapplication.service

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.gcc.gccapplication.R
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {

    companion object{
        const val CHANNEL_ID = "notification_channel"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    fun sendNotification(message: MyFirebaseMessagingService) {}

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if(message.notification != null){
//            startNotification(message.notification?.title,message.notification?.body)
            Log.d("FCM__Pesan",""+message.notification?.title)
            Log.d("FCM__Pesan",""+message.notification?.body)
            startNotification(message.notification?.title, message.notification?.body)
        }
    }

    private fun startNotification(title: String?, message: String?){
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_gcc)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, builder.build())

    }



}