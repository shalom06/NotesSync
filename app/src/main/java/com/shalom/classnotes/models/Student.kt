package com.shalom.classnotes.models

import android.os.Parcel
import android.os.Parcelable

class Student() : Parcelable {


    var id: String = ""
    var name: String = ""
    var notes: List<Note> = listOf()


    constructor(parcel: Parcel) : this() {
        id = parcel.readString().toString()
        name = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Student> {
        override fun createFromParcel(parcel: Parcel): Student {
            return Student(parcel)
        }

        override fun newArray(size: Int): Array<Student?> {
            return arrayOfNulls(size)
        }

        const val NOTES = "NOTES"
    }
}