package views

import ViewRepository
import javax.swing.JFrame

class EditorView(viewRepository: ViewRepository) : BaseView(viewRepository) {
    private val frame = JFrame("Editor")
    private val menuView = MenuView(viewRepository, frame)
    private val textFieldView = TextFieldView(viewRepository, frame)

    init {
        frame.extendedState = JFrame.MAXIMIZED_BOTH
    }

    fun show() {
        frame.isVisible = true
    }
}