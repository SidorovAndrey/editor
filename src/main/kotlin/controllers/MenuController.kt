package controllers

import TextCoordinate
import models.TextModel
import views.MenuView
import views.TextFieldView
import java.io.File

class MenuController(private val textModel: TextModel, private val textFieldView: TextFieldView, menuView: MenuView) {
    init {
        menuView.asOnFileSetEventProducer().subscribe(this) { file -> onFileSet(file) }
        menuView.asOnFileSaveEventProducer().subscribe(this) { file -> onFileSave(file) }
    }

    private fun onFileSet(file: File) {
        textModel.loadText(file.bufferedReader())
        textFieldView.setTokenizedText(textModel.getTokenizedText(), textModel.currentText, TextCoordinate(textModel.cursorRow, textModel.cursorColumn))
    }

    private fun onFileSave(file: File) {
        file.writeText(textModel.getAllText())
    }
}