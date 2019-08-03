package easydone.core.utils

import java.util.Date
import java.util.concurrent.TimeUnit
import kotlin.math.abs


fun Date.daysBetween(date: Date): Int =
    TimeUnit.DAYS.convert(abs(time - date.time), TimeUnit.MILLISECONDS).toInt()