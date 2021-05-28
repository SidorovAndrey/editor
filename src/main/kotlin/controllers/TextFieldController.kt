package controllers

import TextCoordinate
import models.TextModel
import views.TextFieldView

class TextFieldController(
    private val textModel: TextModel,
    private val textFieldView: TextFieldView) {

    init {
        textFieldView.asOnResizedEventProducer().subscribe(this) { value -> this.onResized(value) }
        textFieldView.asOnControlKeyPressEventProducer().subscribe(this) { key, isShiftPressed, isControlPressed -> this.onKeyPress(key, isShiftPressed, isControlPressed) }
        textFieldView.asOnTextChangedEventProducer().subscribe(this) { str -> this.onTextChanged(str) }
        textFieldView.asOnCursorChangedEventProducer().subscribe(this) { coordinates -> this.onCursorChanged(coordinates) }
        textFieldView.asOnCursorDraggedEventProducer().subscribe(this) { origin, current -> this.onCursorDragged(origin, current) }
    }

    private fun onResized(value: Int) {
        textModel.resize(value)
        updateTextFieldView()
    }

    private fun onKeyPress(key: TextFieldView.KeyTypes, isShiftPressed: Boolean, isControlPressed: Boolean) {
        if (isControlPressed) {
            when (key) {
                TextFieldView.KeyTypes.C -> this.textModel.copySelected()
                TextFieldView.KeyTypes.V -> this.textModel.paste()
                TextFieldView.KeyTypes.X -> this.textModel.cut()
            }
        } else if (isShiftPressed) {
            if (!this.textModel.isSelecting)
                this.textModel.startSelect()

            when (key) {
                TextFieldView.KeyTypes.UP -> this.textModel.moveSelectRow(-1)
                TextFieldView.KeyTypes.DOWN -> this.textModel.moveSelectRow(1)
                TextFieldView.KeyTypes.LEFT -> this.textModel.moveSelectColumn(-1)
                TextFieldView.KeyTypes.RIGHT -> this.textModel.moveSelectColumn(1)
                TextFieldView.KeyTypes.HOME -> this.textModel.moveSelectColumnToBegin()
                TextFieldView.KeyTypes.END -> this.textModel.moveSelectColumnToEnd()
            }
        } else {

            this.textModel.stopSelect()
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

        updateTextFieldView()
    }

    private fun onTextChanged(str: String) {
        this.textModel.addText(str)
        updateTextFieldView()
    }

    private fun onCursorChanged(textCoordinate: TextCoordinate) {
        this.textModel.setCursor(textCoordinate)
        this.textModel.stopSelect()
        updateTextFieldView()
    }

    private fun onCursorDragged(origin: TextCoordinate, current: TextCoordinate) {
        this.textModel.setSelectCoordinates(origin, current)
        updateTextFieldView()
    }

    private fun updateTextFieldView() {
        this.textFieldView.setTokenizedText(this.textModel.getTokenizedText(), this.textModel.currentText, TextCoordinate(this.textModel.cursorRow, this.textModel.cursorColumn))
        this.textFieldView.setSelect(this.textModel.selectCoordinates, TextCoordinate(this.textModel.cursorRow, this.textModel.cursorColumn))
    }
}