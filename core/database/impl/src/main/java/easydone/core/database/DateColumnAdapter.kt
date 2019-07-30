package easydone.core.database

import com.squareup.sqldelight.ColumnAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


object DateColumnAdapter : ColumnAdapter<Date, String> {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override fun decode(databaseValue: String): Date = dateFormat.parse(databaseValue)

    override fun encode(value: Date): String = dateFormat.format(value)
}