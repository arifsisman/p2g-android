package vip.yazilim.p2g.android.util.gson

import com.google.gson.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.lang.reflect.Type

class LocalDateConverter : JsonSerializer<LocalDate?>,
    JsonDeserializer<LocalDate> {
    override fun serialize(
        src: LocalDate?,
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
    ): LocalDate {
        return FORMATTER.parse(
            json.asString,
            LocalDate.FROM
        )
    }

    companion object {
        private val FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE
    }
}