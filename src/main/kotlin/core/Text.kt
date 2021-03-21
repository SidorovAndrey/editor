package core

import java.io.BufferedReader
import kotlin.math.abs

class Text(textBuffer: BufferedReader) {
    private var head: TextLine
    private var currentLine: TextLine

    var lineNumber: Int
        private set

    var totalLines: Int
        private set

    var currentLineText: String
        get() = currentLine.text
        set(value) {
            val prev = currentLine.prev
            val next = currentLine.next
            val newLine = TextLine(value)
            if (prev != null) {
                newLine.prev = prev
                prev.next = newLine
            }
            if (next != null) {
                newLine.next = next
                next.prev = newLine
            }
        }

    init {
        head = TextLine(textBuffer.readLine() ?: "")
        currentLine = head
        lineNumber = 1
        totalLines = 1

        var current: TextLine = head
        for (line in textBuffer.lineSequence()) {
            current.next = TextLine(line, prev = current)
            current = current.next!!
            totalLines++
        }
    }

    private fun updateCurrentLine(current: TextLine): String {
        currentLine = current
        return currentLine.text
    }

    fun move(offset: Int): String {
        var current: TextLine = currentLine
        val range = 1..abs(offset)

        if (offset > 0) {
            for (i in range) {
                if (current.next == null)
                    return updateCurrentLine(current)

                current = current.next!!
                lineNumber++
            }
        } else {
            for (i in range) {
                if (current.prev == null)
                    return updateCurrentLine(current)

                current = current.prev!!
                lineNumber--
            }
        }

        return updateCurrentLine(current)
    }

    fun addLine(): String {
        val current = currentLine
        val next = current.next
        current.next = TextLine("")

        val created = current.next!!
        created.prev = current
        created.next = next
        totalLines++

        return updateCurrentLine(created)
    }

    fun removeLine(): String {
        val current = currentLine
        val prev = current.prev
        if (prev == null) {
            head = TextLine("")
            currentLine = head
            return currentLine.text
        }

        prev.next = current.next
        totalLines--
        return updateCurrentLine(prev)
    }

    fun getRange(start: Int, end: Int): MutableList<String> {
        if (start < 1 || end < 1 || start > end)
            throw IllegalArgumentException("Indexes should represent positions of line in text, values start=${start}; end=${end} are incorrect")

        var current = head;
        var idx = 1
        val list = mutableListOf<String>()
        while (idx < start) {
            if (current.next == null)
                return list

            current = current.next!!
            idx++
        }

        while (idx <= end) {
            list.add(current.text)
            if (current.next == null)
                return list

            current = current.next!!
            idx++
        }

        return list
    }

    fun addText(text: String, pos: Int) {
        val currentLine = currentLine
        val builder = StringBuilder(currentLine.text)
        builder.insert(pos, text)

        val newLine = TextLine(builder.toString())
        replaceLine(currentLine, newLine)
    }

    fun deleteText(pos: Int, elements: Int) {
        val currentLine = currentLine
        val builder = StringBuilder(currentLine.text)

        // TODO: use exceptions? handle rows here?
        if (elements < 0 && pos + elements >= 0) {
            builder.deleteRange(pos + elements, pos)
        } else if (elements > 0 && pos + elements < currentLineText.length) {
            builder.deleteRange(pos, pos + elements)
        }

        val newLine = TextLine(builder.toString())
        replaceLine(currentLine, newLine)
    }

    private fun replaceLine(current: TextLine, newLine: TextLine) {
        val prev = current.prev
        val next = current.next
        if (prev != null) {
            prev.next = newLine
            newLine.prev = prev
        } else {
            head = newLine
        }

        if (next != null) {
            next.prev = newLine
            newLine.next = next
        }

        this.currentLine = newLine
    }
}