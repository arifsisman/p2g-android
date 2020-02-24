package vip.yazilim.p2g.android.util.helper

import android.annotation.SuppressLint
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat

/**
 * @author mustafaarifsisman - 05.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class TimeHelper {
    companion object {
        val dateTimeFormatterFull: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM uuuu")
        val dateTimeFormatterCompact: DateTimeFormatter =
            DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm")

        @SuppressLint("SimpleDateFormat")
        fun getHumanReadableTimestamp(time: Long): String {
            return (SimpleDateFormat("mm:ss")).format(time)
        }
    }
}