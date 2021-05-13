package utils

import TextCoordinate
import tokenizer.Token
import tokenizer.TokenKinds
import java.awt.Font
import java.awt.Graphics

fun Graphics.drawBackground(x0: Int, y0: Int, xOffset: Int, yOffset: Int) {
    val prevColor = this.color
    this.color = Configuration.bgColor
    this.fillRect(x0, y0, xOffset, yOffset)
    this.color = prevColor
}

fun Graphics.setMonospaceFont(fontSize: Int): Int {
    this.font = Font(Font.MONOSPACED, Font.PLAIN, fontSize)
    return this.fontMetrics.charWidth('a')
}

fun Graphics.drawSelection(
    currentRowIndex: Int,
    startRow: Int,
    startColumn: Int,
    endRow: Int,
    endColumn: Int,
    charSize: Int,
    lineLength: Int,
    lineHeight: Int,
    leftMargin: Int,
    topMargin: Int,
) {
    val prevColor = this.color
    this.color = Configuration.selectionColor

    val currentRow = currentRowIndex + 1

    if (startRow == currentRow && endRow == currentRow) {
        val xOffset = startColumn * charSize + leftMargin
        val yOffset = currentRowIndex * lineHeight + topMargin
        this.fillRect(xOffset, yOffset, (endColumn - startColumn) * charSize, lineHeight)
    } else if (startRow == currentRow) {
        val xOffset = startColumn * charSize + leftMargin
        val yOffset = currentRowIndex * lineHeight + topMargin
        this.fillRect(xOffset, yOffset, (lineLength + 1) * charSize - xOffset, lineHeight)
    } else if (startRow < currentRow && endRow > currentRow) {
        val yOffset = currentRowIndex * lineHeight + topMargin
        this.fillRect(leftMargin, yOffset, lineLength * charSize, lineHeight)
    } else if (endRow == currentRow) {
        val yOffset = currentRowIndex * lineHeight + topMargin
        this.fillRect(leftMargin, yOffset, endColumn * charSize, lineHeight)
    }

    this.color = prevColor
}

fun Graphics.drawCursor(
    currentRowIndex: Int,
    row: Int,
    column: Int,
    charSize: Int,
    fontSize: Int,
    lineHeight: Int,
    leftMargin: Int,
    topMargin: Int
) {
    if (currentRowIndex == row) {
        val prevColor = this.color
        this.color = Configuration.cursorColor

        val xOffset = column * charSize + leftMargin
        val yOffset = currentRowIndex * lineHeight + topMargin
        this.drawLine(xOffset, yOffset, xOffset, yOffset + fontSize)

        this.color = prevColor
    }
}

fun Graphics.drawTokenizedLine(
    tokenizedText: MutableList<Token>,
    leftMargin: Int,
    topMargin: Int,
    charSize: Int
) {
    val keywordColor = Configuration.keywordFontColor
    val identifierColor = Configuration.identifierFontColor
    val nothingColor = Configuration.mainFontColor
    val stringColor = Configuration.stringFontColor
    val commentColor = Configuration.commentFontColor

    val prevColor = this.color
    var currentLeftOffset = leftMargin
    for (current in tokenizedText) {
        when (current.kind) {
            TokenKinds.NOTHING -> this.color = nothingColor
            TokenKinds.COMMENT -> this.color = commentColor
            TokenKinds.STRING -> this.color = stringColor
            TokenKinds.IDENTIFIER -> this.color = identifierColor
            TokenKinds.KEYWORD -> this.color = keywordColor
        }

        this.drawString(current.text, currentLeftOffset, topMargin)

        currentLeftOffset += current.text.length * charSize
    }

    this.color = prevColor
}

fun Graphics.drawHighlightedBracket(
    currentRowIndex: Int,
    currentColumnIndex: Int,
    brackets: Pair<TextCoordinate, TextCoordinate>,
    charSize: Int,
    fontSize: Int,
    lineHeight: Int,
    leftMargin: Int,
    topMargin: Int
) {
    val prevColor = this.color
    this.color = Configuration.bracketHighlightColor

    if (currentRowIndex == brackets.first.row && currentColumnIndex == brackets.first.column) {
        val firstXOffset = brackets.first.column * charSize + leftMargin
        val firstYOffset = brackets.first.row * lineHeight + topMargin
        this.drawRect(firstXOffset, firstYOffset, charSize, fontSize)

        val secondXOffset = brackets.second.column * charSize + leftMargin
        val secondYOffset = brackets.second.row * lineHeight + topMargin
        this.drawRect(secondXOffset, secondYOffset, charSize, fontSize)
    }

    if (currentRowIndex == brackets.second.row && currentColumnIndex == brackets.first.column) {
        val firstXOffset = brackets.first.column * charSize + leftMargin
        val firstYOffset = brackets.first.row * lineHeight + topMargin
        this.drawRect(firstXOffset, firstYOffset, charSize, fontSize)

        val secondXOffset = brackets.second.column * charSize + leftMargin
        val secondYOffset = brackets.second.row * lineHeight + topMargin
        this.drawRect(secondXOffset, secondYOffset, charSize, fontSize)
    }

    this.color = prevColor
}