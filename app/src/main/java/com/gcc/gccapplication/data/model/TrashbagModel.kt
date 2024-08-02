package com.gcc.gccapplication.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrashbagModel(
    val id: String,
    val trashId: String,
    val amount: Double,
) : Parcelable
