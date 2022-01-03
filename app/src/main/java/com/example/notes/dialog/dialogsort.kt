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
        val datesort=findViewById<RadioButton>(R.id.datesort)
        val ascendingsort=findViewById<RadioButton>(R.id.ascendingsort)
        val cancelbutton=findViewById<Button>(R.id.cancelsort)
        val radiogroup1=findViewById<RadioGroup>(R.id.radioGroup)
        val radiogroup2=findViewById<RadioGroup>(R.id.radioGroup2)
        datesort.isChecked=true
        ascendingsort.isChecked=true
        if(sharedPreferences.getBoolean("bool",false)){
            findViewById<RadioButton>(sharedPreferences.getInt("typeofsort", Util.typeofsort)).isChecked=true
            findViewById<RadioButton>(sharedPreferences.getInt("orderascordesc", Util.ascordec)).isChecked=true
        }


        donebutton.setOnClickListener {
            val typesort=radiogroup1.checkedRadioButtonId
            val ordersort=radiogroup2.checkedRadioButtonId

            if(ordersort==R.id.ascendingsort)
            {
                when(typesort)
                {
                    R.id.titlesort -> { Util.listforadapter.value=1 }
                    R.id.datesort ->{
                        Util.listforadapter.value=2}
                    R.id.notesizesort ->{
                        Util.listforadapter.value=3}
                }
            }
            else{
                when(typesort)
                {
                    R.id.titlesort -> {
                        Util.listforadapter.value=4}
                    R.id.datesort ->{
                        Util.listforadapter.value=5}
                    R.id.notesizesort ->{
                        Util.listforadapter.value=6}
                }
            }
            editor.putInt("typeofsort",typesort)
            editor.putInt("orderascordesc",ordersort)
            editor.putInt("listforadapter", Util.listforadapter.value!!)
            editor.putBoolean("bool",true)
            editor.apply()

            dismiss()
        }

        cancelbutton.setOnClickListener {
            dismiss()
        }

    }



}