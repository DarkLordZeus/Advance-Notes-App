package com.example.notes.room

import androidx.lifecycle.LiveData

class Roomrepository(private val NotesDao:NotesDao) {

    val readallnotes: LiveData<List<NotesEntity>> = NotesDao.readallsavednotes()
    val readalldeletednotes: LiveData<List<NotesEntity>> = NotesDao.readalldeletednotes()
    val readallfavnotes: LiveData<List<NotesEntity>> = NotesDao.readallfavnotes()
    val readtitlesortnotes: LiveData<List<NotesEntity>> = NotesDao.readalltitle()
    val readnotesizednote: LiveData<List<NotesEntity>> = NotesDao.readallnotesize()


    suspend fun addnote(note: NotesEntity)
    {
        NotesDao.insertnote(note)
    }

    suspend fun  deletenote(note:NotesEntity)
    {
        NotesDao.deletenote(note)
    }

    suspend fun updatenote(note: NotesEntity)
    {
        NotesDao.updatenote(note)
    }

    fun lastsavednote():NotesEntity
    {
        return NotesDao.readalatestsavednotes()
    }

    fun readsearchednote(string: String):LiveData<List<NotesEntity>>
    {
        return NotesDao.readallsearchnote(string)
    }

    fun notesofthisid(id:Int):NotesEntity
    {
        return NotesDao.notesofthisid(id)
    }
}