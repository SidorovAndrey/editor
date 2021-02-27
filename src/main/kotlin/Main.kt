import controllers.MenuController
import controllers.TextFieldController
import models.FileModel
import models.TextModel
import views.EditorView
import views.MenuView
import views.TextFieldView
import java.io.File

fun main() {
    Configuration.load(File("config.properties").inputStream())

    val fileModel = FileModel()
    val textModel = TextModel()

    val viewRepository = ViewRepository()
    val editorView = EditorView(viewRepository)
    val menuController = MenuController(fileModel, viewRepository[MenuView::class])
    val textFieldController = TextFieldController(fileModel, textModel, viewRepository[TextFieldView::class])

    editorView.show()
}
