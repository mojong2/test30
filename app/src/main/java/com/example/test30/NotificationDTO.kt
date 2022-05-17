package com.example.test30

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationDTO(
    var name : String? = null,
    var date : String? = null
) : Parcelable