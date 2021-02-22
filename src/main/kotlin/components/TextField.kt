package components

import java.awt.Color
import java.awt.Graphics
import javax.swing.JComponent

class TextField : JComponent() {
    private var text: List<String> = mutableListOf()

    fun setText(text: List<String>) {
        this.text = text
        revalidate()
        repaint()
    }

    override fun paintComponent(g: Graphics?) {
        g!!.color = Color.CYAN
        g.fillRect(2, 2, this.width - 4, this.height - 4)
        g.color = Color.BLACK

        val gap = 30
        var current = 14
        for (line in text) {
            g.drawString(line, 14, current)
            current += gap
        }
    }
}
