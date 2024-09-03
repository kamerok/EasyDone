package easydone.core.database

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.db.SqlDriver


object DatabaseFactory {

    fun create(driver: SqlDriver) = Database(
        driver,
        Change.Adapter(EnumColumnAdapter()),
        Delta.Adapter(EnumColumnAdapter()),
        Task.Adapter(EnumColumnAdapter(), DateColumnAdapter)
    )

}
