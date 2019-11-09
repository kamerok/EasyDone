package easydone.core.database

import com.squareup.sqldelight.ColumnAdapter
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter


object DateColumnAdapter : ColumnAdapter<LocalDate, String> {

    override fun decode(databaseValue: String): LocalDate = LocalDate.parse(databaseValue)

    override fun encode(value: LocalDate): String = value.format(DateTimeFormatter.ISO_LOCAL_DATE)
}
