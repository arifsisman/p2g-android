package vip.yazilim.p2g.android.util.helper

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import org.joda.time.DateTime
import java.lang.reflect.Type
import java.text.ParseException


/**
 * @author mustafaarifsisman - 23.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
object GsonHelper {
    object DateDeserializer : JsonDeserializer<DateTime?> {
        @Throws(JsonParseException::class)
        override fun deserialize(
            jsonElement: JsonElement?,
            typeOF: Type?,
            context: JsonDeserializationContext?
        ): DateTime? {
            if (jsonElement == null) return null
            val dateStr = jsonElement.asString
            try {
                return DateTime.parse(dateStr)
            } catch (ex: ParseException) {
                ex.printStackTrace()
            }
            return null
        }
    }
}