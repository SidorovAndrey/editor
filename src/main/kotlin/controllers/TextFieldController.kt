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
        textFieldView.asOnControlKeyPressEventProducer().subscribe(this) { key, isShiftPressed, isControlPressed -> this.onKeyPress(key, isShiftPressed, isControlPressed) }
        textFieldView.asOnTextChangedEventProducer().subscribe(this) { str -> this.onTextChanged(str) }
    }

    private fun onFileSet(file: File) {
        this.textModel.loadText(file.bufferedReader())
        this.textFieldView.setText(this.textModel.currentText, this.textModel.cursorRow, this.textModel.cursorColumn)
    }

    private fun onResized(value: Int) {
        textModel.resize(value)
        this.textFieldView.setText(this.textModel.currentText, this.textModel.cursorRow, this.textModel.cursorColumn)
    }

    private fun onKeyPress(key: TextFieldView.KeyTypes, isShiftPressed: Boolean, isControlPressed: Boolean) {
        if (isControlPressed) {
            when (key) {
                TextFieldView.KeyTypes.C -> this.textModel.copySelected()
                TextFieldView.KeyTypes.V -> this.textModel.paste()
            }
        } else if (isShiftPressed) {
            if (!this.textModel.isSelecting)
                this.textModel.startSelect()

            when (key) {
                TextFieldView.KeyTypes.UP -> this.textModel.moveSelectRow(-1)
                TextFieldView.KeyTypes.DOWN -> this.textModel.moveSelectRow(1)
                TextFieldView.KeyTypes.LEFT -> this.textModel.moveSelectColumn(-1)
                TextFieldView.KeyTypes.RIGHT -> this.textModel.moveSelectColumn(1)
            }

            this.textFieldView.setSelect(
                this.textModel.selectStartRow,
                this.textModel.selectStartColumn,
                this.textModel.selectEndRow,
                this.textModel.selectEndColumn
            )
        } else {

            this.textModel.stopSelect()
            this.textFieldView.setSelect(
                1,
                0,
                1,
                0
            )
            when (key) {
                TextFieldView.KeyTypes.UP -> this.textModel.moveCursorUp()
                TextFieldView.KeyTypes.DOWN -> this.textModel.moveCursorDown()
                TextFieldView.KeyTypes.LEFT -> this.textModel.moveCursorLeft()
                TextFieldView.KeyTypes.RIGHT -> this.textModel.moveCursorRight()
                TextFieldView.KeyTypes.HOME -> this.textModel.moveCursorToLineBegin()
                TextFieldView.KeyTypes.END -> this.textModel.moveCursorToLineEnd()
                TextFieldView.KeyTypes.PAGE_UP -> this.textModel.moveCursorToFirstLine()
                TextFieldView.KeyTypes.PAGE_DOWN -> this.textModel.moveCursorToLastLine()
                TextFieldView.KeyTypes.BACK_SPACE -> this.textModel.deletePrevious()
                TextFieldView.KeyTypes.DELETE -> this.textModel.deleteNext()
                TextFieldView.KeyTypes.ENTER -> this.textModel.addNewLine()
            }
        }

        this.textFieldView.setText(this.textModel.currentText, this.textModel.cursorRow, this.textModel.cursorColumn)
    }

    private fun onTextChanged(str: String) {
        this.textModel.addText(str)
        this.textFieldView.setText(this.textModel.currentText, this.textModel.cursorRow, this.textModel.cursorColumn)
    }
}