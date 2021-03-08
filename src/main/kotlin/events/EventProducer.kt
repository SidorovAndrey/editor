package events

interface EventProducer<TCallback> {
    fun subscribe(who: Any, callback: TCallback)
    fun unsubscribe(who: Any)
}