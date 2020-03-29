package com.johngoodstadt.memorize.models

import android.os.Parcel
import android.os.Parcelable


data class PieceDetailBottom(val id: Int, var title: String?, var isRed: Boolean? = false) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeValue(isRed)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PieceDetailBottom> {
        override fun createFromParcel(parcel: Parcel): PieceDetailBottom {
            return PieceDetailBottom(parcel)
        }

        override fun newArray(size: Int): Array<PieceDetailBottom?> {
            return arrayOfNulls(size)
        }
    }
}