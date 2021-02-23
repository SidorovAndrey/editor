package views

import ViewRepository
import components.TextField
import events.Event
import events.EventProducer
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JFrame

class TextFieldView(
    viewRepository: ViewRepository,
    frame : JFrame
) : BaseView(viewRepository), EventProducer<TextFieldView.OnResizedCallback> {

    private val textField = TextField()
    private var text: List<String> = mutableListOf()
    private val onHeightChangedEvent: Event<OnResizedCallback> = Event()

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
    }

    fun interface OnResizedCallback {
        fun set(height: Int)
    }

    override fun subscribe(who: Any, callback: OnResizedCallback) {
        onHeightChangedEvent.subscribe(who, callback);
    }

    override fun <OnResizedCallback> unsubscribe(who: Any) {
        onHeightChangedEvent.unsubscribe(who);
    }

    fun setText(text: List<String>) {
        this.text = text
        textField.setText(text)
    }
}