package com.example.notes.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities=[NotesEntity::class],version=1,exportSchema=false)


abstract class NotesDatabase: RoomDatabase(){

    abstract fun newsDao():NotesDao

    companion object{
        @Volatile
        var INSTANCE:NotesDatabase?=null

        fun getDatabase(context: Context):NotesDatabase{
        val tempInstance=INSTANCE
        if(tempInstance!=null){
            return tempInstance
        }
        synchronized(this){
            val instance= Room.databaseBuilder(
                context.applicationContext,
                NotesDatabase::class.java,
                "user_database"
            ).allowMainThreadQueries()
                .build()
            INSTANCE=instance
            return instance

        }
    }
    }

}
