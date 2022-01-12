package com.example.notes.fragments

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notes.R
import com.example.notes.Util.Util
import com.example.notes.adapter.Stagerredadapter
import com.example.notes.databinding.FragmentRecycleBinBinding
import com.example.notes.room.RoomViewmodel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RecycleBinFragment : Fragment() {
    private var _binding: FragmentRecycleBinBinding?=null
    private val binding get()=_binding!!
    private val roommodel: RoomViewmodel by viewModels()
    lateinit var notesadapter: Stagerredadapter
    private lateinit var textview: TextView
    private lateinit var toolbar: com.google.android.material.appbar.CollapsingToolbarLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentRecycleBinBinding.inflate(inflater,container,false)
        activity!!.findViewById<View>(R.id.floatingActionButton2).visibility=View.GONE
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setuprecyclerview()
        textview=activity!!.findViewById<View>(R.id.textview) as TextView
        toolbar = activity!!.findViewById<View>(R.id.collapsingtoolbar) as com.google.android.material.appbar.CollapsingToolbarLayout
        toolbar.title="RECYCLE BIN"

        roommodel.readalldeletednotes.observe(viewLifecycleOwner, Observer {list ->
            notesadapter.differ.submitList(list)
            textview.text="${list.size} notes"
        })

        notesadapter.setOnItemClickListener { it->
            val action=
                RecycleBinFragmentDirections.actionRecycleBinFragmentToWritingFragment(
                    it.id,
                    2)
            findNavController().navigate(action)

        }

        Util.deleteinbulkfromrb.observe(viewLifecycleOwner, Observer {
            for (i in 0 until it.size) {
                val currentnote = it[i]
                roommodel.deletenote(currentnote)
            }

        })
    }

    private fun setuprecyclerview() {
        notesadapter= Stagerredadapter(activity, true)
        binding.recyclebinrv.adapter=notesadapter
        binding.recyclebinrv.layoutManager= StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

    }
    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.recyclebinfragmentemptybin, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.emptyreccyclebin -> {

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Empty Bin")
                    .setMessage("Permanently delete all notes?")
                    .setPositiveButton("YES"){ _, _->
                        val list =roommodel.readalldeletednotes.value
                        if (list != null) {
                            for (i in 0 until list.size) {
                                val currentnote = list[i]
                                roommodel.deletenote(currentnote)
                            }
                        }
                    }
                    .setNegativeButton("NO") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setCancelable(true)
                    .show()


                return true
            }


            else -> super.onOptionsItemSelected(item)
        }
    }

}
