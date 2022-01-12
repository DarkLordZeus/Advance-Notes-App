package com.example.notes.Util

import android.text.Selection.getSelectionStart
import android.widget.EditText
import androidx.core.content.res.TypedArrayUtils.getText
import java.io.IOException
import java.io.LineNumberReader
import java.io.StringReader


class getIndexfrompos {
    private val LINE_SEPARATOR = System.getProperty("line.separator")
    fun getIndexFromPos(line: Int, column: Int , editText: EditText): Int {
        var line = line
        val lineCount = getTrueLineCount(editText)
        if (line < 0) line =
            editText.getLayout().getLineForOffset(getSelectionStart(editText.text)) // No line, take current line
        if (line >= lineCount) line = lineCount - 1 // Line out of bounds, take last line
        val content: String = editText.getText().toString() + LINE_SEPARATOR
        var currentLine = 0
        for (i in 0 until content.length) {
            if (currentLine == line) {
                val lineLength: Int = content.substring(i, content.length).indexOf(LINE_SEPARATOR)
                return if (column < 0 || column > lineLength) i + lineLength // No column or column out of bounds, take last column
                else i + column
            }
            if (content[i].toString() == LINE_SEPARATOR) currentLine++
        }
        return -1 // Should not happen
    }

    // Fast alternative to StringUtils.countMatches(getText().toString(), LINE_SEPARATOR) + 1
    fun getTrueLineCount(editText: EditText): Int {
        val count: Int
        val text: String = editText.getText().toString()
        val sr = StringReader(text)
        val lnr = LineNumberReader(sr)
        count = try {
            lnr.skip(Long.MAX_VALUE)
            lnr.getLineNumber() + 1
        } catch (e: IOException) {
            0 // Should not happen
        }
        sr.close()
        return count
    }
}