package models

import core.Text
import java.io.BufferedReader

class TextModel(private var text: Text) {
    private var firstRow = 1
    private var lastRow = 1

    val currentText: MutableList<String>
        get() = text.getRange(firstRow, lastRow)

    var cursorColumn: Int = 0
        private set

    val cursorRow: Int
        get() = text.lineNumber

    fun loadText(bufferedReader: BufferedReader) {
        text = Text(bufferedReader)
    }

    fun resize(size: Int) {
        lastRow = firstRow + size
    }

    fun moveCursorUp() {
        text.move(-1)
    }

    fun moveCursorDown() {
        text.move(1)
    }

    fun moveCursorRight() {
        val line = text.currentLineText
        if (line.length > cursorColumn)
            cursorColumn++
    }

    fun moveCursorLeft() {
        if (cursorColumn > 0)
            cursorColumn--
    }

    fun moveCursorToFirstLine() {
        text.move(-text.lineNumber)
        if (cursorColumn > text.currentLineText.length)
            cursorColumn = text.currentLineText.length
    }

    fun moveCursorToLastLine() {
        text.move(text.totalLines - text.lineNumber)
        if (cursorColumn > text.currentLineText.length)
            cursorColumn = text.currentLineText.length
    }

    fun moveCursorToLineBegin() {
        cursorColumn = 0
    }

    fun moveCursorToLineEnd() {
        cursorColumn = text.currentLineText.length
    }

    fun addText(string: String) {
        text.addText(string, cursorColumn)
        cursorColumn += string.length
    }

    fun deletePrevious() {
        // TODO: handle cursorColumn == 0 situation
        text.deleteText(cursorColumn, -1)
        cursorColumn--
    }

    fun deleteNext() {
        text.deleteText(cursorColumn, 1)
    }
}