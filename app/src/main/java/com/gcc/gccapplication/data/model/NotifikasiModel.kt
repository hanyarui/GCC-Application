package com.gcc.gccapplication.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class NotifikasiModel(
    val namaLengkap: String?=null,
    val alamatLengkap: String?=null,
    val noHp: String?=null,
    val totalAmount: String?=null,
    val dusun: String?=null,
    val time: String?=null,
    var isPicked: Boolean,
    val buktiUploadId: String? = null
): Parcelable
