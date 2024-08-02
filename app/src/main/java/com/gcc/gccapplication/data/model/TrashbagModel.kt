package com.gcc.gccapplication.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrashbagModel(
    val name: String,
    val amount: Double,
    val photo: Int
) : Parcelable
