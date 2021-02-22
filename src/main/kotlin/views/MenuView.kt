package views

import ViewRepository
import events.Event
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.File
import javax.swing.*

class MenuView (
    viewRepository: ViewRepository,
    private val frame: JFrame
) : BaseView(viewRepository) {

    private val menuBar = JMenuBar()
    private var selectedFile: File? = null
    private val onFileSetEvent: Event<FileSetCallback> = Event()

    init {
        val fileMenu = JMenu("File")

        val openMenuItem = JMenuItem("Open")
        fileMenu.add(openMenuItem)

        val reopenMenuItem = JMenuItem("Reopen")
        fileMenu.add(reopenMenuItem)

        val saveMenuItem = JMenuItem("Save")
        fileMenu.add(saveMenuItem)

        val eventListener = MenuEventListener(openMenuItem, reopenMenuItem, saveMenuItem)
        openMenuItem.addActionListener(eventListener)

        menuBar.add(fileMenu)
        frame.jMenuBar = menuBar
    }

    fun onFileSet(who: Any, callback: FileSetCallback) {
        this.onFileSetEvent.subscribe(who, callback)
    }

    fun unsubscribeOnFileSet(who: Any) {
        this.onFileSetEvent.unsubscribe(who)
    }

    fun interface FileSetCallback {
        fun onSet(file: File)
    }

    inner class MenuEventListener(
        private val openMenuItem: JMenuItem,
        private val reopenMenuItem: JMenuItem,
        private val saveMenuItem: JMenuItem) : ActionListener {

        override fun actionPerformed(p0: ActionEvent?) {
            if (p0?.source == openMenuItem) {
                val fileChooser = JFileChooser();
                fileChooser.currentDirectory = File(System.getProperty("user.home"))
                val result = fileChooser.showOpenDialog(frame)
                if (result == JFileChooser.APPROVE_OPTION) {
                    this@MenuView.setFile(fileChooser.selectedFile)
                }
            }
        }
    }

    private fun setFile(file: File) {
        this.selectedFile = file
        for (c in this.onFileSetEvent.getSubscribers()) {
            c.onSet(file)
        }
    }
}