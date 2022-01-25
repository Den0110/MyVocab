package com.myvocab.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class TranslatableText(
    val text: String,
    val lang: String
) : Parcelable, Serializable