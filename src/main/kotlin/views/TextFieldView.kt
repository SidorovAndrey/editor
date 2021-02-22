package views

import ViewRepository
import components.TextField
import events.Event
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JFrame

class TextFieldView(
    viewRepository: ViewRepository,
    frame : JFrame
) : BaseView(viewRepository) {

    private val textField = TextField()
    var currentHeight: Int = textField.height
        private set

    private var text: List<String> = mutableListOf()
    private val onHeightChangedEvent: Event<OnResizedCallback> = Event()

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
    }

    fun interface OnResizedCallback {
        fun set(height: Int)
    }

    fun onResized(who: Any, callback: OnResizedCallback) {
        onHeightChangedEvent.subscribe(who, callback);
    }

    fun unsubscribeOnResized(who: Any) {
        onHeightChangedEvent.unsubscribe(who);
    }

    fun setText(text: List<String>) {
        this.text = text
        textField.setText(text)
    }
}