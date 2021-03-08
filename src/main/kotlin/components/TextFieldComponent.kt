package components

import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import javax.swing.JComponent

class TextFieldComponent : JComponent() {
    private var text: MutableList<String> = mutableListOf()
    private var row: Int = 1
    private var column: Int = 0

    private val widthZero = 2;
    private val heightZero = 2;
    private val textLeftMargin = widthZero + 10

    val lineHeight = Configuration.fontSize + Configuration.linesGap

    fun setText(text: MutableList<String>, row: Int, column: Int) {
        this.text = text
        this.row = row
        this.column = column
        revalidate()
        repaint()
        requestFocus()
    }

    fun addChar(ch: Char) {
        // TODO: fix bug when typing TO FAST
        val builder = StringBuilder(this.text[row - 1])
        builder.insert(column, ch)
        this.text[row - 1] = builder.toString()
        this.column++
        revalidate()
        repaint()
        requestFocus()
    }

    override fun paintComponent(g: Graphics?) {
        g!!.color = Color.CYAN
        g.fillRect(widthZero, heightZero, this.width - 4, this.height - 4)
        g.color = Color.BLACK

        val fontSize = Configuration.fontSize
        g.font = Font(Font.MONOSPACED, Font.PLAIN, fontSize)
        val charSize = g.fontMetrics.charWidth('a')

        var current = fontSize
        for ((i, line) in text.withIndex()) {
            g.drawString(line, textLeftMargin, current)
            if (i + 1 == row) {
                val xOffset = column * charSize + textLeftMargin
                val yOffset = i * lineHeight + heightZero
                g.color = Color.RED
                g.drawLine(xOffset, yOffset, xOffset, yOffset + fontSize)
                g.color = Color.BLACK
            }

            current += lineHeight
        }
    }
}
