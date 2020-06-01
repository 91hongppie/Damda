package com.example.damda.navigation.model

import android.os.Parcel
import android.os.Parcelable

class Mission(var id: Int, var user: Int, var title: String, var status: Int, var point: Int, var prize: Int, var period: Int) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(user)
        parcel.writeString(title)!!
        parcel.writeInt(status)
        parcel.writeInt(point)
        parcel.writeInt(prize)
        parcel.writeInt(period)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Album> {
        override fun createFromParcel(parcel: Parcel): Album {
            return Album(parcel)
        }

        override fun newArray(size: Int): Array<Album?> {
            return arrayOfNulls(size)
        }
    }
}