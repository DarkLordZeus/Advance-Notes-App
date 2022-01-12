package com.example.notes.Util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class CreateSaveSharepdf(val context: Context,val isSave:Boolean) {
    private lateinit var document:PdfDocument
    private lateinit var paint:Paint
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun createPdf(title:String,description:String){
        document= PdfDocument()
        paint=Paint()
        val pageInfo:PdfDocument.PageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
        // start a page
        var page:PdfDocument.Page = document.startPage(pageInfo)
        var canvas: Canvas = page.canvas
        paint.color = Color.BLACK
        paint.textSize= 16F
        canvas.drawText(title, 20F, 40F, paint);
        paint.textSize=12F
        val lines:Int=description.length/45
        for(i in 0 until lines){
            canvas.drawText(description.subSequence(45*i,45*(i+1)).toString(), 20F,  60F+(15F)*(i), paint)
        }

        document.finishPage(page)

        if(!isSave) {
            val directorypath = context.getExternalFilesDir(null)?.path + "/zeusnotes/"
            val file = File(directorypath)
            if (!file.exists()) {
                file.mkdirs()
            }
            val targetPdf = "$directorypath$title.pdf"
            val filePath = File(targetPdf)
            try {
                document.writeTo(FileOutputStream(filePath))
                Toast.makeText(context, "Done", Toast.LENGTH_LONG).show()
            } catch (e: IOException) {
                Log.e("main", "error $e")
                Toast.makeText(context, "Something wrong: $e",
                    Toast.LENGTH_LONG).show()
            }
            document.close()

            val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                FileProvider.getUriForFile(context, context.packageName + ".provider", File(targetPdf))
            } else {
                Uri.fromFile(File(targetPdf))
            }
            val share = Intent()
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            share.action = Intent.ACTION_SEND
            share.type = "application/pdf"
            share.putExtra(Intent.EXTRA_STREAM, uri)
            context.startActivity(share)
        }

        else{
        val directorypathforsave = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path +"/";
        val targetPdftosave = "$directorypathforsave$title.pdf"
        val filePathtosave = File(targetPdftosave)
        try {
            document.writeTo(FileOutputStream(filePathtosave))
            Toast.makeText(context, "Saved", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            Log.e("main", "error $e")
            Toast.makeText(context, "Something wrong: $e",
                Toast.LENGTH_LONG).show()
        }

        document.close()}


    }
}