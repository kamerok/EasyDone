package easydone.core.database

import easydone.core.model.EntityField


data class ChangeEntry(
    val changeId: Long,
    val entityName: EntityName,
    val entityId: String,
    val fields: Map<EntityField, Any>
)