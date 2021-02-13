import views.BaseView
import kotlin.reflect.KClass
import kotlin.reflect.cast

class ViewRepository {
    private val views: MutableMap<String, BaseView> = mutableMapOf()

    operator fun <T : BaseView> get(cl: KClass<T>): T {
        if (!views.containsKey(cl.qualifiedName))
            throw IllegalArgumentException("Object not found for class ${cl.qualifiedName}")

        return cl.cast(views[cl.qualifiedName])
    }

    operator fun <T : BaseView> set(cl: KClass<T>, value: BaseView) {
        if (views.containsKey(cl.qualifiedName))
            throw IllegalArgumentException("Object for class ${cl.qualifiedName} already exists")

        views[cl.qualifiedName!!] = value
    }

    fun <T : BaseView> delete(cl: KClass<T>): T? {
        val res = views.remove(cl.qualifiedName) ?: return null
        return cl.cast(res)
    }
}