package np.gov.mofaga.imis.shared.projection

import np.gov.mofaga.imis.shared.enums.EntityField

interface EntityProjection<F : EntityField> {
    fun getValue(field: F): Any?

    fun getRawData(): Map<String, Any?>
}
