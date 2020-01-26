package vip.yazilim.p2g.android.util.gson

import com.google.gson.*
import org.threeten.bp.OffsetTime
import org.threeten.bp.format.DateTimeFormatter
import java.lang.reflect.Type

class OffsetTimeConverter : JsonSerializer<OffsetTime?>,
    JsonDeserializer<OffsetTime> {
    override fun serialize(
        src: OffsetTime?,
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
    ): OffsetTime {
        return FORMATTER.parse(
            json.asString,
            OffsetTime.FROM
        )
    }

    companion object {
        private val FORMATTER = DateTimeFormatter.ISO_OFFSET_TIME
    }
}