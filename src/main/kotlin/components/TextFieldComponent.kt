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
        val tokenizedRow = getTokenizedRow(row - 1)
        val tokenInsertion = getTokenToInsert(tokenizedRow, column)

        val builder = StringBuilder(tokenInsertion.first.text)
        builder.insert(tokenInsertion.second, ch)
        tokenInsertion.first.text = builder.toString()
        this.column++
        repaint()
    }

    private fun getTokenToInsert(tokenizedRow: MutableList<Token>, idx: Int): Pair<Token, Int> {
        var current = 0
        for ((tokenIdx, token) in tokenizedRow.withIndex()) {
            var charIdx = 0
            while (current < idx && charIdx < token.text.length) {
                current++
                charIdx++
            }

            if (current == idx) {
                return if (token.kind == TokenKinds.END_LINE) {
                    val newToken = Token("", TokenKinds.NOTHING)
                    tokenizedRow.add(tokenIdx, newToken)
                    Pair(newToken, 0)
                } else {
                    Pair(tokenizedRow[tokenIdx], charIdx)
                }
            }

        }

        val newToken = Token("", TokenKinds.NOTHING)
        tokenizedRow.add(tokenizedRow.size - 1, newToken)
        return Pair(newToken, 0)
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

    private fun getTokenizedRow(idx: Int): MutableList<Token> {
        if (tokenizedText.count { t -> t.kind == TokenKinds.END_LINE } == 1)
            return tokenizedText

        var i = 0
        var currentTokens = tokenizedText.toList()
        while (i < idx) {
            currentTokens = currentTokens.dropWhile { t -> t.kind != TokenKinds.END_LINE }.drop(1)
            ++i
        }

        return currentTokens.takeWhile { t -> t.kind != TokenKinds.END_LINE }.toMutableList()
    }

    override fun paintComponent(g: Graphics?) {
        g!!.drawBackground(editorLeftMargin, editorTopMargin, width - 4, height -4)

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
                editorTopMargin
            )

            g.drawTokenizedLine(
                getTokenizedRow(i),
                textLeftMargin,
                current,
                charSize
            )

            current += lineHeight

            g.drawCursor(
                i,
                row,
                column,
                charSize,
                fontSize,
                lineHeight,
                textLeftMargin,
                editorTopMargin
            )
        }
    }
}
