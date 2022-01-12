package com.example.notes.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertnote(note:NotesEntity)

    @Delete
    suspend fun deletenote(note: NotesEntity)

    @Query("SELECT * FROM Notes WHERE recyclebincount = 1 OR recyclebincount = 2 ORDER BY id ASC ")
    fun readallsavednotes():LiveData<List<NotesEntity>>

    @Update
    suspend fun updatenote(note: NotesEntity)

    @Query("SELECT * FROM Notes WHERE recyclebincount = 0 ORDER BY id ASC")
    fun readalldeletednotes():LiveData<List<NotesEntity>>

    @Query("SELECT * FROM Notes WHERE recyclebincount = 2  ORDER BY id ASC")
    fun readallfavnotes():LiveData<List<NotesEntity>>

    @Query("SELECT * FROM Notes ORDER BY id DESC ")
    fun readalatestsavednotes():NotesEntity

    @Query("SELECT * FROM Notes WHERE recyclebincount = 1 OR recyclebincount = 2 ORDER BY LENGTH(Title) ASC")
    fun readalltitle():LiveData<List<NotesEntity>>

    @Query("SELECT * FROM Notes WHERE recyclebincount = 1 OR recyclebincount = 2 ORDER BY LENGTH(notes) ASC")
    fun readallnotesize():LiveData<List<NotesEntity>>

    @Query("SELECT * FROM Notes WHERE (Title LIKE '%' || :string || '%' OR notes LIKE '%' || :string || '%') AND (recyclebincount = 1 OR recyclebincount = 2)  ORDER BY id ASC")
    fun readallsearchnote(string: String):LiveData<List<NotesEntity>>

    @Query("DELETE FROM Notes WHERE id=:ids")
    suspend fun deleteselectednotes(ids:Int)

    @Query("SELECT * FROM Notes WHERE id=:ids")
    fun notesofthisid(ids:Int):NotesEntity
}