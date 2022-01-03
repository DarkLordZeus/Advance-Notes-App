package com.example.notes.Util

import androidx.lifecycle.MutableLiveData
import com.example.notes.R
import com.example.notes.room.NotesEntity

class Util {
    companion object{
        var colorbg=MutableLiveData<Int>(R.color.bgtransparent)
        var layoutmanager=MutableLiveData<Int>(1)
        var isfav=MutableLiveData<Boolean>(false)
        var listforadapter = MutableLiveData<Int>()
        var typeofsort:Int= R.id.datesort
        var ascordec:Int= R.id.ascendingsort
        var deleteinbulk=MutableLiveData<List<NotesEntity>>()
        var deleteinbulkfromrb=MutableLiveData<List<NotesEntity>>()
    }
}