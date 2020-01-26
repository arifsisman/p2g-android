package vip.yazilim.p2g.android.util.gson

import com.google.gson.*
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import java.lang.reflect.Type

class LocalTimeConverter : JsonSerializer<LocalTime?>,
    JsonDeserializer<LocalTime> {
    override fun serialize(
        src: LocalTime?,
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
    ): LocalTime {
        return FORMATTER.parse(
            json.asString,
            LocalTime.FROM
        )
    }

    companion object {
        private val FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME
    }
}