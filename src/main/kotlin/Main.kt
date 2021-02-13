import controllers.MenuController
import models.FileModel
import views.EditorView
import views.MenuView
import java.awt.Color
import java.awt.Graphics
import javax.swing.JComponent

fun main() {
    val fileModel = FileModel()

    val viewRepository = ViewRepository()
    val editorView = EditorView(viewRepository)
    val menuController = MenuController(fileModel, viewRepository[MenuView::class])

    editorView.show()
}

class TextField(private val text: String) : JComponent() {
    override fun paintComponent(g: Graphics?) {
        g!!.color = Color.CYAN
        g.fillRect(2, 2, this.width - 4, this.height - 4)
        g.drawString(text, 4, 4)
    }
}
