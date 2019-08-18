package easydone.core.database

import easydone.core.model.Task
import java.util.Date

fun EntityField.getMapper(): Mapper = when (this) {
    EntityField.TYPE -> TypeMapper
    EntityField.TITLE -> StringMapper
    EntityField.DESCRIPTION -> StringMapper
    EntityField.DUE_DATE -> DateMapper
    EntityField.IS_DONE -> BooleanMapper
}

interface Mapper {
    fun toString(value: Any?): String
    fun toValue(string: String): Any?
}

object TypeMapper : Mapper {
    override fun toString(value: Any?): String = (value as? Task.Type)?.name ?: ""
    override fun toValue(string: String): Any? =
        if (string.isEmpty()) null else Task.Type.valueOf(string)
}

object BooleanMapper : Mapper {
    override fun toString(value: Any?): String = value?.let { value.toString() } ?: ""
    override fun toValue(string: String): Any? = if (string.isEmpty()) null else string.toBoolean()
}

object StringMapper : Mapper {
    override fun toString(value: Any?): String =
        (value as? String).let { if (it.isNullOrEmpty()) "" else it }

    override fun toValue(string: String): Any? = if (string.isEmpty()) null else string
}

object DateMapper : Mapper {
    override fun toString(value: Any?): String =
        (value as? Date)?.let { DateColumnAdapter.encode(it) } ?: ""

    override fun toValue(string: String): Any? =
        if (string.isEmpty()) null else DateColumnAdapter.decode(string)
}
