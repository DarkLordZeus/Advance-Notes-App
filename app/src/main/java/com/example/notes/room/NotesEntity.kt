package com.example.notes.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

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

    ):Serializable

//recyclebincount 1 == normal saved
//recyclebincount 2 == favourites
//recyclebincount 3 == recylced