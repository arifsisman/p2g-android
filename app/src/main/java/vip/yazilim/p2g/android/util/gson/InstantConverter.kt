package vip.yazilim.p2g.android.util.gson

import com.google.gson.*
import org.threeten.bp.Instant
import org.threeten.bp.format.DateTimeFormatter
import java.lang.reflect.Type

/**
 * @author mustafaarifsisman - 25.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
class InstantConverter : JsonSerializer<Instant?>, JsonDeserializer<Instant?> {
    override fun serialize(
        src: Instant?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(FORMATTER.format(src))
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Instant {
        return FORMATTER.parse(json.asString, Instant.FROM)
    }

    companion object {
        private val FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT
    }
}