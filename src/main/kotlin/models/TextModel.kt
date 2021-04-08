package models

import core.Text
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
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

    fun copySelected() {
        val textLines = text.getRange(selectStartRow, selectEndRow)
        val text = if (textLines.size < 2)
            textLines[0].substring(selectStartColumn, selectEndColumn)
        else {
            val builder = StringBuilder()
            builder.appendLine(textLines.first().substring(selectStartColumn))
            textLines
                .drop(1)
                .dropLast(1)
                .forEach { str -> builder.appendLine(str) }

            builder.appendLine(textLines.last().substring(0, selectEndColumn))

            builder.toString()
        }

        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(StringSelection(text), null)
    }

    fun paste() {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val bufferText = clipboard.getData(DataFlavor.stringFlavor) as String
        val bufferLines = bufferText.split(System.lineSeparator())

        text.addText(bufferLines.first(), cursorColumn)
        cursorColumn += bufferLines.first().length
        if (bufferLines.size > 1) {
            addNewLine()
            bufferLines.drop(1).take(bufferLines.size - 2).forEachIndexed { i, line -> run {
                text.addText(line, 0)
                cursorColumn += line.length
                addNewLine()
            }}

            text.addText(bufferLines.last(), 0)
            cursorColumn += bufferLines.last().length
        }
    }
}
