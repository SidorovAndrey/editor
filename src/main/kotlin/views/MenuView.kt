package views

import ViewRepository
import events.Event
import events.EventProducer
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.File
import javax.swing.*

class MenuView (
    viewRepository: ViewRepository,
    private val frame: JFrame
) : BaseView(viewRepository) {

    private val menuBar = JMenuBar()
    private val onFileSetEvent: Event<FileSetCallback> = Event()
    private val onFileSaveEvent: Event<FileSaveCallback> = Event()

    init {
        val fileMenu = JMenu("File")

        val openMenuItem = JMenuItem("Open")
        fileMenu.add(openMenuItem)

        val saveMenuItem = JMenuItem("Save")
        fileMenu.add(saveMenuItem)

        val eventListener = MenuEventListener(openMenuItem, saveMenuItem)
        openMenuItem.addActionListener(eventListener)
        saveMenuItem.addActionListener(eventListener)

        menuBar.add(fileMenu)
        frame.jMenuBar = menuBar
    }

    fun interface FileSetCallback {
        fun onSet(file: File)
    }

    fun asOnFileSetEventProducer(): EventProducer<FileSetCallback> {
        return object : EventProducer<FileSetCallback> {
            override fun subscribe(who: Any, callback: FileSetCallback) {
                onFileSetEvent.subscribe(who, callback)
            }

            override fun unsubscribe(who: Any) {
                onFileSetEvent.unsubscribe(who)
            }
        }
    }

    fun interface FileSaveCallback {
        fun onSave(file: File)
    }

    fun asOnFileSaveEventProducer(): EventProducer<FileSaveCallback> {
        return object : EventProducer<FileSaveCallback> {
            override fun subscribe(who: Any, callback: FileSaveCallback) {
                onFileSaveEvent.subscribe(who, callback)
            }

            override fun unsubscribe(who: Any) {
                onFileSaveEvent.unsubscribe(who)
            }
        }
    }

    inner class MenuEventListener(
        private val openMenuItem: JMenuItem,
        private val saveMenuItem: JMenuItem) : ActionListener {

        override fun actionPerformed(p0: ActionEvent?) {
            if (p0?.source == openMenuItem) {
                val fileChooser = JFileChooser()
                fileChooser.currentDirectory = File(System.getProperty("user.home"))
                val result = fileChooser.showOpenDialog(frame)
                if (result == JFileChooser.APPROVE_OPTION) {
                    this@MenuView.setFile(fileChooser.selectedFile)
                }
            }

            if (p0?.source == saveMenuItem) {
                val fileChooser = JFileChooser()
                fileChooser.currentDirectory = File(System.getProperty("user.home"))
                val result = fileChooser.showSaveDialog(frame)
                if (result == JFileChooser.APPROVE_OPTION) {
                    this@MenuView.saveFile(fileChooser.selectedFile)
                }
            }
        }
    }

    private fun setFile(file: File) {
        for (c in this.onFileSetEvent.getSubscribers()) {
            c.onSet(file)
        }
    }

    private fun saveFile(file: File) {
        for (c in this.onFileSaveEvent.getSubscribers()) {
            c.onSave(file)
        }
    }
}