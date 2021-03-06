package models

import SelectCoordinates
import TextCoordinate
import core.Text
import textCoordinateCopy
import textCoordinateDefault
import tokenizer.Token
import tokenizer.Tokenizer
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.io.BufferedReader

class TextModel(private var text: Text) {
    private var firstRow = 0
    private var lastRow = 0

    private var tokenizer = Tokenizer(text)

    val currentText: MutableList<String>
        get() = text.getRange(firstRow, lastRow)

    var cursorColumn: Int = 0
        private set

    val cursorRow: Int
        get() = text.lineIndex - firstRow

    var isSelecting: Boolean = false
        private set

    var selectInitialCoordinates = textCoordinateDefault()
    var selectCoordinates = SelectCoordinates(textCoordinateDefault(), textCoordinateDefault())

    fun loadText(bufferedReader: BufferedReader) {
        text = Text(bufferedReader)
        tokenizer = Tokenizer(text)
    }

    fun getAllText(): String {
        val lines = text.getRange(1, text.totalLines)
        val builder = StringBuilder()
        lines.forEach { line -> builder.appendLine(line) }
        return builder.toString()
    }

    fun getTokenizedText(): MutableList<Token> {
        return tokenizer.getRange(firstRow, lastRow)
    }

    fun resize(size: Int) {
        lastRow = if (size < 2)
            firstRow + 1
        else
            firstRow + size
    }

    fun moveCursorUp() {
        text.move(-1)
        if (text.currentLineText.length < cursorColumn)
            cursorColumn = text.currentLineText.length

        if (text.lineIndex < firstRow) {
            val size = lastRow - firstRow
            firstRow = text.lineIndex
            lastRow = firstRow + size
        }
    }

    fun moveCursorDown() {
        text.move(1)
        if (text.currentLineText.length < cursorColumn)
            cursorColumn = text.currentLineText.length

        if (text.lineIndex > lastRow) {
            val size = lastRow - firstRow
            lastRow = text.lineIndex
            firstRow = lastRow - size
        }
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
        text.move(-text.lineIndex)
        if (cursorColumn > text.currentLineText.length)
            cursorColumn = text.currentLineText.length

        val size = lastRow - firstRow
        firstRow = text.lineIndex
        lastRow = firstRow + size
    }

    fun moveCursorToLastLine() {
        text.move(text.totalLines - text.lineIndex)
        if (cursorColumn > text.currentLineText.length)
            cursorColumn = text.currentLineText.length

        val size = lastRow - firstRow
        lastRow = text.lineIndex
        firstRow = lastRow - size
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

    fun startSelect(origin: TextCoordinate? = null) {
        isSelecting = true
        val initial = if (origin != null) textCoordinateCopy(origin) else TextCoordinate(cursorRow, cursorColumn)
        selectCoordinates = SelectCoordinates(textCoordinateCopy(initial), textCoordinateCopy(initial))
        selectInitialCoordinates = textCoordinateCopy(initial)
    }

    fun stopSelect() {
        isSelecting = false
        selectCoordinates = SelectCoordinates(textCoordinateDefault(), textCoordinateDefault())
        selectInitialCoordinates = textCoordinateDefault()
    }

    fun moveSelectColumn(offset: Int) {
        if (cursorColumn + offset < 0 || cursorColumn + offset > currentText[cursorRow].length)
            return

        cursorColumn += offset
        updateSelectedColumn()
    }

    fun moveSelectColumnToBegin() {
        moveSelectColumn(-cursorColumn)
    }

    fun moveSelectColumnToEnd() {
        moveSelectColumn(text.currentLineText.length - cursorColumn)
    }

    private fun updateSelectedColumn() {
        if (selectCoordinates.start.row == selectCoordinates.end.row) {
            if (cursorColumn == selectInitialCoordinates.column) {
                selectCoordinates.start.column = cursorColumn
                selectCoordinates.end.column = cursorColumn
            }
            else if (cursorColumn > selectInitialCoordinates.column)
                selectCoordinates.end.column = cursorColumn
            else
                selectCoordinates.start.column = cursorColumn
        } else {
            if (cursorRow == selectCoordinates.start.row)
                selectCoordinates.start.column = cursorColumn
            else
                selectCoordinates.end.column = cursorColumn
        }
    }

    fun moveSelectRow(offset: Int) {
        text.move(offset)

        if (text.currentLineText.length < cursorColumn)
            cursorColumn = text.currentLineText.length

        if (cursorRow > selectCoordinates.end.row)
            selectCoordinates.end.row = cursorRow
        else if (cursorRow >= selectCoordinates.start.row)
            if (offset > 0)
                selectCoordinates.start.row = cursorRow
            else
                selectCoordinates.end.row = cursorRow
        else
            selectCoordinates.start.row = cursorRow

        updateSelectedColumn()
    }

    fun copySelected() {
        val text = getSelectedText()

        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(StringSelection(text), null)
    }

    private fun getSelectedText(): String {
        val textLines = text.getRange(selectCoordinates.start.row, selectCoordinates.end.row)
        return if (textLines.size < 2)
            textLines[0].substring(selectCoordinates.start.column, selectCoordinates.end.column)
        else {
            val builder = StringBuilder()
            builder.appendLine(textLines.first().substring(selectCoordinates.start.column))
            textLines
                .drop(1)
                .dropLast(1)
                .forEach { str -> builder.appendLine(str) }

            builder.appendLine(textLines.last().substring(0, selectCoordinates.end.column))

            builder.toString()
        }
    }

    fun paste() {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val bufferText = clipboard.getData(DataFlavor.stringFlavor) as String
        val bufferLines = bufferText.split(System.lineSeparator())

        text.addText(bufferLines.first(), cursorColumn)
        cursorColumn += bufferLines.first().length
        if (bufferLines.size > 1) {
            addNewLine()
            bufferLines.drop(1).take(bufferLines.size - 2).forEach { line -> run {
                text.addText(line, 0)
                cursorColumn += line.length
                addNewLine()
            }}

            text.addText(bufferLines.last(), 0)
            cursorColumn += bufferLines.last().length
        }
    }

    fun cut() {
        copySelected()
        deleteSelectedText()
        stopSelect()
    }

    private fun deleteSelectedText() {
        if (selectCoordinates.start.row == selectCoordinates.end.row) {
            text.deleteText(selectCoordinates.start.column, selectCoordinates.end.column - selectCoordinates.start.column)
        } else {
            while (cursorRow != selectCoordinates.start.row)
                text.move(-1)

            text.deleteText(selectCoordinates.start.column, text.currentLineText.length - selectCoordinates.start.column)
            text.move(1)
            cursorColumn = 0

            for (i in 0 until selectCoordinates.end.row - selectCoordinates.start.row - 1) {
                text.removeLine()
                text.move(1)
            }

            text.deleteText(0, selectCoordinates.end.column)
            cursorColumn = text.mergeCurrentLineWithPrevious()
        }
    }

    fun setCursor(textCoordinate: TextCoordinate) {
        text.move(textCoordinate.row - text.lineIndex)
        cursorColumn =
            if (textCoordinate.column < text.currentLineText.length)
                textCoordinate.column
            else
                text.currentLineText.length

    }

    fun setSelectCoordinates(origin: TextCoordinate, current: TextCoordinate) {
        startSelect(origin)
        cursorColumn =
            if (current.column < text.currentLineText.length)
                current.column
            else
                text.currentLineText.length

        moveSelectRow(current.row - text.lineIndex)
    }
}
