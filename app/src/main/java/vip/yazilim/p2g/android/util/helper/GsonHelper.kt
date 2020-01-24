package vip.yazilim.p2g.android.util.helper

import com.google.gson.*
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import java.lang.reflect.Type
import java.text.ParseException


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

    object DateDeserializer : JsonDeserializer<LocalDateTime?> {
        @Throws(JsonParseException::class)
        override fun deserialize(
            jsonElement: JsonElement?,
            typeOF: Type?,
            context: JsonDeserializationContext?
        ): LocalDateTime? {
            if (jsonElement == null) return null
            val dateStr = jsonElement.asString
            try {
                return LocalDateTime.parse(dateStr)
            } catch (ex: ParseException) {
                ex.printStackTrace()
            }
            return null
        }
    }
}