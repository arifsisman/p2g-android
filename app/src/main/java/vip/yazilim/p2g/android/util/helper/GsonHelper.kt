package vip.yazilim.p2g.android.util.helper

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

/**
 * @author mustafaarifsisman - 23.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
object GsonHelper {
    fun gsonWithLocalDateTimeFormatter(): Gson {
        return GsonBuilder().registerTypeAdapter(
            LocalDateTime::class.java,
            JsonDeserializer<Any?> { json, _, _ ->
                val instant: Instant = Instant.ofEpochMilli(json.asJsonPrimitive.asLong)
                LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            }).create()
    }
}