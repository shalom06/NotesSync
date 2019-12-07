package com.shalom.classnotes.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = Student.NOTES)
data class Note  (


    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var noteName: String = "",
    var className: String = "",
    var noteDetail: String = "",
    var date: String = ""

    ): Parcelable