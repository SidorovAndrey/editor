package utils

import java.util.*

class InputTimer(delay: Long, private val callback: InputTimerCallback) {
    private val input: StringBuilder = StringBuilder()
    private var timer: Timer = Timer()
    @Volatile private var changed = false

    var finished: Boolean = false
        private set

    var string: String? = null
        private set

    init {
        // TODO: think about using coroutines and ticker channel
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                onTime()
            }
        }, 0, delay)
    }

    fun interface InputTimerCallback {
        fun call(string: String)
    }

    fun add(ch: Char) {
        input.append(ch)
        changed = true
    }

    fun finish() {
        val str = input.toString()
        string = str
        callback.call(str)
        finished = true
        changed = false
    }

    private fun onTime() {
        if (!finished && !changed) {
            val str = input.toString()
            string = str
            callback.call(str)
            finished = true
        }

        changed = false
    }
}