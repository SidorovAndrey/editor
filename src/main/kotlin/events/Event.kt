package events

class Event<TCallback> {
    private val subscribers: MutableMap<String, TCallback> = mutableMapOf()

    fun subscribe(who: Any, callback: TCallback) {
        subscribers[who::class.qualifiedName!!] = callback
    }

    fun unsubscribe(who: Any) {
        subscribers.remove(who::class.qualifiedName)
    }

    fun getSubscribers(): Collection<TCallback> {
        return subscribers.values
    }
}