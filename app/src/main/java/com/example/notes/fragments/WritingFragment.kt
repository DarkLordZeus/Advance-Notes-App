package com.example.notes.fragments


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.util.Linkify
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notes.R
import com.example.notes.Util.CreateSaveSharepdf

import com.example.notes.Util.MyMovementMethod
import com.example.notes.Util.PaintView.Companion.pathList

import com.example.notes.Util.Util
import com.example.notes.Util.Util.Companion.isfav

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
    private var statecount:Int=1
    private lateinit var  notebyid:NotesEntity
    companion object{
        var path = Path()
        var paintBrush = Paint()
    }

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
        notebyid=roommodel.notesofthisid(args.notesEntityid)
        activity!!.findViewById<View>(R.id.floatingActionButton2).visibility=View.GONE
        val appBar = activity!!.findViewById<com.google.android.material.appbar.AppBarLayout>(R.id.appbar)
        appBar.setExpanded(false)

        toolbar = activity!!.findViewById<View>(R.id.toolbar) as androidx.appcompat.widget.Toolbar
        textToolHeader = toolbar.findViewById(R.id.toolbartextedit) as EditText
        textToolHeader.visibility = View.VISIBLE


        if(args.checkargs ==2)
            binding.floatingActionButton.visibility=View.GONE
        if (args.checkargs == 1 || args.checkargs == 2) {

            setupcolor(notebyid.color)
            Util.colorbg.value = notebyid.color
            textToolHeader.setText(notebyid.Title)
            binding.edittext1.setText(notebyid.notes)
            if (notebyid.recyclebincount == 2) //is already favourite
            {
                isfav.value = true
            }
        }
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
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
        binding.apply {

            edittext1.linksClickable = true
            edittext1.autoLinkMask = Linkify.WEB_URLS
            edittext1.setMovementMethod(MyMovementMethod.instance) }
        Linkify.addLinks(binding.edittext1, Linkify.WEB_URLS)
        binding.edittext1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                Linkify.addLinks(s, Linkify.WEB_URLS)
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

                    val note = NotesEntity(notebyid.id,
                        notebyid.notes,
                        notebyid.Title,
                        notebyid.description,
                        notebyid.datentime,
                        notebyid.color,
                        2)
                    roommodel.updatenote(note)
                    statecount=2
                } else {
                    val note = NotesEntity(notebyid.id,
                        notebyid.notes,
                        notebyid.Title,
                        notebyid.description,
                        notebyid.datentime,
                        notebyid.color,
                        1)
                    roommodel.updatenote(note)
                    statecount=1
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

                    val lsnote = roommodel.latestsavednote()
                    val note = NotesEntity(lsnote.id,
                        notedescription,
                        texttitle,
                        description,
                        datentime,
                        Util.colorbg.value!!,if (isfav.value == true) 1 else 2)
                        roommodel.updatenote(note)
                    }
                if (isfav.value == true) 2 else 1

                    isfav.value = !isfav.value!!


            }
            setupfavtoasts()
        }
        binding.floatingActionButtondraw.setOnClickListener{
            binding.drawView.visibility=View.VISIBLE


        }

    }

    private fun convertViewToDrawable(view: View): Bitmap {
        val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(spec, spec)
        view.layout(0, 0, 500, 500)
        val b = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight,
            Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        c.translate((-view.scrollX).toFloat(), (-view.scrollY).toFloat())
        view.draw(c)
        return b
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
            binding.floatingActionButtondraw.visibility = View.VISIBLE
        } else {
            binding.floatingActionButtonsave.visibility = View.INVISIBLE
            binding.floatingActionButtonfav.visibility = View.INVISIBLE
            binding.floatingActionButtondraw.visibility = View.INVISIBLE
        }
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            binding.floatingActionButtonsave.startAnimation(fromBottom)
            binding.floatingActionButtonfav.startAnimation(fromBottom)
            binding.floatingActionButtondraw.startAnimation(fromBottom)
            binding.floatingActionButton.startAnimation(rotateopen)
        } else {
            binding.floatingActionButtonsave.startAnimation(toBottom)
            binding.floatingActionButtonfav.startAnimation(toBottom)
            binding.floatingActionButtondraw.startAnimation(toBottom)
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
                val note = NotesEntity(notebyid.id,
                    notebyid.notes,
                    notebyid.Title,
                    notebyid.description,
                    notebyid.datentime,
                    notebyid.color,
                    1)
                roommodel.updatenote(note)
                statecount=0
                findNavController().popBackStack()
                return true
            }
            R.id.delete -> {
                lifecycleScope.launch {
                    if (args.checkargs == 1) {
                        val note = NotesEntity(notebyid.id,
                            notebyid.notes,
                            notebyid.Title,
                            notebyid.description,
                            notebyid.datentime,
                            notebyid.color,
                            0)
                        roommodel.updatenote(note)
                        Toast.makeText(requireContext(),
                            "Note moved to RecycleBin!",
                            Toast.LENGTH_SHORT).show()
                    } else if (args.checkargs == 2) {
                        roommodel.deletenote(notebyid)
                        Toast.makeText(requireContext(), "Note Deleted", Toast.LENGTH_SHORT).show()
                    }
                    else if(newnote){
                        val texttitle: String = textToolHeader.text.toString()
                        val notedescription: String = binding.edittext1.text.toString()
                        val description: String = descriptionstring(notedescription)
                        val datentime = setupdatentime()
                        val lsnote = roommodel.latestsavednote()
                        val note = NotesEntity(lsnote.id,
                            notedescription,
                            texttitle,
                            description,
                            datentime,
                            Util.colorbg.value!!,0)
                        roommodel.updatenote(note)
                    }
                    else {
                        textToolHeader.text.clear()
                        binding.edittext1.text.clear()
                        delay(10L)
                        Toast.makeText(requireContext(),
                            "Deleted Successfully!",
                            Toast.LENGTH_SHORT).show()
                    }
                    statecount=0
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

            R.id.Share->{
                sharemenu()
                return true
            }
            R.id.saveaspdf ->{
                CreateSaveSharepdf(requireContext(),true).createPdf(textToolHeader.text.toString(),binding.edittext1.text.toString())
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }





    private fun sharemenu() {
        val popupMenu: PopupMenu = PopupMenu(requireContext(),activity!!.findViewById(R.id.overflowActionButton))
        popupMenu.menuInflater.inflate(R.menu.sharemenu,popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when(item.itemId) {
                R.id.shareaspdf -> {
                    CreateSaveSharepdf(requireContext(),false).createPdf(textToolHeader.text.toString(),binding.edittext1.text.toString())
                }
                R.id.shareaslink -> {
                    val shareIntent=Intent().apply() {
                        this.action = Intent.ACTION_SEND
                        this.putExtra(Intent.EXTRA_TEXT, "www.zeusnotes.com/${args.notesEntityid}/${args.checkargs}")
                        this.type = "text/plain/url"
                    }
                    startActivity(shareIntent)
                }
                R.id.textshare ->{
                    val shareIntent=Intent().apply() {
                        this.action = Intent.ACTION_SEND
                        this.putExtra(Intent.EXTRA_TEXT, "${notebyid.notes}")
                        this.type = "text/plain/url"
                    }
                    startActivity(shareIntent)
                }
            }
            true
        })
        popupMenu.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        popingwritingfrag()
        _binding = null
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
                roommodel.deletenote(notebyid)
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
                    val note = NotesEntity(notebyid.id,
                        notedescription,
                        texttitle,
                        description,
                        datentime,
                        Util.colorbg.value!!,
                        statecount)
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
                        Util.colorbg.value!!,if(statecount!=0) lsnote.recyclebincount else 0)
                    roommodel.updatenote(note)
                }
                else -> {
                    newnote=true
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
        //binding.edittext2.setBackgroundResource(color)
        //binding.edittext3.setBackgroundResource(color)
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), color)
    }

    private fun applyreadmode(boolean: Boolean) {

        binding.apply {
            edittext1.apply {
                isFocusable = boolean
                isFocusableInTouchMode = boolean
                isCursorVisible = boolean
            }
//            edittext2.apply {
//                isFocusable = boolean
//                isFocusableInTouchMode = boolean
//                isCursorVisible = boolean
//            }
//            edittext3.apply {
//                isFocusable = boolean
//                isFocusableInTouchMode = boolean
//                isCursorVisible = boolean
       //     }
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
        if (args.checkargs != 2 || statecount!=0) {
            addnote()
        }

        pathList.clear()
        toolbar.setBackgroundResource(R.color.toolbargray)
        activity?.window?.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.toolbargray)
        textToolHeader.text.clear()
        textToolHeader.visibility = View.GONE
        Util.colorbg.value = R.color.bgtransparent
        applyreadmode(true)
        isfav.value = false 
    }


    private fun setupfavtoasts() {
        if (isfav.value == true) {
            Toast.makeText(requireContext(), "Added to favourites", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Removed from favourites", Toast.LENGTH_SHORT).show()
        }
    }
}
