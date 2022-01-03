package com.example.notes.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notes.Dialogsort
import com.example.notes.R
import com.example.notes.Util.Util
import com.example.notes.adapter.Stagerredadapter
import com.example.notes.databinding.FragmentHomeBinding
import com.example.notes.room.NotesEntity
import com.example.notes.room.RoomViewmodel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding?=null
    private val binding get()=_binding!!
    private lateinit var collapsingtoolbar: com.google.android.material.appbar.CollapsingToolbarLayout
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var fab: com.google.android.material.floatingactionbutton.FloatingActionButton
    private lateinit var textview: TextView
     val roommodel: RoomViewmodel by viewModels()
    lateinit var notesadapter: Stagerredadapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding=FragmentHomeBinding.inflate(inflater,container,false)
        setHasOptionsMenu(true)
        toolbar = activity!!.findViewById<View>(R.id.toolbar) as androidx.appcompat.widget.Toolbar
        fab = activity!!.findViewById<View>(R.id.floatingActionButton2) as com.google.android.material.floatingactionbutton.FloatingActionButton
        fab.visibility=View.VISIBLE
        textview=activity!!.findViewById<View>(R.id.textview) as TextView
        collapsingtoolbar = activity!!.findViewById<View>(R.id.collapsingtoolbar) as com.google.android.material.appbar.CollapsingToolbarLayout
        collapsingtoolbar.title="NOTES"
        val sharedPreferences= context?.getSharedPreferences("shared_preference", Context.MODE_PRIVATE)
        Util.listforadapter.value=sharedPreferences?.getInt("listforadapter",2)
                return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setuprecyclerview()
        Util.listforadapter.observe(viewLifecycleOwner, Observer {
            when(it){
                1 -> roommodel.readtitlesortnotes.observe(viewLifecycleOwner, Observer { list->
                    notesadapter.differ.submitList(list)
                    textview.text="${list.size} notes"
                })
                2 -> roommodel.readnotes.observe(viewLifecycleOwner, Observer { list->
                notesadapter.differ.submitList(list)
                    textview.text="${list.size} notes"
                })
                3 ->roommodel.readnotesizednote.observe(viewLifecycleOwner, Observer { list->
                notesadapter.differ.submitList(list)
                    textview.text="${list.size} notes"
                })
                4 ->roommodel.readtitlesortnotes.observe(viewLifecycleOwner, Observer { list->
                    notesadapter.differ.submitList(list.reversed())
                    textview.text="${list.size} notes"
                })
                5 ->roommodel.readnotes.observe(viewLifecycleOwner, Observer { list->
                    notesadapter.differ.submitList(list.reversed())
                    textview.text="${list.size} notes"
                })
                6 ->roommodel.readnotesizednote.observe(viewLifecycleOwner, Observer { list->
                    notesadapter.differ.submitList(list.reversed())
                    textview.text="${list.size} notes"
                })
            }

        })

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

        fab.setOnClickListener {
            val action=
                HomeFragmentDirections.actionHomeFragmentToWritingFragment(null,
                    0)
            findNavController().navigate(action)

        }
        notesadapter.setOnItemClickListener { it->
            val action=
                HomeFragmentDirections.actionHomeFragmentToWritingFragment(it, 1)
            findNavController().navigate(action)
        }

    }

    private fun setuprecyclerview() {
        notesadapter= Stagerredadapter(activity,false)
        binding.recyclerview.adapter=notesadapter
        Util.layoutmanager.observe(viewLifecycleOwner, Observer {
            when(it)
            {
                1-> binding.recyclerview.layoutManager=StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL)
                2-> binding.recyclerview.layoutManager=LinearLayoutManager(requireContext())
                3-> binding.recyclerview.layoutManager=GridLayoutManager(requireContext(),2)
            }
        })


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun onOptionsItemSelected(item: MenuItem):Boolean{
        return when(item.itemId){
            R.id.View -> {
                val popupMenu: PopupMenu = PopupMenu(requireContext(),activity!!.findViewById(R.id.overflowActionButton))
                popupMenu.menuInflater.inflate(R.menu.viewrv_menu,popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                    when(item.itemId) {
                        R.id.staglistrv -> {
                            Util.layoutmanager.value= 1
                        }
                        R.id.Listrv -> {
                            Util.layoutmanager.value= 2
                        }
                        R.id.Grid ->{
                            Util.layoutmanager.value= 3
                        }
                    }
                    true
                })
                popupMenu.show()
                return true
            }
            R.id.search ->{
                val action =
                    HomeFragmentDirections.actionHomeFragmentToSearchFragmnet()
                findNavController().navigate(action)
                return true
            }

            R.id.Sort ->{

                Dialogsort(requireContext()).show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
