package models

import core.Text
import java.io.BufferedReader
import java.io.StringReader

class TextModel {
    private var text: Text
    private var firstRow = 1
    private var lastRow = 10 // TODO: use view size calculations

    init {
        val reader = StringReader("")
        val bufferedReader = BufferedReader(reader)
        text = Text(bufferedReader)
    }

    val currentText: List<String>
        get() = text.getRange(firstRow, lastRow)

    fun loadText(bufferedReader: BufferedReader) {
        text = Text(bufferedReader)
    }

    fun resize(size: Int) {
        lastRow = firstRow + size
    }
}