package com.example.notes.fragments


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notes.R
import com.example.notes.Util
import com.example.notes.Util.Companion.isfav
import com.example.notes.databinding.FragmentWritingBinding
import com.example.notes.room.NotesEntity
import com.example.notes.room.RoomViewmodel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class WritingFragment : Fragment() {

    private lateinit var textToolHeader: EditText
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private var _binding: FragmentWritingBinding? = null
    private val binding get() = _binding!!
    private val roommodel: RoomViewmodel by viewModels()
    private val args: WritingFragmentArgs by navArgs()
    private val rotateopen: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(),
            R.anim.rotate_open)
    }
    private val rotateclose: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(),
            R.anim.rotate_close)
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(),
            R.anim.from_bottom)
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(),
            R.anim.to_bottom)
    }
    var countclick: Int = 0
    var clicked: Boolean = false
    var newnote: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
//        val callback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//
//            }
//        }
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        _binding = FragmentWritingBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        activity!!.findViewById<View>(R.id.floatingActionButton2).visibility=View.GONE
        val appBar = activity!!.findViewById<com.google.android.material.appbar.AppBarLayout>(R.id.appbar)
        appBar.setExpanded(false)
        toolbar = activity!!.findViewById<View>(R.id.toolbar) as androidx.appcompat.widget.Toolbar
        textToolHeader = toolbar.findViewById(R.id.toolbartextedit) as EditText
        textToolHeader.visibility = View.VISIBLE


        if(args.checkargs ==2)
            binding.floatingActionButton.visibility=View.GONE
        if (args.checkargs == 1 || args.checkargs == 2) {
            setupcolor(args.notesEntity!!.color)
            Util.colorbg.value = args.notesEntity!!.color
            textToolHeader.setText(args.notesEntity?.Title)
            binding.edittext1.setText(args.notesEntity?.notes)
            if (args.notesEntity!!.recyclebincount == 2) //is already favourite
            {
                isfav.value = true
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Util.colorbg.observe(viewLifecycleOwner, Observer { color ->
            setupcolor(color)
        })
        isfav.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.floatingActionButtonfav.setImageDrawable(ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.remove_fav))
            } else {
                binding.floatingActionButtonfav.setImageDrawable(ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_baseline_bookmark_24))
            }
        })

        binding.floatingActionButton.setOnClickListener {
            showmorefbbuttons()
        }

        binding.floatingActionButtonsave.setOnClickListener {
            addnote()
            newnote=true
        }
        binding.floatingActionButtonfav.setOnClickListener {
            if (args.checkargs == 1) {
                if (isfav.value == false) {
                    val note = NotesEntity(args.notesEntity!!.id,
                        args.notesEntity!!.notes,
                        args.notesEntity!!.Title,
                        args.notesEntity!!.description,
                        args.notesEntity!!.datentime,
                        args.notesEntity!!.color,
                        2)
                    roommodel.updatenote(note)
                } else {
                    val note = NotesEntity(args.notesEntity!!.id,
                        args.notesEntity!!.notes,
                        args.notesEntity!!.Title,
                        args.notesEntity!!.description,
                        args.notesEntity!!.datentime,
                        args.notesEntity!!.color,
                        1)
                    roommodel.updatenote(note)
                }
                isfav.value = !isfav.value!!

            } else {
                val texttitle: String = textToolHeader.text.toString()
                val notedescription: String = binding.edittext1.text.toString()
                val description: String = descriptionstring(notedescription)
                val datentime = setupdatentime()
                if (textToolHeader.text.toString().isEmpty() && binding.edittext1.text.toString()
                        .isEmpty()
                ) {
                    Toast.makeText(requireContext(), "Note is empty", Toast.LENGTH_SHORT).show()
                } else {
                    if(newnote==false){
                        val note = NotesEntity(0,
                            notedescription,
                            texttitle,
                            description,
                            datentime,
                            Util.colorbg.value!!,2)
                        roommodel.addnote(note)
                        newnote=true
                    }
                    else{
                        val lsnote = roommodel.latestsavednote()
                        val note = NotesEntity(lsnote.id,
                            notedescription,
                            texttitle,
                            description,
                            datentime,
                            Util.colorbg.value!!,if (isfav.value == true) 1 else 2)
                        roommodel.updatenote(note)
                    }


                    isfav.value = !isfav.value!!

                }
            }
            setupfavtoasts()
        }

    }


    private fun showmorefbbuttons() {
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            binding.floatingActionButtonsave.visibility = View.VISIBLE
            binding.floatingActionButtonfav.visibility = View.VISIBLE
        } else {
            binding.floatingActionButtonsave.visibility = View.INVISIBLE
            binding.floatingActionButtonfav.visibility = View.INVISIBLE
        }
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            binding.floatingActionButtonsave.startAnimation(fromBottom)
            binding.floatingActionButtonfav.startAnimation(fromBottom)
            binding.floatingActionButton.startAnimation(rotateopen)
        } else {
            binding.floatingActionButtonsave.startAnimation(toBottom)
            binding.floatingActionButtonfav.startAnimation(toBottom)
            binding.floatingActionButton.startAnimation(rotateclose)
        }

    }


    fun descriptionstring(notedescription: String): String {
        if (notedescription.count() >= 250)
            return notedescription.substring(0, 250) + "..."
        else
            return notedescription
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        if (args.checkargs != 2) {
            inflater.inflate(R.menu.writingfragmentmenu, menu)
        } else {
            inflater.inflate(R.menu.restoreitem_menu, menu)
        }


        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.bgcolor -> {
                val action =
                    WritingFragmentDirections.actionWritingFragmentToBackgroundcolor()
                findNavController().navigate(action)
                return true
            }

            R.id.restorefromtrash -> {
                val note = NotesEntity(args.notesEntity!!.id,
                    args.notesEntity!!.notes,
                    args.notesEntity!!.Title,
                    args.notesEntity!!.description,
                    args.notesEntity!!.datentime,
                    args.notesEntity!!.color,
                    1)
                roommodel.updatenote(note)
                findNavController().popBackStack()
                return true
            }
            R.id.delete -> {
                lifecycleScope.launch {
                    if (args.checkargs == 1) {
                        val note = NotesEntity(args.notesEntity!!.id,
                            args.notesEntity!!.notes,
                            args.notesEntity!!.Title,
                            args.notesEntity!!.description,
                            args.notesEntity!!.datentime,
                            args.notesEntity!!.color,
                            0)
                        roommodel.updatenote(note)
                        Toast.makeText(requireContext(),
                            "Note moved to RecycleBin!",
                            Toast.LENGTH_SHORT).show()
                    } else if (args.checkargs == 2) {
                        roommodel.deletenote(args.notesEntity!!)
                        Toast.makeText(requireContext(), "Note Deleted", Toast.LENGTH_SHORT).show()
                    } else {
                        textToolHeader.text.clear()
                        binding.edittext1.text.clear()
                        delay(10L)
                        Toast.makeText(requireContext(),
                            "Deleted Successfully!",
                            Toast.LENGTH_SHORT).show()
                    }
                    findNavController().popBackStack()

                }
                return true
            }

            R.id.readmode -> {

                toolbar.hideKeyboard()
                countclick++
                if (countclick % 2 == 0) {
                    applyreadmode(true)
                    item.setIcon(R.drawable.ic_baseline_menu_book_24)
                    Toast.makeText(requireContext(), "read mode disabled", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    applyreadmode(false)
                    item.setIcon(R.drawable.ic_baseline_menu_book_clicked_24)
                    Toast.makeText(requireContext(), "read mode enabled", Toast.LENGTH_SHORT).show()
                }
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        popingwritingfrag()

    }

    @SuppressLint("SimpleDateFormat")
    fun setupdatentime(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy")
        val date = sdf.format(Date())
        return date
    }


    fun addnote() {
        if (textToolHeader.text.toString().isEmpty() && binding.edittext1.text.toString()
                .isEmpty()
        ) {

            if (args.checkargs == 1) {
                roommodel.deletenote(args.notesEntity!!)
                Toast.makeText(requireContext(),
                    "Nothing to Save.Note Discarted!",
                    Toast.LENGTH_SHORT).show()
            }

        } else {
            val texttitle: String = textToolHeader.text.toString()
            val notedescription: String = binding.edittext1.text.toString()
            val description: String = descriptionstring(notedescription)
            val datentime = setupdatentime()
            when {
                args.checkargs == 1 -> {
                    val note = NotesEntity(args.notesEntity!!.id,
                        notedescription,
                        texttitle,
                        description,
                        datentime,
                        Util.colorbg.value!!,
                        args.notesEntity!!.recyclebincount)
                    roommodel.updatenote(note)
                }
                newnote -> {
                    val lsnote = roommodel.latestsavednote()
                    Toast.makeText(requireContext(), "${notedescription}", Toast.LENGTH_SHORT).show()
                    val note = NotesEntity(lsnote.id,
                        notedescription,
                        texttitle,
                        description,
                        datentime,
                        Util.colorbg.value!!,lsnote.recyclebincount)
                    roommodel.addnote(note)
                }
                else -> {
                    val note = NotesEntity(0,
                        notedescription,
                        texttitle,
                        description,
                        datentime,
                        Util.colorbg.value!!)
                    roommodel.addnote(note)

                }
            }
        }
    }

    fun setupcolor(color: Int) {
        toolbar.setBackgroundResource(color)
        textToolHeader.setBackgroundResource(color)
        binding.edittext1.setBackgroundResource(color)
        binding.edittext2.setBackgroundResource(color)
        binding.edittext3.setBackgroundResource(color)
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), color)
    }

    private fun applyreadmode(boolean: Boolean) {

        binding.apply {
            edittext1.apply {
                isFocusable = boolean
                isFocusableInTouchMode = boolean
                isCursorVisible = boolean
            }
            edittext2.apply {
                isFocusable = boolean
                isFocusableInTouchMode = boolean
                isCursorVisible = boolean
            }
            edittext3.apply {
                isFocusable = boolean
                isFocusableInTouchMode = boolean
                isCursorVisible = boolean
            }
            textToolHeader.apply {
                isFocusable = boolean
                isFocusableInTouchMode = boolean
                isCursorVisible = boolean
            }
        }
    }

    fun View.hideKeyboard() {
        val inputMethodManager =
            context!!.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(this.windowToken, 0)
    }

    private fun popingwritingfrag() {
        if (args.checkargs != 2) {
            addnote()
        }
        toolbar.setBackgroundResource(R.color.toolbargray)
        activity?.window?.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.toolbargray)
        textToolHeader.text.clear()
        textToolHeader.visibility = View.GONE
        Util.colorbg.value = R.color.bgtransparent
        applyreadmode(true)
        isfav.value = false
        _binding = null
    }


    private fun setupfavtoasts() {
        if (isfav.value == true) {
            Toast.makeText(requireContext(), "Added to favourites", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Removed from favourites", Toast.LENGTH_SHORT).show()
        }
    }
}
