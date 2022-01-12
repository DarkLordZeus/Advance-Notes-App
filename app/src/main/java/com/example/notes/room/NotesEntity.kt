package com.example.notes.room

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@kotlinx.parcelize.Parcelize
@Entity(tableName = "Notes")
data class NotesEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val notes:String,
    val Title:String,
    val description:String,
    val datentime:String,
    val color:Int,
    val recyclebincount:Int=1,

    ):Parcelable

//recyclebincount 1 == normal saved
//recyclebincount 2 == favourites
//recyclebincount 3 == recylced