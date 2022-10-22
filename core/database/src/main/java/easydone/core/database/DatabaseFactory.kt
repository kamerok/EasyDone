package easydone.core.database

import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.db.SqlDriver

object DatabaseFactory {

    fun create(driver: SqlDriver) = Database(
        driver,
        Change.Adapter(EnumColumnAdapter()),
        Delta.Adapter(EnumColumnAdapter()),
        Task.Adapter(EnumColumnAdapter(), DateColumnAdapter)
    )

}
