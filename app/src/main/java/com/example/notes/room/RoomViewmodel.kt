package com.example.notes.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch

class RoomViewmodel(application: Application):AndroidViewModel(application) {

    val readnotes:LiveData<List<NotesEntity>>
    val readalldeletednotes:LiveData<List<NotesEntity>>
    val readallfavnotes:LiveData<List<NotesEntity>>
    val readtitlesortnotes: LiveData<List<NotesEntity>>
    val readnotesizednote: LiveData<List<NotesEntity>>
    val repository:Roomrepository



    init {
        val notedao=NotesDatabase.getDatabase(application).newsDao()
        repository=Roomrepository(notedao)
        readnotes=repository.readallnotes
        readalldeletednotes=repository.readalldeletednotes
        readallfavnotes=repository.readallfavnotes
        readtitlesortnotes=repository.readtitlesortnotes
        readnotesizednote=repository.readnotesizednote
    }

    fun addnote(note:NotesEntity)
    {
        viewModelScope.launch(Dispatchers.IO + NonCancellable) {
            repository.addnote(note)
        }
    }

    fun deletenote(note:NotesEntity)
    {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletenote(note)
        }
    }

    fun updatenote(note:NotesEntity)
    {
        viewModelScope.launch(Dispatchers.IO + NonCancellable){
            repository.updatenote(note)
        }
    }

    fun latestsavednote():NotesEntity
    {
        return repository.lastsavednote()
    }

    fun readsearchednote(string: String):LiveData<List<NotesEntity>>
    {
        return repository.readsearchednote(string)
    }

    fun notesofthisid(id:Int):NotesEntity
    {
        return repository.notesofthisid(id)
    }
}