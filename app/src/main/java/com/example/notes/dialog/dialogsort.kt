package com.example.notes

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.notes.Util.Util

class Dialogsort(context: Context) : Dialog(context) {

    init {
        setCancelable(true)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.fragment_sort_fragmentdialog)
        val sharedPreferences=context.getSharedPreferences("shared_preference",Context.MODE_PRIVATE)
        val editor=sharedPreferences.edit()

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setGravity(Gravity.BOTTOM)
        window?.attributes?.windowAnimations=R.style.DialogAnimation
        editor.putBoolean("bool",false)
        val donebutton=findViewById<Button>(R.id.donesort)
        val cancelbutton=findViewById<Button>(R.id.cancelsort)
        val radiogroup1=findViewById<RadioGroup>(R.id.radioGroup)
        val radiogroup2=findViewById<RadioGroup>(R.id.radioGroup2)

        when(sharedPreferences.getInt("typeofsort",0)){


            1 -> findViewById<RadioButton>(R.id.titlesort).isChecked=true
            2 -> findViewById<RadioButton>(R.id.datesort).isChecked=true
            3 -> findViewById<RadioButton>(R.id.titlesort).isChecked=true
            else -> findViewById<RadioButton>(R.id.datesort).isChecked=true
        }
        when(sharedPreferences.getInt("orderascordesc",0)){
            1 -> findViewById<RadioButton>(R.id.ascendingsort).isChecked=true
            2 -> findViewById<RadioButton>(R.id.descendingsort).isChecked=true
            else -> findViewById<RadioButton>(R.id.ascendingsort).isChecked=true
        }



        donebutton.setOnClickListener {
            val typesort=radiogroup1.checkedRadioButtonId
            val ordersort=radiogroup2.checkedRadioButtonId

            if(ordersort==R.id.ascendingsort)
            {
                editor.putInt("orderascordesc",1)
                when(typesort)
                {
                    R.id.titlesort -> {
                        Util.listforadapter.value=1
                        editor.putInt("typeofsort",1)}
                    R.id.datesort ->{
                        Util.listforadapter.value=2
                        editor.putInt("typeofsort",2)}
                    R.id.notesizesort ->{
                        Util.listforadapter.value=3
                        editor.putInt("typeofsort",3)}
                }
            }
            else{
                editor.putInt("orderascordesc",2)
                when(typesort)
                {
                    R.id.titlesort -> {
                        Util.listforadapter.value=4
                        editor.putInt("typeofsort",1)}
                    R.id.datesort ->{
                        Util.listforadapter.value=5
                        editor.putInt("typeofsort",2)}
                    R.id.notesizesort ->{
                        Util.listforadapter.value=6
                        editor.putInt("typeofsort",3)}
                }
            }
            editor.putInt("listforadapter", Util.listforadapter.value!!)
            editor.apply()

            dismiss()
        }

        cancelbutton.setOnClickListener {
            dismiss()
        }

    }



}