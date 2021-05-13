package components

import SelectCoordinates
import TextCoordinate
import tokenizer.Token
import tokenizer.TokenKinds
import utils.*
import java.awt.Graphics
import javax.swing.JComponent

class TextFieldComponent : JComponent() {
    private val editorLeftMargin = 2;
    private val editorTopMargin = 2;
    private val textLeftMargin = editorLeftMargin + 10

    private var row: Int = 0
    private var column: Int = 0
    private var rawText: MutableList<String> = mutableListOf()
    private var tokenizedText: MutableList<Token> = mutableListOf()

    private var selectStartRow = 0
    private var selectStartColumn = 0
    private var selectEndRow = 0
    private var selectEndColumn = 0
    private var selectCoordinates = SelectCoordinates(TextCoordinate(0, 0), TextCoordinate(0, 0))

    private var highlightBrackets = Pair(TextCoordinate(-1, -1), TextCoordinate(-1, -1))

    val lineHeight = Configuration.fontSize + Configuration.linesGap

    fun setText(text: MutableList<Token>, rawText: MutableList<String>, row: Int, column: Int, brackets: Pair<TextCoordinate, TextCoordinate>) {
        tokenizedText = text
        this.rawText = rawText
        this.row = row
        this.column = column

        highlightBrackets = brackets
        revalidate()
        repaint()
        requestFocus()
    }

    fun addChar(ch: Char) {
        val tokenizedRow = getTokenizedRow(row)
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

    fun setSelect(selectCoordinates: SelectCoordinates) {
        this.selectCoordinates = selectCoordinates
        column = selectCoordinates.end.column
        row = selectCoordinates.end.row

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
        for ((i, line) in rawText.withIndex()) {
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

            g.drawHighlightedBracket(
                row,
                column,
                highlightBrackets,
                charSize,
                fontSize,
                lineHeight,
                textLeftMargin,
                editorTopMargin
            )
        }
    }
}
