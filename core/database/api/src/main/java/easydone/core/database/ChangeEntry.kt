package easydone.core.database


data class ChangeEntry(
    val changeId: Long,
    val entityName: EntityName,
    val entityId: String,
    val map: Map<EntityField, String>
)