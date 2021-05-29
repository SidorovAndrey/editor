package views

import SelectCoordinates
import TextCoordinate
import ViewRepository
import components.TextFieldComponent
import events.Event
import events.EventProducer
import tokenizer.Token
import utils.InputTimer
import utils.getBracketCoordinates
import java.awt.Event.UP
import java.awt.Point
import java.awt.event.*
import javax.swing.JFrame
import javax.swing.SwingUtilities

class TextFieldView(
    viewRepository: ViewRepository,
    frame : JFrame
) : BaseView(viewRepository) {

    private val textField = TextFieldComponent()
    private val onHeightChangedEvent: Event<OnResizedCallback> = Event()
    private val onControlKeyPressedEvent: Event<OnControlKeyPressed> = Event()
    private val onTextChangedEvent: Event<OnTextChanged> = Event()
    private val onCursorChangedEvent: Event<OnCursorChanged> = Event()
    private val onCursorDraggedEvent: Event<OnCursorDragged> = Event()

    private var inputTimer: InputTimer? = null

    private var mousePressedPoint: Point? = null

    var currentHeight: Int = textField.height
        private set

    init {
        frame.add(textField)

        textField.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                if (currentHeight != e!!.component.height && e.component.height > 1) {
                    currentHeight = e.component.height
                    for (c in onHeightChangedEvent.getSubscribers()) {
                        c.set((currentHeight / textField.lineHeight) - 1)
                    }
                }
            }
        })

        textField.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (isSymbol(e.keyChar)) {
                    handleChar(e.keyChar)
                } else {
                    if (inputTimer != null && !inputTimer!!.finished)
                        inputTimer!!.finish()

                    val key = when (e.keyCode) {
                        KeyEvent.VK_UP -> KeyTypes.UP
                        KeyEvent.VK_DOWN -> KeyTypes.DOWN
                        KeyEvent.VK_LEFT -> KeyTypes.LEFT
                        KeyEvent.VK_RIGHT -> KeyTypes.RIGHT

                        KeyEvent.VK_HOME -> KeyTypes.HOME
                        KeyEvent.VK_END -> KeyTypes.END
                        KeyEvent.VK_PAGE_UP -> KeyTypes.PAGE_UP
                        KeyEvent.VK_PAGE_DOWN -> KeyTypes.PAGE_DOWN

                        KeyEvent.VK_BACK_SPACE -> KeyTypes.BACK_SPACE
                        KeyEvent.VK_DELETE -> KeyTypes.DELETE
                        KeyEvent.VK_ENTER -> KeyTypes.ENTER

                        KeyEvent.VK_C -> KeyTypes.C
                        KeyEvent.VK_V -> KeyTypes.V
                        KeyEvent.VK_X -> KeyTypes.X
                        else -> KeyTypes.NONE
                    }

                    for (s in onControlKeyPressedEvent.getSubscribers()) {
                        s.handle(key, e.isShiftDown, e.isControlDown)
                    }
                }
            }
        })

        textField.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    val row = e!!.y / textField.lineHeight
                    val column = (e.x / textField.charWidth) - 1
                    for (s in onCursorChangedEvent.getSubscribers()) {
                        s.update(TextCoordinate(row, column))
                    }
                }
            }

            override fun mousePressed(e: MouseEvent?) {
                if (SwingUtilities.isLeftMouseButton(e) && mousePressedPoint == null) {
                    mousePressedPoint = e!!.point
                }
            }

            override fun mouseReleased(e: MouseEvent?) {
                if (SwingUtilities.isLeftMouseButton(e) && mousePressedPoint != null) {
                    mousePressedPoint = null
                }
            }
        })

        textField.addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseDragged(e: MouseEvent?) {
                val originPoint = mousePressedPoint
                if (SwingUtilities.isLeftMouseButton(e) && originPoint != null) {
                    val originRow = originPoint.y / textField.lineHeight
                    val originColumn = (originPoint.x / textField.charWidth) - 1
                    val row = e!!.y / textField.lineHeight
                    val column = (e.x / textField.charWidth) - 1
                    for (s in onCursorDraggedEvent.getSubscribers()) {
                        s.update(TextCoordinate(originRow, originColumn), TextCoordinate(row, column))
                    }
                }
            }
        })

        textField.requestFocus()
    }

    private fun isSymbol(ch: Char): Boolean {
        return ch.toInt() in 32..126
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
        BACK_SPACE,
        DELETE,
        ENTER,
        C,
        V,
        X
    }

    fun interface OnControlKeyPressed {
        fun handle(key: KeyTypes, isShiftPressed: Boolean, isControlDown: Boolean)
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

    fun interface OnCursorChanged {
        fun update(textCoordinate: TextCoordinate)
    }

    fun asOnCursorChangedEventProducer(): EventProducer<OnCursorChanged> {
        return object : EventProducer<OnCursorChanged> {
            override fun subscribe(who: Any, callback: OnCursorChanged) {
                onCursorChangedEvent.subscribe(who, callback)
            }

            override fun unsubscribe(who: Any) {
                onCursorChangedEvent.unsubscribe(who)
            }
        }
    }

    fun interface OnCursorDragged {
        fun update(origin: TextCoordinate, current: TextCoordinate)
    }

    fun asOnCursorDraggedEventProducer(): EventProducer<OnCursorDragged> {
        return object : EventProducer<OnCursorDragged> {
            override fun subscribe(who: Any, callback: OnCursorDragged) {
                onCursorDraggedEvent.subscribe(who, callback)
            }

            override fun unsubscribe(who: Any) {
                onCursorDraggedEvent.unsubscribe(who)
            }
        }
    }

    fun setTokenizedText(text: MutableList<Token>, rawText: MutableList<String>, textCoordinate: TextCoordinate) {
        val bracketCoordinates = getBracketCoordinates(rawText, textCoordinate)
        textField.setText(text, rawText, textCoordinate, bracketCoordinates)
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

    fun setSelect(selectCoordinates: SelectCoordinates, textCoordinate: TextCoordinate) {
        textField.setSelect(selectCoordinates, textCoordinate)
    }
}