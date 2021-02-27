package views

import ViewRepository
import components.TextFieldComponent
import events.Event
import events.EventProducer
import java.awt.event.*
import javax.swing.JFrame

class TextFieldView(
    viewRepository: ViewRepository,
    frame : JFrame
) : BaseView(viewRepository) {

    private val textField = TextFieldComponent()
    private val onHeightChangedEvent: Event<OnResizedCallback> = Event()
    private val onKeyPressedEvent: Event<OnKeyPressed> = Event()

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
                val key = when (e.keyCode) {
                    KeyEvent.VK_UP -> KeyTypes.UP
                    KeyEvent.VK_DOWN -> KeyTypes.DOWN
                    KeyEvent.VK_LEFT -> KeyTypes.LEFT
                    KeyEvent.VK_RIGHT -> KeyTypes.RIGHT
                    KeyEvent.VK_HOME -> KeyTypes.HOME
                    KeyEvent.VK_END -> KeyTypes.END
                    KeyEvent.VK_PAGE_UP -> KeyTypes.PAGE_UP
                    KeyEvent.VK_PAGE_DOWN -> KeyTypes.PAGE_DOWN
                    else -> if (e.keyChar.isLetterOrDigit() || e.keyChar.isWhitespace()) KeyTypes.SYMBOL
                        else KeyTypes.NONE
                }

                for (s in onKeyPressedEvent.getSubscribers()) {
                    s.handle(key, e.keyChar)
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
        PAGE_DOWN,
        SYMBOL
    }

    fun interface OnKeyPressed {
        fun handle(key: KeyTypes, char: Char)
    }

    fun asOnKeyPressEventProducer(): EventProducer<OnKeyPressed> {
        return object : EventProducer<OnKeyPressed> {
            override fun subscribe(who: Any, callback: OnKeyPressed) {
                onKeyPressedEvent.subscribe(who, callback)
            }

            override fun <OnKeyPressed> unsubscribe(who: Any) {
                onKeyPressedEvent.unsubscribe(who)
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

            override fun <OnResizedCallback> unsubscribe(who: Any) {
                onHeightChangedEvent.unsubscribe(who)
            }

        }
    }

    fun setText(text: List<String>, row: Int, column: Int) {
        textField.setText(text, row, column)
    }
}