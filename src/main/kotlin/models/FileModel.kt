package models

import events.Event
import java.io.File

class FileModel {
    private var file: File? = null
    private val onFileSetEvent: Event<FileSetCallback> = Event()

    fun interface FileSetCallback {
        fun onSet(file: File)
    }

    fun onFileSet(who: Any, callback: FileSetCallback) {
        this.onFileSetEvent.subscribe(who, callback)
    }

    fun unsubscribeOnFileSet(who: Any) {
        this.onFileSetEvent.unsubscribe(who)
    }

    fun setFile(file: File) {
        this.file = file
        for (c: FileSetCallback in this.onFileSetEvent.getSubscribers()) {
            c.onSet(file)
        }
    }
}