package com.example.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.notes.databinding.FragmentBackgroundcolorBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class Backgroundcolor : BottomSheetDialogFragment() {
    private var _binding: FragmentBackgroundcolorBinding?=null
    private val binding get()=_binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentBackgroundcolorBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.done.setOnClickListener {
            dismiss()
        }
        binding.COLOURcyangreen.setOnClickListener {
            Util.Companion.colorbg.value=R.color.bgcyangreen
        }
        binding.COLORgray.setOnClickListener {
            Util.Companion.colorbg.value=R.color.bggray
        }
        binding.COLOURtrans.setOnClickListener {
            Util.Companion.colorbg.value=R.color.bgtransparent
        }
        binding.COLOURorange.setOnClickListener {
            Util.Companion.colorbg.value=R.color.bgorange
        }
        binding.COLOURyellow.setOnClickListener {
            Util.Companion.colorbg.value=R.color.bgyellow
        }
        binding.COLOURblue.setOnClickListener {
            Util.Companion.colorbg.value=R.color.bgblue
        }
        binding.COLOURpurple.setOnClickListener {
            Util.Companion.colorbg.value=R.color.bgpurple
        }
        binding.COLOURpink.setOnClickListener {
            Util.Companion.colorbg.value=R.color.bgpink
        }
        Util.Companion.colorbg.observe(viewLifecycleOwner, Observer { color->
            binding.done.setTextColor(ContextCompat.getColor(requireContext(),Util.Companion.colorbg.value!! ))
        })

    }
    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}


