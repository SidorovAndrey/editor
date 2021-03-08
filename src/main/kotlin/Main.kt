import controllers.MenuController
import controllers.TextFieldController
import core.Text
import models.FileModel
import models.TextModel
import views.EditorView
import views.MenuView
import views.TextFieldView
import java.io.BufferedReader
import java.io.File
import java.io.StringReader

fun main() {
    Configuration.load(File("config.properties").inputStream())

    val reader = StringReader("")
    val bufferedReader = BufferedReader(reader)

    val fileModel = FileModel()
    val textModel = TextModel(Text(bufferedReader))

    val viewRepository = ViewRepository()
    val editorView = EditorView(viewRepository)
    val menuController = MenuController(fileModel, viewRepository[MenuView::class])
    val textFieldController = TextFieldController(fileModel, textModel, viewRepository[TextFieldView::class])

    editorView.show()
}
