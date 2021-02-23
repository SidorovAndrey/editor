package components

import java.awt.Color
import java.awt.Graphics
import javax.swing.JComponent

class TextField : JComponent() {
    private var text: List<String> = mutableListOf()

    val lineHeight = Configuration.fontSize + Configuration.linesGap

    fun setText(text: List<String>) {
        this.text = text
        revalidate()
        repaint()
    }

    override fun paintComponent(g: Graphics?) {
        g!!.color = Color.CYAN
        g.fillRect(2, 2, this.width - 4, this.height - 4)
        g.color = Color.BLACK

        val fontSize = Configuration.fontSize
        g.font = g.font.deriveFont(fontSize.toFloat())

        var current = fontSize
        for (line in text) {
            g.drawString(line, 14, current)
            current += lineHeight
        }
    }
}
