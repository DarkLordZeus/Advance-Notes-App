package com.example.notes.adapter

import android.view.*
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.Util.Util
import com.example.notes.databinding.RecyclerviewItemBinding
import com.example.notes.room.NotesEntity


class Stagerredadapter(val activity: FragmentActivity?, val inrecyclebin: Boolean) : RecyclerView.Adapter<Stagerredadapter.ViewHolder>(){
    private var mAreCheckboxesVisible= MutableLiveData<Boolean>(false)
    var isRadioButtonChecked = false
    var actionMode: ActionMode? = null
    private var selected=MutableLiveData<Int>(0)
    var noteslist:MutableList<NotesEntity> = mutableListOf<NotesEntity>()
    inner class ViewHolder(val binding:RecyclerviewItemBinding)
        :RecyclerView.ViewHolder(binding.root){

    }

    companion object DiffCallback: DiffUtil.ItemCallback<NotesEntity>(){
        override fun areItemsTheSame(oldItem:NotesEntity,newItem:NotesEntity):Boolean{
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem:NotesEntity,newItem:NotesEntity):Boolean{
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,DiffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int):Stagerredadapter.ViewHolder{
        val binding = RecyclerviewItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder:ViewHolder,position:Int){
        val note = differ.currentList[position]
        holder.binding.apply{
            titlerv.text=note.Title
            noterv.text=note.description
            daterv.text=note.datentime
            viewofitem.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context,note.color))

        }


        mAreCheckboxesVisible.observeForever(Observer {


            holder.itemView.setOnClickListener{  view->
                if(mAreCheckboxesVisible.value==false)
                {onItemClickListener?.let{it(note)
                }}
                else
                {
                    view.findViewById<CheckBox>(R.id.rvitemselect).isChecked = !view.findViewById<CheckBox>(R.id.rvitemselect).isChecked

                }
            }

            holder.binding.rvitemselect.setOnCheckedChangeListener { button, ischecked ->
                if (ischecked) {
                    noteslist.add(note)
                    selected.value = selected.value?.plus(1)
                }
                else{
                    noteslist.remove(note)
                    selected.value = selected.value?.minus(1)
                }
            }

            if(mAreCheckboxesVisible.value==true){
                if (actionMode == null) if (activity != null) {
                    actionMode = activity.startActionMode(ActionModeCallback())
                }
                holder.binding.rvitemselect.visibility=View.VISIBLE}
            else{
                holder.binding.rvitemselect.isChecked=false
                holder.binding.rvitemselect.visibility=View.GONE
            }
        })
        holder.itemView.setOnLongClickListener {
            mAreCheckboxesVisible.value = true
            it.findViewById<CheckBox>(R.id.rvitemselect).isChecked=true
            it.findViewById<CheckBox>(R.id.rvitemselect).setOnCheckedChangeListener { button, ischecked ->
                if (ischecked) {
                    noteslist.add(note)
                    selected.value = selected.value?.plus(1)
                }
                else{
                    noteslist.remove(note)
                    selected.value = selected.value?.minus(1)
                }
            }
            true
        }

    }

    override fun getItemCount():Int{
        return differ.currentList.size
    }
//hereithelptogetarticlewhenweclicktheitem......
    private var onItemClickListener:((NotesEntity)->Unit)?=null

    fun setOnItemClickListener(listener:(NotesEntity)->Unit){
        onItemClickListener = listener
    }





    inner class ActionModeCallback : ActionMode.Callback {
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.getItemId()) {
                R.id.adapteriddelete -> {
                    if(inrecyclebin)                   //if adapter in rcybinfragment
                    {
                        Util.deleteinbulkfromrb.value=noteslist }
                    else{                              //if in home or fav fragment
                        Util.deleteinbulk.value=noteslist}
                    mAreCheckboxesVisible.value = false
                    actionMode?.finish()

                    return true
                }
            }
            return false
        }

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menu?.clear()
            val inflater = mode?.menuInflater
            inflater?.inflate(R.menu.adapterdeletemenu, menu)
            selected.observeForever(Observer {
                mode?.title="$it SELECTED "
            })

            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menu?.findItem(R.id.adapteriddelete)?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
           // Log.d(TAG, "onDestroyActionMode Called")
            //myAdapter?.selectedIds?.clear()
            //myAdapter?.notifyDataSetChanged()
            mAreCheckboxesVisible.value=false
            actionMode = null
            selected.value=0
        }
    }
   //the item clicked will be chcked not any random
    override fun getItemViewType(position: Int): Int {
        return position
    }



}
