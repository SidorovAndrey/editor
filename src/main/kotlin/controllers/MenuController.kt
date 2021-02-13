package controllers

import models.FileModel
import views.MenuView
import java.io.File

class MenuController(private val fileModel: FileModel, menuView: MenuView) {
    init {
        menuView.onFileSet(this) { file -> this.onFileSet(file) }
    }

    private fun onFileSet(file: File) {
        fileModel.setFile(file)
    }
}