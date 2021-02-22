package core

import java.io.BufferedReader
import java.io.StringReader
import kotlin.test.Test

class TextTest {
    @Test
    fun initShouldSetCurrentLineToFirstRow() {
        val expectedString = "first"
        val reader = StringReader(expectedString)
        val bufferedReader = BufferedReader(reader)

        val text = Text(bufferedReader)

        assert(text.currentLineText == expectedString)
    }

    @Test
    fun initShouldCreateObjectFromEmptyString() {
        val reader = StringReader("")
        val bufferReader = BufferedReader(reader)

        val text = Text(bufferReader)

        assert(text.currentLineText == "")
    }

    @Test
    fun moveShouldMoveForward() {
        val lines = arrayOf("first", "second", "third")
        val reader = StringReader(lines.joinToString(System.lineSeparator()) { it })
        val bufferedReader = BufferedReader(reader)

        val text = Text(bufferedReader)

        var actualText = text.currentLineText
        assert(actualText == lines[0])

        actualText = text.move(1)
        assert(actualText == lines[1])

        actualText = text.move(1)
        assert(actualText == lines[2])
    }

    @Test
    fun moveShouldMoveBack() {
        val lines = arrayOf("first", "second")
        val reader = StringReader(lines.joinToString(System.lineSeparator()) { it })
        val bufferedReader = BufferedReader(reader)

        val text = Text(bufferedReader)

        var actualLine = text.currentLineText
        assert(actualLine == lines[0])

        actualLine = text.move(1)
        assert(actualLine == lines[1])

        actualLine = text.move(-1)
        assert(actualLine == lines[0])
    }

    @Test
    fun moveShouldSetToHeadWhenNegativeOffsetIsGoingOutOfBounds() {
        val expectedString = "test"
        val reader = StringReader(expectedString)
        val bufferedReader = BufferedReader(reader)

        val text = Text(bufferedReader)
        val currentLine = text.move(-1)

        assert(currentLine == expectedString)
    }

    @Test
    fun moveShouldSetToLatestElementWhenPositiveOffsetIsGoingOutOfBounds() {
        val lines = arrayOf("first", "second")
        val reader = StringReader(lines.joinToString(System.lineSeparator()) { it })
        val bufferedReader = BufferedReader(reader)

        val text = Text(bufferedReader)
        val currentLine = text.move(2)

        assert(currentLine == lines[1])
    }

    @Test
    fun addLineShouldAppendLineAfterCurrentLine() {
        val lines = arrayOf("first", "second")
        val reader = StringReader(lines.joinToString(System.lineSeparator()) { it })
        val bufferedReader = BufferedReader(reader)

        val text = Text(bufferedReader)
        val added = text.addLine()

        assert(added == "")
        assert(text.move(-1) == lines[0])
        assert(text.move(2) == lines[1])
    }

    @Test
    fun removeLineShouldRemoveCurrentLine() {
        val lines = arrayOf("first", "second")
        val reader = StringReader(lines.joinToString(System.lineSeparator()) { it })
        val bufferedReader = BufferedReader(reader)

        val text = Text(bufferedReader)
        text.move(1)
        val prevLine = text.removeLine()

        assert(prevLine == lines[0])
        assert(text.move(1) == lines[0])
    }

    @Test
    fun removeLineShouldRemoveCurrentLineOnSingleLineText() {
        val reader = StringReader("test")
        val bufferedReader = BufferedReader(reader)

        val text = Text(bufferedReader)
        val line = text.removeLine()

        assert(line == "")
    }
}