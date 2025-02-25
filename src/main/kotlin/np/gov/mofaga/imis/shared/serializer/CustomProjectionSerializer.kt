package np.gov.mofaga.imis.shared.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import np.gov.mofaga.imis.shared.projection.EntityProjection

class CustomProjectionSerializer : JsonSerializer<EntityProjection<*>>() {
    override fun serialize(value: EntityProjection<*>, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeStartObject()
        value.getRawData().forEach { (key, value) ->
            gen.writeObjectField(key, value)
        }
        gen.writeEndObject()
    }
}
