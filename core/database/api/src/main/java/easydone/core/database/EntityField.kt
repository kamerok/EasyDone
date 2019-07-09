package easydone.core.database

import easydone.core.model.Task

enum class EntityField {
    TYPE, TITLE, DESCRIPTION, IS_DONE;

    fun getMapper(): Mapper = when (this) {
        TYPE -> TypeMapper
        TITLE -> StringMapper
        DESCRIPTION -> StringMapper
        IS_DONE -> BooleanMapper
    }
}

interface Mapper {
    fun toString(value: Any): String
    fun toValue(string: String): Any
}

object TypeMapper : Mapper {
    override fun toString(value: Any): String = (value as Task.Type).name
    override fun toValue(string: String): Any = Task.Type.valueOf(string)
}

object BooleanMapper : Mapper {
    override fun toString(value: Any): String = value.toString()
    override fun toValue(string: String): Any = string.toBoolean()
}

object StringMapper : Mapper {
    override fun toString(value: Any): String = value as String
    override fun toValue(string: String): Any = string
}