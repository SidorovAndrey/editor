package components

import tokenizer.Token
import tokenizer.TokenKinds
import utils.*
import java.awt.Color
import java.awt.Graphics
import javax.swing.JComponent

class TextFieldComponent : JComponent() {
    private val editorLeftMargin = 2;
    private val editorTopMargin = 2;
    private val textLeftMargin = editorLeftMargin + 10

    private var text: MutableList<String> = mutableListOf()
    private var row: Int = 1
    private var column: Int = 0
    private var tokenizedText: MutableList<Token> = mutableListOf()

    private var drawTokenized = false

    private var selectStartRow = 0
    private var selectStartColumn = 0
    private var selectEndRow = 0
    private var selectEndColumn = 0

    val lineHeight = Configuration.fontSize + Configuration.linesGap

    fun setText(text: MutableList<String>, row: Int, column: Int) {
        this.text = text
        this.row = row
        this.column = column

        drawTokenized = false

        revalidate()
        repaint()
        requestFocus()
    }

    fun setTokenizedText(text: MutableList<Token>) {
        tokenizedText = text
        drawTokenized = true
        revalidate()
        repaint()
        requestFocus()
    }

    fun addChar(ch: Char) {
        val builder = StringBuilder(this.text[row - 1])
        builder.insert(column, ch)
        this.text[row - 1] = builder.toString()
        this.column++
        repaint()
    }

    fun setSelect(startRow: Int, startColumn: Int, endRow: Int, endColumn: Int) {
        selectStartRow = startRow
        selectStartColumn = startColumn
        selectEndRow = endRow
        selectEndColumn = endColumn
        column = selectEndColumn
        row = selectEndRow
        revalidate()
        repaint()
        requestFocus()
    }

    private fun getCurrentTokenizedRow(idx: Int): MutableList<Token> {
        var i = 0
        var currentTokens = tokenizedText.toList()
        while (i < idx) {
            currentTokens = currentTokens.dropWhile { t -> t.kind != TokenKinds.END_LINE }.drop(1)
            ++i
        }

        return currentTokens.takeWhile { t -> t.kind != TokenKinds.END_LINE }.toMutableList()
    }

    override fun paintComponent(g: Graphics?) {
        g!!.drawBackground(editorLeftMargin, editorTopMargin, width - 4, height -4, Color.CYAN)

        val fontSize = Configuration.fontSize
        val charSize = g.setMonospaceFont(fontSize)

        var current = fontSize
        for ((i, line) in text.withIndex()) {
            g.drawSelection(
                i,
                selectStartRow,
                selectStartColumn,
                selectEndRow,
                selectEndColumn,
                charSize,
                line.length,
                lineHeight,
                textLeftMargin,
                editorTopMargin,
                Color.LIGHT_GRAY
            )

            if (drawTokenized)
                g.drawTokenizedLine(
                    getCurrentTokenizedRow(i),
                    textLeftMargin,
                    current,
                    charSize
                )
            else
                g.drawString(line, textLeftMargin, current)

            current += lineHeight

            g.drawCursor(
                i,
                row,
                column,
                charSize,
                fontSize,
                lineHeight,
                textLeftMargin,
                editorTopMargin,
                Color.RED
            )
        }
    }
}
