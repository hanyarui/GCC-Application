package com.gcc.gccapplication.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrashbagModel(
    val id: String?=null,
    val name : String?=null,
    val trashId: String?=null,
//    val waktu : String?= null,
    val amount: String?=null,
    val photoUrl: String ?=null,

//    val photoUrl: String?=null,
) : Parcelable
