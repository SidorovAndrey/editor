package events

interface EventProducer<TCallback> {
    fun subscribe(who: Any, callback: TCallback)
    fun <TCallback> unsubscribe(who: Any)
}