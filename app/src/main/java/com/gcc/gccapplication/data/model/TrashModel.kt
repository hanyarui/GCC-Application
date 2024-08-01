package com.gcc.gccapplication.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrashModel(
    val name: String,
    val description: String,
    val photo: Int
) : Parcelable
