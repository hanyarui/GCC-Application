package com.gcc.gccapplication.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class HistoryModel(
    val trashItems: List<AngkutModel> ?= null,
    val name: String?=null,
    val alamat: String?=null,
    val telp: String?=null,
    val timeStamp: Date?=null,
    val totalAmount: Double?=null,
    val photoUrl: String?=null
): Parcelable
