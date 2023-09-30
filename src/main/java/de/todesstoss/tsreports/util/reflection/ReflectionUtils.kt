package de.todesstoss.tsreports.util.reflection

object ReflectionUtils {

    fun <T> setField(
        obj: Any,
        fieldName: String,
        value: T
    ) {
        try {
            val field = obj.javaClass.getDeclaredField( fieldName )
            val accessible = field.isAccessible

            field.isAccessible = true
            field.set( obj, value )
            field.isAccessible = accessible
        } catch (ex: Exception) {
            throw NullPointerException( ex.message )
        }
    }

}