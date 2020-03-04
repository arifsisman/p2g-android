package vip.yazilim.p2g.android.util.helper

import android.text.format.DateUtils
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter

/**
 * @author mustafaarifsisman - 05.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class TimeHelper {
    companion object {
        fun ZonedDateTime.getFormattedFull(): String {
            return DateTimeFormatter.ofPattern("dd MMMM uuuu").format(this)
        }

        fun ZonedDateTime.getFormattedCompact(): String {
            return DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm").format(this)
        }

        fun Int.getHumanReadableTimestamp(): String {
            return DateUtils.formatElapsedTime(this / 1000L)
        }

        fun getLocalDateTimeZonedUTC(): LocalDateTime {
            return LocalDateTime.now(Clock.systemUTC())
        }

        fun LocalDateTime.toZonedDateTime(): ZonedDateTime {
            return this.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault())
        }
    }
}