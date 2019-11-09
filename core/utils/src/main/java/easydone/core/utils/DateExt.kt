package easydone.core.utils

import org.threeten.bp.Instant
import org.threeten.bp.Period
import org.threeten.bp.ZoneId
import java.util.Date


fun Date.daysFrom(date: Date): Int {
    val leftDate = Instant.ofEpochMilli(date.time).atZone(ZoneId.systemDefault()).toLocalDate()
    val rightDate = Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDate()
    val period = Period.between(leftDate, rightDate)
    return period.days
}
