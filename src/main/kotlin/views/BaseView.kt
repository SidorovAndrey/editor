package views

import ViewRepository

open class BaseView(viewRepository: ViewRepository) {
    init {
        // want to have virtual this access here
        @Suppress("LeakingThis")
        viewRepository[this::class] = this
    }
}
