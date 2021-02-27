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
        fileModel.subscribe(this) { file -> this.onFileSet(file) }
        textFieldView.asOnResizedEventProducer().subscribe(this) { value -> this.onResized(value) }
        textFieldView.asOnKeyPressEventProducer().subscribe(this) { key, ch -> this.onKeyPress(key, ch) }
    }

    private fun onFileSet(file: File) {
        this.textModel.loadText(file.bufferedReader())
        this.textFieldView.setText(this.textModel.currentText, this.textModel.cursorRow, this.textModel.cursorColumn)
    }

    private fun onResized(value: Int) {
        textModel.resize(value)
        this.textFieldView.setText(this.textModel.currentText, this.textModel.cursorRow, this.textModel.cursorColumn)
    }

    private fun onKeyPress(key: TextFieldView.KeyTypes, char: Char) {
        when (key) {
            TextFieldView.KeyTypes.UP -> this.textModel.moveCursorUp()
            TextFieldView.KeyTypes.DOWN -> this.textModel.moveCursorDown()
            TextFieldView.KeyTypes.LEFT -> this.textModel.moveCursorLeft()
            TextFieldView.KeyTypes.RIGHT -> this.textModel.moveCursorRight()
            TextFieldView.KeyTypes.HOME -> this.textModel.moveCursorToLineBegin()
            TextFieldView.KeyTypes.END -> this.textModel.moveCursorToLineEnd()
            TextFieldView.KeyTypes.PAGE_UP -> this.textModel.moveCursorToFirstLine()
            TextFieldView.KeyTypes.PAGE_DOWN -> this.textModel.moveCursorToLastLine()
        }

        this.textFieldView.setText(this.textModel.currentText, this.textModel.cursorRow, this.textModel.cursorColumn)
    }
}