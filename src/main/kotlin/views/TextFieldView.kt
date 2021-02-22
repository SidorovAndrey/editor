package views

import ViewRepository
import components.TextField
import javax.swing.JFrame

class TextFieldView(
    viewRepository: ViewRepository,
    frame : JFrame
) : BaseView(viewRepository) {

    private val textField = TextField()
    private var text: List<String> = mutableListOf()

    init {
        frame.add(textField)
    }

    fun setText(text: List<String>) {
        this.text = text
        textField.setText(text)
    }
}