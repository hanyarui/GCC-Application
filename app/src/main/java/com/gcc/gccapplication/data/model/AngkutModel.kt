package com.gcc.gccapplication.data.model

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class AngkutModel (
//    val id: String?=null,
    val name : String?=null,
//    val trashId: String?=null,
    val time : String?= null,
    val amount: Double?=null,
    val photoUrl: String ?=null,

) : Parcelable


