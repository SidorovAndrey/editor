package views

import ViewRepository
import components.TextFieldComponent
import events.Event
import events.EventProducer
import utils.InputTimer
import java.awt.event.*
import java.util.*
import javax.swing.JFrame
import kotlin.concurrent.schedule

class TextFieldView(
    viewRepository: ViewRepository,
    frame : JFrame
) : BaseView(viewRepository) {

    private val textField = TextFieldComponent()
    private val onHeightChangedEvent: Event<OnResizedCallback> = Event()
    private val onControlKeyPressedEvent: Event<OnControlKeyPressed> = Event()
    private val onTextChangedEvent: Event<OnTextChanged> = Event()

    private var inputTimer: InputTimer? = null

    var currentHeight: Int = textField.height
        private set

    init {
        frame.add(textField)

        textField.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                if (currentHeight != e!!.component.height && e.component.height > 1) {
                    currentHeight = e.component.height
                    for (c in onHeightChangedEvent.getSubscribers()) {
                        c.set(currentHeight / textField.lineHeight)
                    }
                }
            }
        })

        textField.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyChar.isLetterOrDigit() || e.keyChar.isWhitespace()) {
                    handleChar(e.keyChar)
                } else {
                    val key = when (e.keyCode) {
                        KeyEvent.VK_UP -> KeyTypes.UP
                        KeyEvent.VK_DOWN -> KeyTypes.DOWN
                        KeyEvent.VK_LEFT -> KeyTypes.LEFT
                        KeyEvent.VK_RIGHT -> KeyTypes.RIGHT
                        KeyEvent.VK_HOME -> KeyTypes.HOME
                        KeyEvent.VK_END -> KeyTypes.END
                        KeyEvent.VK_PAGE_UP -> KeyTypes.PAGE_UP
                        KeyEvent.VK_PAGE_DOWN -> KeyTypes.PAGE_DOWN
                        else -> KeyTypes.NONE
                    }

                    for (s in onControlKeyPressedEvent.getSubscribers()) {
                        s.handle(key, e.keyChar)
                    }
                }
            }
        })
    }

    enum class KeyTypes {
        NONE,
        UP,
        DOWN,
        LEFT,
        RIGHT,
        HOME,
        END,
        PAGE_UP,
        PAGE_DOWN
    }

    fun interface OnControlKeyPressed {
        fun handle(key: KeyTypes, char: Char)
    }

    fun asOnControlKeyPressEventProducer(): EventProducer<OnControlKeyPressed> {
        return object : EventProducer<OnControlKeyPressed> {
            override fun subscribe(who: Any, callback: OnControlKeyPressed) {
                onControlKeyPressedEvent.subscribe(who, callback)
            }

            override fun unsubscribe(who: Any) {
                onControlKeyPressedEvent.unsubscribe(who)
            }
        }
    }

    fun interface OnTextChanged {
        fun handle(string: String)
    }

    fun asOnTextChangedEventProducer(): EventProducer<OnTextChanged> {
        return object : EventProducer<OnTextChanged> {
            override fun subscribe(who: Any, callback: OnTextChanged) {
                onTextChangedEvent.subscribe(who, callback)
            }

            override fun unsubscribe(who: Any) {
                onTextChangedEvent.unsubscribe(who)
            }
        }
    }

    fun interface OnResizedCallback {
        fun set(height: Int)
    }

    fun asOnResizedEventProducer(): EventProducer<OnResizedCallback> {
        return object : EventProducer<OnResizedCallback> {
            override fun subscribe(who: Any, callback: OnResizedCallback) {
                onHeightChangedEvent.subscribe(who, callback)
            }

            override fun unsubscribe(who: Any) {
                onHeightChangedEvent.unsubscribe(who)
            }
        }
    }

    fun setText(text: MutableList<String>, row: Int, column: Int) {
        textField.setText(text, row, column)
    }

    private fun handleChar(ch: Char) {
        if (inputTimer == null || inputTimer!!.finished) {
            inputTimer = InputTimer(500L) {
                for (s in onTextChangedEvent.getSubscribers()) {
                    s.handle(it)
                }
            }
        }

        inputTimer!!.add(ch)
        textField.addChar(ch)
    }
}