package controllers

import models.FileModel
import models.TextModel
import views.TextFieldView
import java.io.File

class TextFieldController(
    private val fileModel: FileModel,
    private val textModel: TextModel,
    private val textFieldView: TextFieldView) {

    init {
        fileModel.onFileSet(this) { file -> this.onFileSet(file) }
        textFieldView.onResized(this) { value -> this.onResized(value) }
    }

    private fun onFileSet(file: File) {
        this.textModel.loadText(file.bufferedReader())
        this.textFieldView.setText(this.textModel.currentText)
    }

    private fun onResized(value: Int) {
        textModel.resize(value)
        this.textFieldView.setText(this.textModel.currentText) // TODO: may it be reactive?
    }
}