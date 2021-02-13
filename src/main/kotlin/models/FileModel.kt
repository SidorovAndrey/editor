package models

import events.Event
import java.io.File

class FileModel {
    private var file: File? = null
    private val onFileSet: Event<FileSetCallback> = Event()

    fun interface FileSetCallback {
        fun onSet(file: File)
    }

    fun onFileSet(who: Any, callback: FileSetCallback) {
        this.onFileSet.subscribe(who, callback)
    }

    fun unsubscribeOnFileSet(who: Any) {
        this.onFileSet.unsubscribe(who)
    }

    fun setFile(file: File) {
        this.file = file
        for (c in this.onFileSet.getSubscribers()) {
            c.onSet(file)
        }
    }
}