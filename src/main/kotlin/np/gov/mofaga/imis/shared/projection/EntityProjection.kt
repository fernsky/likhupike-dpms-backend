package np.gov.mofaga.imis.shared.projection

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import np.gov.mofaga.imis.shared.enums.EntityField
import np.gov.mofaga.imis.shared.serializer.CustomProjectionSerializer

@JsonSerialize(using = CustomProjectionSerializer::class)
interface EntityProjection<F : EntityField> {
    fun getValue(field: F): Any?

    fun getRawData(): Map<String, Any?>
}
