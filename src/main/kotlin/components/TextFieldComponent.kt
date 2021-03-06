package components

import SelectCoordinates
import TextCoordinate
import textCoordinateCopy
import textCoordinateDefault
import textCoordinateNegative
import tokenizer.Token
import tokenizer.TokenKinds
import utils.*
import java.awt.Cursor
import java.awt.Graphics
import java.awt.Toolkit
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent

class TextFieldComponent : JComponent() {
    private val editorLeftMargin = Configuration.editorLeftMargin
    private val editorTopMargin = Configuration.editorTopMargin
    private val textLeftMargin = editorLeftMargin + Configuration.textLeftMargin

    private var cursorPosition = textCoordinateDefault()
    private var rawText: MutableList<String> = mutableListOf()
    private var tokenizedText: MutableList<Token> = mutableListOf()

    private var selectCoordinates = SelectCoordinates(textCoordinateDefault(), textCoordinateDefault())

    private var highlightBrackets = Pair(textCoordinateNegative(), textCoordinateNegative())

    val lineHeight = Configuration.fontSize + Configuration.linesGap
    var charWidth: Int = 0
        private set

    init {
        cursor = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR)
    }

    fun setText(text: MutableList<Token>, rawText: MutableList<String>, textCoordinate: TextCoordinate, brackets: Pair<TextCoordinate, TextCoordinate>) {
        tokenizedText = text
        this.rawText = rawText
        this.cursorPosition = TextCoordinate(textCoordinate.row, textCoordinate.column)

        highlightBrackets = brackets
        revalidate()
        repaint()
        requestFocus()
    }

    fun addChar(ch: Char) {
        val tokenizedRow = getTokenizedRow(cursorPosition.row)
        val tokenInsertion = getTokenToInsert(tokenizedRow)

        val builder = StringBuilder(tokenInsertion.first.text)
        builder.insert(tokenInsertion.second, ch)
        tokenInsertion.first.text = builder.toString()
        cursorPosition.column++
        repaint()
    }

    private fun getTokenToInsert(tokenizedRow: MutableList<Token>): Pair<Token, Int> {
        // try to find existing token to insert
        var current = 0
        for ((tokenIdx, token) in tokenizedRow.withIndex()) {
            var charIdx = 0
            while (current < cursorPosition.column && charIdx < token.text.length) {
                current++
                charIdx++
            }

            if (current == cursorPosition.column && token.kind != TokenKinds.END_LINE) {
                return Pair(tokenizedRow[tokenIdx], charIdx)
            }
        }

        // if not found, insert a new one
        val newToken = Token("", TokenKinds.NOTHING)
        insertToken(newToken)
        return Pair(newToken, 0)
    }

    private fun insertToken(token: Token) {
        var idx = 0
        var currentRow = 0
        while (currentRow < cursorPosition.row) {
            if (tokenizedText[idx].kind == TokenKinds.END_LINE)
                currentRow++

            idx++
        }

        idx += cursorPosition.column
        tokenizedText.add(idx, token)
    }

    fun setSelect(selectCoordinates: SelectCoordinates, textCoordinate: TextCoordinate) {
        this.selectCoordinates = selectCoordinates
        cursorPosition = textCoordinateCopy(textCoordinate)

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

        if (currentTokens.isNotEmpty() && currentTokens[0].kind == TokenKinds.END_LINE)
            return currentTokens.take(1).toMutableList()

        return currentTokens.takeWhile { t -> t.kind != TokenKinds.END_LINE }.toMutableList()
    }

    override fun paintComponent(g: Graphics?) {
        g!!.drawBackground(editorLeftMargin, editorTopMargin, width - 4, height -4)

        val fontSize = Configuration.fontSize
        charWidth = g.setMonospaceFont(fontSize)

        var current = fontSize
        for ((i, line) in rawText.withIndex()) {
            g.drawSelection(
                i,
                selectCoordinates.start.row,
                selectCoordinates.start.column,
                selectCoordinates.end.row,
                selectCoordinates.end.column,
                charWidth,
                line.length,
                lineHeight,
                textLeftMargin,
                editorTopMargin
            )

            g.drawTokenizedLine(
                getTokenizedRow(i),
                textLeftMargin,
                current,
                charWidth
            )

            current += lineHeight

            g.drawCursor(
                i,
                cursorPosition.row,
                cursorPosition.column,
                charWidth,
                fontSize,
                lineHeight,
                textLeftMargin,
                editorTopMargin
            )

            g.drawHighlightedBracket(
                cursorPosition.row,
                cursorPosition.column,
                highlightBrackets,
                charWidth,
                fontSize,
                lineHeight,
                textLeftMargin,
                editorTopMargin
            )
        }
    }
}
