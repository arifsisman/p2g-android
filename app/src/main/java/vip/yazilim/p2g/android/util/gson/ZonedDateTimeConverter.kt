package vip.yazilim.p2g.android.util.gson

import com.google.gson.*
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.lang.reflect.Type

class ZonedDateTimeConverter : JsonSerializer<ZonedDateTime?>,
    JsonDeserializer<ZonedDateTime> {
    override fun serialize(
        src: ZonedDateTime?,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(FORMATTER.format(src))
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): ZonedDateTime {
        return FORMATTER.parse(
            json.asString,
            ZonedDateTime.FROM
        )
    }

    companion object {
        private val FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME
    }
}