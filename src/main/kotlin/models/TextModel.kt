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

    var isSelecting: Boolean = false
        private set

    var selectStartRow: Int = 0
        private set

    var selectStartColumn: Int = 0
        private set

    var selectEndRow: Int = 0
        private set

    var selectEndColumn: Int = 0
        private set

    fun loadText(bufferedReader: BufferedReader) {
        text = Text(bufferedReader)
    }

    fun resize(size: Int) {
        lastRow = firstRow + size
    }

    fun moveCursorUp() {
        text.move(-1)
        if (text.currentLineText.length < cursorColumn)
            cursorColumn = text.currentLineText.length
    }

    fun moveCursorDown() {
        text.move(1)
        if (text.currentLineText.length < cursorColumn)
            cursorColumn = text.currentLineText.length
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
        if (cursorColumn == 0) {
            cursorColumn = text.mergeCurrentLineWithPrevious()
        } else {
            text.deleteText(cursorColumn, -1)
            cursorColumn--
        }
    }

    fun deleteNext() {
        text.deleteText(cursorColumn, 1)
    }

    fun addNewLine() {
        val newLineText = text.currentLineText.substring(cursorColumn)
        text.deleteText(cursorColumn, newLineText.length)
        text.addLine(newLineText)
        cursorColumn = 0
    }

    fun startSelect() {
        isSelecting = true
        selectStartRow = cursorRow
        selectEndRow = cursorRow
        selectStartColumn = cursorColumn
        selectEndColumn = cursorColumn
    }

    fun stopSelect() {
        isSelecting = false
    }

    fun moveSelectColumn(offset: Int) {
        if (cursorColumn + offset < 0 || cursorColumn + offset > currentText[cursorRow].length)
            return

        cursorColumn += offset
        if (cursorColumn > selectStartColumn)
            selectEndColumn = cursorColumn
        else
            selectStartColumn = cursorColumn
    }

    fun moveSelectRow(offset: Int) {
        text.move(offset)

        if (cursorRow > selectEndRow)
            selectEndRow = cursorRow
        else if (cursorRow >= selectStartRow)
            if (offset > 0)
                selectStartRow = cursorRow
            else
                selectEndRow = cursorRow
        else
            selectStartRow = cursorRow
    }
}