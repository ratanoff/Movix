package ru.ratanov.movix.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Film(
    val id: Int,
    val title: String,
    val posterUrl: String?,
    val streamId: String
) : Parcelable