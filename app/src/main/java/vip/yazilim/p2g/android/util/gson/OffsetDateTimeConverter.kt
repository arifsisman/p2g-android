package vip.yazilim.p2g.android.util.gson

import com.google.gson.*
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.lang.reflect.Type

class OffsetDateTimeConverter : JsonSerializer<OffsetDateTime?>,
    JsonDeserializer<OffsetDateTime> {
    override fun serialize(
        src: OffsetDateTime?,
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
    ): OffsetDateTime {
        return FORMATTER.parse(
            json.asString,
            OffsetDateTime.FROM
        )
    }

    companion object {
        private val FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    }
}