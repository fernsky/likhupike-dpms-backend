package np.gov.mofaga.imis.shared.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import np.gov.mofaga.imis.location.api.dto.enums.ProvinceField
import np.gov.mofaga.imis.location.api.dto.enums.WardField
import np.gov.mofaga.imis.location.api.dto.response.DynamicProvinceProjection
import np.gov.mofaga.imis.location.api.dto.response.DynamicWardProjection
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

@Configuration
class JacksonConfig {
    @Bean
    fun objectMapper(): ObjectMapper =
        ObjectMapper().apply {
            registerModule(JavaTimeModule())
            registerModule(KotlinModule.Builder().build())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            findAndRegisterModules()
            registerModule(customModule())
        }

    @Bean
    fun mappingJackson2HttpMessageConverter(objectMapper: ObjectMapper): MappingJackson2HttpMessageConverter =
        MappingJackson2HttpMessageConverter(objectMapper)

    @Bean
    fun customModule(): SimpleModule {
        val module = SimpleModule()
        module.addSerializer(DynamicProvinceProjection::class.java, DynamicProvinceProjectionSerializer())
        module.addSerializer(DynamicWardProjection::class.java, DynamicWardProjectionSerializer())
        return module
    }

    private class DynamicProvinceProjectionSerializer : StdSerializer<DynamicProvinceProjection>(DynamicProvinceProjection::class.java) {
        override fun serialize(
            value: DynamicProvinceProjection,
            gen: JsonGenerator,
            provider: SerializerProvider,
        ) {
            gen.writeStartObject()
            ProvinceField.values().forEach { field ->
                value.getValue(field)?.let {
                    gen.writeObjectField(field.toJsonFieldName(), it)
                }
            }
            gen.writeEndObject()
        }
    }

    private class DynamicWardProjectionSerializer : StdSerializer<DynamicWardProjection>(DynamicWardProjection::class.java) {
        private val logger = LoggerFactory.getLogger(this::class.java)

        override fun serialize(
            value: DynamicWardProjection,
            gen: JsonGenerator,
            provider: SerializerProvider,
        ) {
            gen.writeStartObject()
            WardField.values().forEach { field ->
                value.getValue(field)?.let {
                    val jsonFieldName = field.toJsonFieldName()
                    logger.debug("Serializing field: ${field.name} as JSON field: $jsonFieldName with value: $it")
                    gen.writeObjectField(jsonFieldName, it)
                }
            }
            gen.writeEndObject()
        }
    }
}
