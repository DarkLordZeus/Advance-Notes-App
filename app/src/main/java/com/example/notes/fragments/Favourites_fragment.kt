package com.example.notes.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notes.R
import com.example.notes.Util.Util
import com.example.notes.adapter.Stagerredadapter
import com.example.notes.databinding.FragmentFavouritesFragmentBinding
import com.example.notes.room.NotesEntity
import com.example.notes.room.RoomViewmodel

class Favourites_fragment : Fragment() {
    private var _binding: FragmentFavouritesFragmentBinding?=null
    private val binding get()=_binding!!
    private val roommodel: RoomViewmodel by viewModels()
    private lateinit var collapsingtoolbar: com.google.android.material.appbar.CollapsingToolbarLayout
    private lateinit var textview: TextView
    lateinit var notesadapter: Stagerredadapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentFavouritesFragmentBinding.inflate(inflater,container,false)
        setHasOptionsMenu(true)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setuprecyclerview()
        activity!!.findViewById<View>(R.id.floatingActionButton2).visibility=View.GONE
        textview=activity!!.findViewById<View>(R.id.textview) as TextView
        collapsingtoolbar = activity!!.findViewById<View>(R.id.collapsingtoolbar) as com.google.android.material.appbar.CollapsingToolbarLayout
        collapsingtoolbar.title="FAVOURITES"
        roommodel.readallfavnotes.observe(viewLifecycleOwner, Observer {list ->
            notesadapter.differ.submitList(list)
            textview.text="${list.size} notes"
        })

        notesadapter.setOnItemClickListener { it->
            val action=
                com.example.notes.fragments.Favourites_fragmentDirections.actionFavouritesFragmentToWritingFragment(
                    it.id,
                    1)
            findNavController().navigate(action)
        }

        Util.deleteinbulk.observe(viewLifecycleOwner, Observer {
            for (i in 0 until it.size) {
                val currentnote = it[i]
                val note = NotesEntity(currentnote.id,
                    currentnote.notes,
                    currentnote.Title,
                    currentnote.description,
                    currentnote.datentime,
                    currentnote.color,
                    0)

                roommodel.updatenote(note)
            }
        })
    }

    private fun setuprecyclerview() {
        notesadapter= Stagerredadapter(activity, false)
        binding.favrecyclerview.adapter=notesadapter
        binding.favrecyclerview.layoutManager= StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }


}
