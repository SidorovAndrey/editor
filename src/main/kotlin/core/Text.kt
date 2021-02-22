package core

import java.io.BufferedReader
import kotlin.math.abs

class Text(textBuffer: BufferedReader) {
    private var head: TextLine
    private var currentLine: TextLine

    val currentLineText: String
        get() = currentLine.text

    init {
        head = TextLine(textBuffer.readLine() ?: "")
        currentLine = head

        var current: TextLine = head
        for (line in textBuffer.lineSequence()) {
            current.next = TextLine(line, prev = current)
            current = current.next!!
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
            }
        } else {
            for (i in range) {
                if (current.prev == null)
                    return updateCurrentLine(current)

                current = current.prev!!
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
        return updateCurrentLine(prev)
    }

    fun getRange(start: Int, end: Int): List<String> {
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
}