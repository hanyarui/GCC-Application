package com.gcc.gccapplication.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrashModel(
    var id: String? = null,
    var name: String? = null,
    var description: String? = null,
    var type: String? = null,
    var address: String? = null,
    var photoUrl: String? = null
) : Parcelable
