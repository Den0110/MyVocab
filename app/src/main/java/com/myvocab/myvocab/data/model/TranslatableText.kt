package com.myvocab.myvocab.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TranslatableText(
    val text: String,
    val lang: String
) : Parcelable