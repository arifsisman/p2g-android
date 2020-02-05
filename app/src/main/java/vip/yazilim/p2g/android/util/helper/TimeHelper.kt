package vip.yazilim.p2g.android.util.helper

import org.threeten.bp.format.DateTimeFormatter

/**
 * @author mustafaarifsisman - 05.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
class TimeHelper {
    companion object{
        val dateTimeFormatterFull = DateTimeFormatter.ofPattern("dd MMMM uuuu")
    }
}