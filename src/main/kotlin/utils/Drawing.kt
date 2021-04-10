package utils

import java.awt.Color
import java.awt.Font
import java.awt.Graphics

fun Graphics.drawBackground(x0: Int, y0: Int, xOffset: Int, yOffset: Int, color: Color) {
    val prevColor = this.color
    this.color = color
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
    color: Color,
) {
    val prevColor = this.color
    this.color = color

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
    topMargin: Int,
    color: Color
) {
    if (currentRowIndex + 1 == row) {
        val prevColor = this.color
        this.color = color

        val xOffset = column * charSize + leftMargin
        val yOffset = currentRowIndex * lineHeight + topMargin
        this.drawLine(xOffset, yOffset, xOffset, yOffset + fontSize)

        this.color = prevColor
    }
}