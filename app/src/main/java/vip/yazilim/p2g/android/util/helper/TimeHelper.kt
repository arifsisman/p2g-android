package vip.yazilim.p2g.android.util.helper

import android.annotation.SuppressLint
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat

/**
 * @author mustafaarifsisman - 05.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class TimeHelper {
    companion object {
        fun LocalDateTime.getFormattedFull(): String {
            return DateTimeFormatter.ofPattern("dd MMMM uuuu").format(this)
        }

        fun LocalDateTime.getFormattedCompact(): String {
            return DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm").format(this)
        }

        fun ZonedDateTime.getFormattedCompact(): String {
            return DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm").format(this)
        }

        @SuppressLint("SimpleDateFormat")
        fun Int.getHumanReadableTimestamp(): String {
            return (SimpleDateFormat("mm:ss")).format(this)
        }

        fun getLocalDateTimeZonedUTC(): LocalDateTime {
            return LocalDateTime.now(Clock.systemUTC())
        }

        fun getLocalDateTime(): LocalDateTime {
            return LocalDateTime.now(Clock.systemDefaultZone())
        }

        fun LocalDateTime.toZonedDateTime(): ZonedDateTime {
            return this.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault())
        }
    }
}