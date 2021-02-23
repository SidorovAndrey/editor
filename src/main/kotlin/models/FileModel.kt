package models

import events.Event
import events.EventProducer
import java.io.File

class FileModel : EventProducer<FileModel.FileSetCallback> {
    private var file: File? = null
    private val onFileSetEvent: Event<FileSetCallback> = Event()

    fun interface FileSetCallback {
        fun onSet(file: File)
    }

    override fun subscribe(who: Any, callback: FileSetCallback) {
        this.onFileSetEvent.subscribe(who, callback)
    }

    override fun <FileSetCallback> unsubscribe(who: Any) {
        this.onFileSetEvent.unsubscribe(who)
    }

    fun setFile(file: File) {
        this.file = file
        for (c: FileSetCallback in this.onFileSetEvent.getSubscribers()) {
            c.onSet(file)
        }
    }
}