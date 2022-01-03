package com.example.notes

import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RadioButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notes.room.NotesEntity

class Util {
    companion object{
        var colorbg=MutableLiveData<Int>(R.color.bgtransparent)
        var layoutmanager=MutableLiveData<Int>(1)
        var isfav=MutableLiveData<Boolean>(false)
        var listforadapter = MutableLiveData<Int>()
        var typeofsort:Int=R.id.datesort
        var ascordec:Int=R.id.ascendingsort
        var deleteinbulk=MutableLiveData<List<NotesEntity>>()
        var deleteinbulkfromrb=MutableLiveData<List<NotesEntity>>()
    }
}