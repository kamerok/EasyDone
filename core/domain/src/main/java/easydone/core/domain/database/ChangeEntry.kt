package easydone.core.domain.database

data class ChangeEntry(
    val changeId: Long,
    val entityName: EntityName,
    val entityId: String,
    val fields: Map<EntityField, Any?>
)
