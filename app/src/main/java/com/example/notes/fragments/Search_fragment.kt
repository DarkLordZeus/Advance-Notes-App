package com.example.notes

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notes.adapter.Stagerredadapter
import com.example.notes.databinding.FragmentSearchFragmentBinding
import com.example.notes.room.RoomViewmodel
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class Search_fragmnet : Fragment() {
    private var _binding: FragmentSearchFragmentBinding?=null
    private val binding get()=_binding!!
    private val roommodel: RoomViewmodel by viewModels()
    private lateinit var textToolHeader: EditText
    private lateinit var textview: TextView
    private lateinit var collapsingtoolbar: com.google.android.material.appbar.CollapsingToolbarLayout

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var notesadapter: Stagerredadapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity!!.findViewById<View>(R.id.floatingActionButton2).visibility=View.GONE
        textview=activity!!.findViewById<View>(R.id.textview) as TextView
        textview.text="SEARCH"
        textview.gravity=Gravity.CENTER
        
        _binding= FragmentSearchFragmentBinding.inflate(inflater,container,false)
        toolbar = activity!!.findViewById<View>(R.id.toolbar) as androidx.appcompat.widget.Toolbar
        textToolHeader = toolbar.findViewById(R.id.toolbartextsearch) as EditText
        textToolHeader.text.clear()
        val appBar = activity!!.findViewById<com.google.android.material.appbar.AppBarLayout>(R.id.appbar)
        appBar.setExpanded(false)

        return binding.root
    }
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setuprecyclerview()
        setHasOptionsMenu(true)
        textToolHeader.visibility=View.VISIBLE

        var job:Job?=null
        textToolHeader.addTextChangedListener {
            job?.cancel()
            job= MainScope().launch {
                delay(200L)
                it?.let { it ->
                    if(it.isNotEmpty()){

                        roommodel.readsearchednote(it.toString()).observeForever(
                            Observer { list->
                                when(list.isEmpty()){
                                     true ->textview.text="no result matched"
                                    else ->
                                        textview.text ="${list.size} results matched"

                                }
                                notesadapter.differ.submitList(list)
                            })
                    }
                    else
                    {
                        textview.text="SEARCH"
                        notesadapter.differ.submitList(emptyList())
                    }
                }
            }
        }

        notesadapter.setOnItemClickListener { it->
            val action=Search_fragmnetDirections.actionSearchFragmnetToWritingFragment(it.id,1)
            findNavController().navigate(action)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        textToolHeader.visibility=View.GONE
        textToolHeader.text.clear()
        _binding=null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.clearsearch, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem):Boolean{
        return when(item.itemId){
            R.id.clearsearch -> {
                textToolHeader.text.clear()
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun setuprecyclerview() {
        notesadapter= Stagerredadapter(activity, false)
        binding.searchrv.adapter=notesadapter
        binding.searchrv.layoutManager= StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

    }
}


