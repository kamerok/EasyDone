package easydone.core.database

import easydone.core.database.model.EntityField
import easydone.core.domain.model.Markers
import easydone.core.domain.model.Task
import java.time.LocalDate

fun EntityField.getMapper(): Mapper = when (this) {
    EntityField.TYPE -> TypeMapper
    EntityField.TITLE -> StringMapper
    EntityField.DESCRIPTION -> StringMapper
    EntityField.DUE_DATE -> DateMapper
    EntityField.MARKERS -> MarkersMapper
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
        (value as? LocalDate)?.let { DateColumnAdapter.encode(it) } ?: ""

    override fun toValue(string: String): Any? =
        if (string.isEmpty()) null else DateColumnAdapter.decode(string)
}

object MarkersMapper : Mapper {
    override fun toString(value: Any?): String =
        (value as? Markers)?.let {
            (if (it.isUrgent) "1" else "0") + (if (it.isImportant) "1" else "0")
        } ?: ""

    override fun toValue(string: String): Any? =
        if (string.isEmpty()) null else Markers(
            isUrgent = string.getOrNull(0) == '1',
            isImportant = string.getOrNull(1) == '1'
        )
}
