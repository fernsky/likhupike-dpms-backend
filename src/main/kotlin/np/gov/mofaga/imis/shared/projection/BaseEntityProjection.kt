package np.gov.mofaga.imis.shared.projection

import np.gov.mofaga.imis.shared.enums.EntityField
import org.slf4j.LoggerFactory

abstract class BaseEntityProjection<T, F : EntityField> : EntityProjection<F> {
    protected val data: MutableMap<String, Any?> = mutableMapOf()

    companion object {
        private val logger = LoggerFactory.getLogger(BaseEntityProjection::class.java)
    }

    protected fun addField(field: F, value: Any?) {
        value?.let {
            data[field.toPropertyName()] = it
            logger.debug("Added field ${field.toPropertyName()} with value: $it")
        }
    }

    override fun getValue(field: F): Any? = data[field.toPropertyName()]
    
    override fun getRawData(): Map<String, Any?> = data.toMap()

    override fun toString(): String = "${this::class.simpleName}(data=$data)"

    protected abstract fun populateFields(entity: T, fields: Set<F>)
}
